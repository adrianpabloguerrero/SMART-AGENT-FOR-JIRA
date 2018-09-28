package core;

import core.Tarea;
import java.util.ArrayList;

public class Administrador {

    public ArrayList<Tarea> tareasLista = new ArrayList<>();
    public ArrayList<Tarea> tareasAnalisis = new ArrayList<>();
    public ArrayList<Float> historico_M = new ArrayList<>();
    public float media_actual;
    public float varianza_actual;
    public ArrayList<Tarea> malEstimadas = new ArrayList<>();
    public ArrayList<Tarea> menosEstimadas = new ArrayList<>();
    public ArrayList<Tarea> sinHorasEstimadas = new ArrayList<>();
    public ArrayList<Tarea> sinHorasRegistradas = new ArrayList<>();

    public Administrador(ArrayList<Tarea> tareasLista, ArrayList<Tarea> tareasAnalisis) {
        this.tareasLista = tareasLista;
        this.tareasAnalisis = tareasAnalisis;
        //System.out.println("Lista de tareas: " + tareasLista.size());
        //System.out.println("Tareas de analisis: " + tareasAnalisis.size());
        this.CalcularMedia();
        this.calcularVarianza();
        this.obtenerTareasMalEstimadas();
        this.obtenerTareasMenosEstimadas();
        this.obtenerTareasSinHorasEstimadas();
        this.obtenerTareasSinHorasRegistradas();
    }

    public void CalcularMedia() {
        float valor_relativo = 0;
        int count = 0;
        for (Tarea i : tareasLista) {
            if (i.getHorasEstimadas() == 0 || i.getHorasReales() == 0) {
                count += 1;
            } else {
                valor_relativo += i.getErrorRelativo();
            }
        }
        media_actual = valor_relativo / (tareasLista.size() - count);
        historico_M.add(media_actual);
    }

    public void calcularVarianza() {
        float result = 0;
        int count = 0;
        for (Tarea i : tareasLista) {
            if (i.getHorasEstimadas() == 0 || i.getHorasReales() == 0) {
                count += 1;
            } else {
                result += (i.getErrorRelativo() - media_actual) * (i.getErrorRelativo() - media_actual);
            }
        }
        varianza_actual = result / (tareasLista.size() - count);
    }

    public double getDesvio() {
        return Math.sqrt(varianza_actual);
    }

    public int esProblematica(Tarea tarea) {
        //me devuelve  1 si estimaste de mas
        //me devuelve -1 si estime de menos
        if (tarea.getHorasEstimadas() != 0 && tarea.getHorasReales() != 0) {
            if (tarea.getErrorRelativo() + media_actual > media_actual + this.getDesvio()) {
                return 1;
            } else if (tarea.getErrorRelativo() + media_actual < media_actual - this.getDesvio()) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void obtenerTareasMalEstimadas() {
        ArrayList<Tarea> lista_resultado = new ArrayList<>();
        for (Tarea i : tareasAnalisis) {
            if (this.esProblematica(i) == 1) {
                lista_resultado.add(i);
            }
        }
        setMalEstimadas(lista_resultado);
    }

    public void obtenerTareasMenosEstimadas() {
        ArrayList<Tarea> lista_resultado = new ArrayList<>();
        for (Tarea i : tareasAnalisis) {
            if (this.esProblematica(i) == -1) {
                lista_resultado.add(i);
            }
        }
        setMenosEstimadas(lista_resultado);

    }

    public void obtenerTareasSinHorasEstimadas() {
        ArrayList<Tarea> lista_resultado = new ArrayList<>();
        for (Tarea i : tareasAnalisis) {
            if (i.getHorasEstimadas() == 0) {
                lista_resultado.add(i);
            }
        }
        setSinHorasEstimadas(lista_resultado);
    }

    public void obtenerTareasSinHorasRegistradas() {
        ArrayList<Tarea> lista_resultado = new ArrayList<>();
        for (Tarea i : tareasAnalisis) {
            if (i.getHorasReales() == 0) {
                lista_resultado.add(i);
            }
        }
        setSinHorasRegistradas(lista_resultado);
    }

    public ArrayList<Tarea> getMalEstimadas() {
        return malEstimadas;
    }

    public void setMalEstimadas(ArrayList<Tarea> malEstimadas) {
        this.malEstimadas = malEstimadas;
    }

    public ArrayList<Tarea> getMenosEstimadas() {
        return menosEstimadas;
    }

    public void setMenosEstimadas(ArrayList<Tarea> menosEstimadas) {
        this.menosEstimadas = menosEstimadas;
    }

    public ArrayList<Tarea> getSinHorasEstimadas() {
        return sinHorasEstimadas;
    }

    public void setSinHorasEstimadas(ArrayList<Tarea> sinHorasEstimadas) {
        this.sinHorasEstimadas = sinHorasEstimadas;
    }

    public ArrayList<Tarea> getSinHorasRegistradas() {
        return sinHorasRegistradas;
    }

    public void setSinHorasRegistradas(ArrayList<Tarea> sinHorasRegistradas) {
        this.sinHorasRegistradas = sinHorasRegistradas;
    }

    public float getMediaTiempoEstimado() {
        //Que se calcule la media del tiempo estimado y se muestre en algun lado tipo indicador 
        float sum = 0;
        for (Tarea t : tareasLista) {
            sum = t.getHorasEstimadas();
        }
        return (sum / tareasLista.size());
    }

    public float getMediaTiempoReal() {
        //idem anterior con el tiempo real, para indicar de que el equipo esta trabajando a tal ritmo en verdad
        float sum = 0;
        for (Tarea t : tareasLista) {
            sum = t.getHorasReales();
        }
        return (sum / tareasLista.size());
    }
}
