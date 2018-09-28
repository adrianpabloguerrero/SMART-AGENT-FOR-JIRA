package core;

public class Tarea {
    private int id;
    private int horasEstimadas;
    private int horasReales;
    private int numeroTarea;
    private String resumen;
    private String asignado;
    private String creador;

    Tarea(int id, int numeroTarea, String resumen, int hEstimadas, int hReales,String asignado,String creador) {
        this.id=id;
        this.numeroTarea=numeroTarea;
        this.resumen=resumen;
        this.horasEstimadas=hEstimadas;
        this.horasReales=hReales;
        this.asignado=asignado;
        this.creador=creador;
    }
    
    Tarea(int id,int hEstimadas,int hReales){
        this.id=id;
        this.horasEstimadas=hEstimadas;
        this.horasReales=hReales;
    }
    
    /*public String toString(){
        return "id: " + id + 
                ", numeroTarea: " + numeroTarea + 
                ", resumen: " + resumen +
                ", horasEstimadas: " + horasEstimadas +
                ", horasReales: " + horasReales +
                ", asignado: " + asignado +
                ", creador: " + creador;
    }*/
    
    public String toString(){
        return "\n id: " + id +
                ", horasEstimadas: " + horasEstimadas +
                ", horasReales: " + horasReales;
    }

    public int getHorasEstimadas() {
        return horasEstimadas;
    }

    public int getHorasReales() {
        return horasReales;
    }
    public String getAsignado(){
        return this.asignado;
    }
    public String getCreador(){
        return this.creador;
    }
    public int getId(){
        return this.id;
    }
    public String getResumen(){
        return this.resumen;
    }
    public int getNumeroT(){
        return this.numeroTarea;
    }
    
    /*boolean EsProblematica(float promedio) {
        if(this.getDiferencia()> promedio)
            return true;
        else
            return false;
    }*/

    public float getErrorRelativo() {
        if (horasEstimadas != 0)
            return (((float)horasEstimadas-(float)horasReales)/(float)horasEstimadas);//To change body of generated methods, choose Tools | Templates.
        else
            return 0;
    }
    
    
}
