package GestorTemperaturas;
import java.io.*;
import java.nio.file.*;
import java.util.*;


import Entidades.TemperaturaRecord;

public class GestorTemperaturas {
   private List<TemperaturaRecord> registros = new ArrayList<>();

    public void cargarDesdeArchivo(String rutaArchivo) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    String ciudad = datos[0];
                    String fecha = datos[1];
                    double temp = Double.parseDouble(datos[2]);
                    registros.add(new TemperaturaRecord(ciudad, fecha, temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarEnArchivo(String rutaArchivo) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(rutaArchivo))) {
            for (TemperaturaRecord r : registros) {
                bw.write(r.getCiudad() + "," + r.getFecha() + "," + r.getTemperatura());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ciudadExtremosPorFecha(String fecha) {
    double maxTemp = Double.NEGATIVE_INFINITY;
    double minTemp = Double.POSITIVE_INFINITY;
    String ciudadMax = "";
    String ciudadMin = "";

    for (TemperaturaRecord r : registros) {
        if (r.getFecha().equals(fecha)) {
            if (r.getTemperatura() > maxTemp) {
                maxTemp = r.getTemperatura();
                ciudadMax = r.getCiudad();
            }
            if (r.getTemperatura() < minTemp) {
                minTemp = r.getTemperatura();
                ciudadMin = r.getCiudad();
            }
        }
    }

    System.out.println("Ciudad más calurosa: " + ciudadMax + " con " + maxTemp + "°");
    System.out.println("Ciudad menos calurosa: " + ciudadMin + " con " + minTemp + "°");
}


    
}