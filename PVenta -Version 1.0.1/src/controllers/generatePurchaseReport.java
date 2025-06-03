package controllers;

import java.io.InputStream;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.util.JRLoader;

import java.util.*;
import javax.swing.JOptionPane;

public class generatePurchaseReport {

    public void generarReporteCompra(int id, String fechaHora, String proveedor, String comprador,
            double total, List<Map<String, Object>> items) {

        try {

            // 1. Convertir los items (Map) a objetos DetalleCompra[] (igual que antes)
            DetalleCompra[] detalles = new DetalleCompra[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> item = items.get(i);
                detalles[i] = new DetalleCompra(
                        (String) item.get("product_name"),
                        Double.valueOf(item.get("purchase_price").toString()),
                        Integer.valueOf(item.get("purchase_amount").toString()),
                        Double.valueOf(item.get("purchase_subtotal").toString())
                );
            }

            // 2. Cargar el reporte compilado (.jasper) (igual que antes)
            InputStream reporte = getClass().getResourceAsStream("/reports/purchase_report.jasper");
            InputStream logo = getClass().getResourceAsStream("/main/resources/images/DalvasandLogo.png");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);

            // 3. Fuente de datos del reporte (igual que antes)
            JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(detalles);

            // 4. Parámetros para el reporte - CAMBIO CLAVE AQUÍ
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("logoEmpresa", logo);
            parametros.put("ds", dataSource);
            parametros.put("id", String.valueOf(id));
            parametros.put("supplier", proveedor);
            parametros.put("purcharser", comprador);
            // Formatear el total como String directamente en Java
            parametros.put("total", total); // Esto asegura que sea String

            parametros.put("created", fechaHora);

            // 5. Llenar el reporte (igual que antes)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

            // 6. Mostrar el reporte (igual que antes)
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Reporte de Compra");
            viewer.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el reporte", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class DetalleCompra {
        private String product_name;
        private Double purchase_price;
        private Integer purchase_amount;
        private Double purchase_subtotal;

        public DetalleCompra(String product_name, Double purchase_price, Integer purchase_amount, Double purchase_subtotal) {
            this.product_name = product_name;
            this.purchase_price = purchase_price;
            this.purchase_amount = purchase_amount;
            this.purchase_subtotal = purchase_subtotal;
        }

        public String getProduct_name() { return product_name; }
        public Double getPurchase_price() { return purchase_price; }
        public Integer getPurchase_amount() { return purchase_amount; }
        public Double getPurchase_subtotal() { return purchase_subtotal; }
    }
}