package controllers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class GenerateSalesReport {

    public void generarReporteVenta(int id, String fechaHora, String vendedor, String cliente, double total, List<Map<String, Object>> items) {
        
        try {
            // 1. Cargar el diseño del reporte
            InputStream reporte = getClass().getResourceAsStream("/reports/sale_report.jasper");
            if (reporte == null) {
                throw new Exception("No se encontró el archivo de reporte");
            }

            InputStream logo = getClass().getResourceAsStream("/main/resources/images/DalvasandLogo.png");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);

            // 2. Convertir los items a objetos DetalleVenta
            DetalleVenta[] detalles = new DetalleVenta[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> item = items.get(i);

                detalles[i] = new DetalleVenta(
                        convertToInt(item.get("product_id")),
                        convertToString(item.get("product_name")),
                        convertToDouble(item.get("sale_price")),
                        convertToInt(item.get("sale_quantity")),
                        convertToDouble(item.get("sale_subtotal")),
                        convertToInt(item.get("sale_amount"))
                );
            }

            // 3. Configurar parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("logoEmpresa", logo);
            parametros.put("dss", new JRBeanArrayDataSource(detalles));
            parametros.put("id", String.valueOf(id));
            parametros.put("seller", vendedor);
            parametros.put("customer", cliente);
            parametros.put("total", total);
            parametros.put("created", fechaHora);

            // 4. Generar y mostrar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Reporte de Venta");
            viewer.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al generar reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para conversión segura
    private int convertToInt(Object value) throws Exception {
        if (value == null) {
            throw new Exception("Valor nulo no puede convertirse a entero");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new Exception("Error al convertir a entero: " + value);
        }
    }

    private double convertToDouble(Object value) throws Exception {
        if (value == null) {
            throw new Exception("Valor nulo no puede convertirse a double");
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new Exception("Error al convertir a double: " + value);
        }
    }

    private String convertToString(Object value) throws Exception {
        if (value == null) {
            throw new Exception("Valor nulo no puede convertirse a texto");
        }
        return value.toString();
    }

    // Clase para representar los detalles de venta
    public static class DetalleVenta {

        private final int id;
        private final String productName; // Cambiado de product_name a productName
        private final double price;
        private final int quantity;
        private final double subtotal;
        private final double amount;

        public DetalleVenta(int id, String productName, double price,
                int quantity, double subtotal, double amount) {
            this.id = id;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = subtotal;
            this.amount = amount;
        }

        // Getters - Los nombres deben coincidir EXACTAMENTE con lo que espera Jasper
        public int getId() {
            return id;
        }

        public String getProductName() {
            return productName;
        } // getProductName() en lugar de getProduct_name()

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getAmount() {
            return amount;
        }
    }
}
