import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

import Entidades.TemperaturaRecord;
import GestorTemperaturas.GestorTemperaturas;

public class FrmTemperatura extends JFrame {
    private GestorTemperaturas  GestorTemperaturas;
    private JTable tabla;
    private DefaultTableModel modelo;
    private String[] columnas = {"Ciudad", "Fecha", "Temperatura"};
    private String archivoCSV = "src/Datos/Temperaturas.csv";

    public FrmTemperatura() {
        setTitle("GestorTemperaturas de Temperaturas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GestorTemperaturas.cargarDesdeArchivo(archivoCSV);

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        cargarDatosEnTabla();

        tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        JPanel pnlBotones = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnFiltrar = new JButton("Filtrar por rango");
        JButton btnReporte = new JButton("Reporte de rango");
        JButton btnExtremos = new JButton("Ciudad más/calor y frío");

        pnlBotones.add(btnAgregar);
        pnlBotones.add(btnEliminar);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnFiltrar);
        pnlBotones.add(btnReporte);
        pnlBotones.add(btnExtremos);

        btnAgregar.addActionListener(e -> modelo.addRow(new Object[]{"", "", ""}));

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                modelo.removeRow(fila);
            }
        });

        btnGuardar.addActionListener(e -> {
            guardarDatosDesdeTabla();
            GestorTemperaturas.guardarEnArchivo(archivoCSV);
            JOptionPane.showMessageDialog(this, "Datos guardados exitosamente.");
        });

        btnFiltrar.addActionListener(e -> {
            String fechaInicio = JOptionPane.showInputDialog(this, "Fecha inicio (dd/MM/yyyy):");
            String fechaFin = JOptionPane.showInputDialog(this, "Fecha fin (dd/MM/yyyy):");
            List<TemperaturaRecord> filtrados = GestorTemperaturas.filtrarPorFecha(fechaInicio, fechaFin);
            mostrarGraficoPromedios(filtrados);
        });

        btnReporte.addActionListener(e -> {
            String fecha = JOptionPane.showInputDialog(this, "Ingrese la fecha (dd/MM/yyyy):");
            String mensaje = GestorTemperaturas.ciudadExtremosPorFecha(fecha);
            JOptionPane.showMessageDialog(this, mensaje);
        });

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void cargarDatosEnTabla() {
        for (TemperaturaRecord r : GestorTemperaturas.getRegistros()) {
            Object[] fila = {r.getCiudad(), r.getFecha(), r.getTemperatura()};
            modelo.addRow(fila);
        }
    }

    private void guardarDatosDesdeTabla() {
        GestorTemperaturas.getRegistros().clear();
        int filas = modelo.getRowCount();
        for (int i = 0; i < filas; i++) {
            String ciudad = (String) modelo.getValueAt(i, 0);
            String fecha = (String) modelo.getValueAt(i, 1);
            double temp = Double.parseDouble(modelo.getValueAt(i, 2).toString());
            GestorTemperaturas.getRegistros().add(new TemperaturaRecord(ciudad, fecha, temp));
        }
    }

    private void mostrarGraficoPromedios(List<TemperaturaRecord> datos) {
        Map<String, List<Double>> agrupados = new HashMap<>();
        for (TemperaturaRecord r : datos) {
            agrupados.computeIfAbsent(r.getCiudad(), k -> new ArrayList<>()).add(r.getTemperatura());
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, List<Double>> entry : agrupados.entrySet()) {
            double promedio = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            dataset.addValue(promedio, "Promedio", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Promedio de Temperaturas por Ciudad",
            "Ciudad",
            "Temperatura",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Gráfico");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(chartPanel);
        
    }

   
}
