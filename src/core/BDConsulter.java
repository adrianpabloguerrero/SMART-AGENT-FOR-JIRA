package core;

import core.Tarea;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BDConsulter {

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public BDConsulter(String url, String user, String password) {
        connection = null;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Connect to the 'prototipo' PostgreSQL database
     */
    public boolean connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            return true;
        } catch (SQLException ex) {
            System.out.println("CHEQUEAR URL, USER Y PASSWORD!!!!!");
            System.out.println("No olvidar agregar postgresql-42.2.2.jar");
            System.out.println(ex.getMessage());
            //System.exit(0);
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private ArrayList<Tarea> generarTareas(ResultSet result) throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        while (result.next()) {
            tareas.add(new Tarea(
                    result.getInt("id"),
                    result.getInt("issuenum"),
                    result.getString("summary"),
                    result.getInt("timeoriginalestimate"),
                    result.getInt("timespent"),
                    result.getString("assignes_name"),
                    result.getString("creators_name")
            ));
        }
        return tareas;
    }

    /*
    -------query tareas---------------
    obtiene: id,numero tarea(no es único),descripccion breve,
            t estimado,t real, nombre asignado, nombre creador
    tablas: jira issue, cwd_user
    filtra: por rango de fecha de creacion de tarea
    ordenado: por fecha creacion de tarea
     */
    public ArrayList<Tarea> getTareas(Date fechaInicio, Date fechaFin, String proyecto, String grupo, String integrante) {
        try {
            String query
                    = "SELECT j.id, j.issuenum, j.summary, j.timeoriginalestimate, j.timespent, \n"
                    + "	u1.display_name AS assignes_name, u2.display_name AS creators_name\n"
                    + " FROM jiraissue j JOIN cwd_user u1 ON(j.assignee=u1.user_name) \n"
                    + "	JOIN cwd_user u2 ON (j.creator=u2.user_name)\n"
                    + "	JOIN project p ON (j.project=p.id)\n"
                    + " JOIN projectroleactor pr ON (p.id = pr.pid)\n"
                    + " JOIN cwd_group cg ON (pr.roletypeparameter = cg.group_name)\n"
                    + " WHERE created BETWEEN ? AND ? \n"
                    + "	AND resolutiondate BETWEEN ? AND ? \n"
                    + "	AND p.pname = ? \n"
                    + " AND cg.group_name = ? "
                    + " AND u1.display_name LIKE ?"
                    + " ORDER BY created";

            /*preparo la sentencia y la ejecuto*/
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setDate(1, fechaInicio);
            pStatement.setDate(2, fechaFin);
            pStatement.setDate(3, fechaInicio);
            pStatement.setDate(4, fechaFin);
            pStatement.setString(5, proyecto);
            pStatement.setString(6, grupo);
            pStatement.setString(7, integrante);
            ResultSet result = pStatement.executeQuery();

            /*transformo a lista de tareas*/
            ArrayList<Tarea> tareas = generarTareas(result);

            result.close();
            pStatement.close();
            //connection.close();

            System.out.println("Cantidad de tareas recuperadas desde la base: " + tareas.size());
            return tareas;
        } catch (SQLException ex) {
            System.out.println("PROBLEMAS CON LA QUERY");
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<String> getGrupos() {
        try {
            String query
                    = "SELECT group_name \n"
                    + "FROM cwd_group \n"
                    + "ORDER BY group_name";

            /*preparo la sentencia y la ejecuto*/
            PreparedStatement pStatement = connection.prepareStatement(query);
            ResultSet result = pStatement.executeQuery();

            ArrayList<String> grupos = new ArrayList<>();
            while (result.next()) {
                grupos.add(result.getString("group_name"));
            }
            result.close();
            pStatement.close();
            //connection.close();

            System.out.println("Cantidad de grupos recuperados desde la base: " + grupos.size());
            return grupos;
        } catch (SQLException ex) {
            System.out.println("PROBLEMAS CON LA QUERY");
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<String> getProyectos(String grupo) {
        try {
            String query
                    = "SELECT p.id, p.pname, pr.roletypeparameter, cg.group_name \n"
                    + "FROM project p JOIN projectroleactor pr ON (p.id = pr.pid) \n"
                    + "JOIN cwd_group cg ON (pr.roletypeparameter = cg.group_name) \n"
                    + "WHERE cg.group_name LIKE ? ";

            /*preparo la sentencia y la ejecuto*/
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, grupo);
            ResultSet result = pStatement.executeQuery();

            ArrayList<String> proyecto = new ArrayList<>();
            while (result.next()) {
                proyecto.add(result.getString("pname"));
            }
            result.close();
            pStatement.close();
            //connection.close();

            System.out.println("Cantidad de proyectos recuperados desde la base: " + proyecto.size());
            return proyecto;
        } catch (SQLException ex) {
            System.out.println("PROBLEMAS CON LA QUERY");
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    
    /** SELECT p.id, p.pname, pr.roletypeparameter, cg.group_name, us.display_name
        FROM project p JOIN projectroleactor pr ON (p.id = pr.pid)
        JOIN cwd_group cg ON (pr.roletypeparameter = cg.group_name)
        JOIN cwd_membership mem ON (pr.roletypeparameter = mem.parent_name)
        JOIN cwd_user us ON (mem.child_name = us.user_name)
        WHERE cg.group_name LIKE 'Empáticos'
        AND p.pname LIKE ?
    **/
    public ArrayList<String> getIntegrantes(String grupo, String proyecto) {
        try {
            String query = 
                    "SELECT p.id, p.pname, pr.roletypeparameter, cg.group_name, us.display_name\n" +
                    "FROM project p JOIN projectroleactor pr ON (p.id = pr.pid)\n" +
                    "JOIN cwd_group cg ON (pr.roletypeparameter = cg.group_name)\n" +
                    "JOIN cwd_membership mem ON (pr.roletypeparameter = mem.parent_name)\n" +
                    "JOIN cwd_user us ON (mem.child_name = us.user_name)\n"+
                    "WHERE cg.group_name LIKE ?\n" +
                    "AND p.pname LIKE ?";

            /*preparo la sentencia y la ejecuto*/
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, grupo);
            pStatement.setString(2, proyecto);
            ResultSet result = pStatement.executeQuery();

            ArrayList<String> integrantes = new ArrayList<>();
            while (result.next()) {
                integrantes.add(result.getString("display_name"));
            }
            result.close();
            pStatement.close();
            //connection.close();

            System.out.println("Cantidad de integrantes recuperados desde la base: " + integrantes.size());
            return integrantes;
        } catch (SQLException ex) {
            System.out.println("PROBLEMAS CON LA QUERY");
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDConsulter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    public static void main(String[] args) {
        BDConsulter consulter = new BDConsulter(url,user,password);//genero la clase, que se conecta a la base postgresql
        consulter.connect();//establezco la conexion
        ArrayList<Tarea> tareas=consulter.getTareas(Date.valueOf("2018-1-1"),Date.valueOf("2018-7-1"));//obtengo las tareas creadas entre un rango de fecha dado
        consulter.disconnect();//termino la conexion con la base
        tareas.stream().forEach(System.out::println);//imprimo para ver que tareas hay
    }*/
}
