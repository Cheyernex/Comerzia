package controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.ReportsCompra;
import models.ReportsVenta;
import models.ReportsDAO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import views.ReportViewer;

public class ReportsController {

    private final ReportViewer view;
    private final ReportsDAO reportDAO;
    private final DefaultTableModel model;

    public ReportsController(ReportViewer view, ReportsDAO reportDAO) {
        this.view = view;
        this.reportDAO = reportDAO;
        this.model = (DefaultTableModel) view.table_all_sales.getModel();
        initController();
        setupTableModel();
    }

    private void initController() {
        // Hacer el JLabel clickeable para buscar reporte
        view.jLabelSearchReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        view.jLabelSearchReport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchReport();
            }
        });

        // Hacer el JLabel clickeable para imprimir reporte
        view.jLabel_print_report.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        view.jLabel_print_report.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                generateJasperReport();
            }
        });
    }

    private void setupTableModel() {
        model.setColumnIdentifiers(new Object[]{
            "Factura", "Cliente/Proveedor", "Empleado", "Total", "Fecha"
        });
    }

    private boolean areDatesValid() {
        Date initialDate = view.jdc_initial_date.getDate();
        Date finalDate = view.jdc_final_date.getDate();

        if (initialDate == null || finalDate == null) {
            JOptionPane.showMessageDialog(view, "Por favor seleccione ambas fechas.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (finalDate.before(initialDate)) {
            JOptionPane.showMessageDialog(view, "La fecha final debe ser mayor o igual a la fecha inicial.",
                    "Error en fechas", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void searchReport() {
        try {
            if (!areDatesValid()) {
                return;
            }

            String reportType = view.cmb_list_report.getSelectedItem().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = sdf.format(view.jdc_initial_date.getDate());
            String endDate = sdf.format(view.jdc_final_date.getDate());

            model.setRowCount(0); // Limpiar tabla

            if (reportType.equals("Reporte de Ventas")) {
                loadSalesData(startDate, endDate);
            } else if (reportType.equals("Reporte de Compras")) {
                loadPurchasesData(startDate, endDate);
            } else {
                JOptionPane.showMessageDialog(view, "Seleccione un tipo de reporte válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error al generar el reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadSalesData(String startDate, String endDate) {
        List<ReportsVenta> list = reportDAO.listAllSalesByDate(startDate, endDate);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No se encontraron registros de ventas en el rango de fechas seleccionado.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (ReportsVenta r : list) {
            model.addRow(new Object[]{
                r.getInvoice(),
                r.getCustomer_name(),
                r.getEmployee_name(),
                r.getTotal(),
                r.getSale_date()
            });
        }
    }

    private void loadPurchasesData(String startDate, String endDate) {
        List<ReportsCompra> list = reportDAO.listPurchasesByDate(startDate, endDate);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No se encontraron registros de compras en el rango de fechas seleccionado.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (ReportsCompra rc : list) {
            model.addRow(new Object[]{
                rc.getInvoice(),
                rc.getSupplier_name(),
                rc.getEmployee_name(),
                rc.getTotal(),
                rc.getCreated()
            });
        }
    }

private void generateJasperReport() {
    try {
        if (!areDatesValid()) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(view.jdc_initial_date.getDate());
        String endDate = sdf.format(view.jdc_final_date.getDate());
        String reportType = view.cmb_list_report.getSelectedItem().toString();

        List<Map<String, Object>> reportData = new ArrayList<>();
        InputStream reportStream = null;

        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(view,
                    "No hay datos para generar el reporte.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalGeneral = 0.0;

        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> row = new HashMap<>();
            Object totalValue = model.getValueAt(i, 3); // Columna "Total"

            if (reportType.equals("Reporte de Ventas")) {
                row.put("invoice", model.getValueAt(i, 0));
                row.put("customer", model.getValueAt(i, 1));
                row.put("employee", model.getValueAt(i, 2));
                row.put("total", totalValue);
                row.put("date", model.getValueAt(i, 4));
            } else if (reportType.equals("Reporte de Compras")) {
                row.put("invoice", model.getValueAt(i, 0));
                row.put("supplier", model.getValueAt(i, 1));
                row.put("employee", model.getValueAt(i, 2));
                row.put("total", totalValue);
                row.put("date", model.getValueAt(i, 4));
            } else {
                JOptionPane.showMessageDialog(view, "Seleccione un tipo de reporte válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sumar totales
            if (totalValue instanceof Number) {
                totalGeneral += ((Number) totalValue).doubleValue();
            } else {
                try {
                    totalGeneral += Double.parseDouble(totalValue.toString());
                } catch (NumberFormatException ex) {
                    System.err.println("Error al convertir total en fila " + i + ": " + ex.getMessage());
                }
            }

            reportData.add(row);
        }

        // Cargar el archivo .jasper según tipo de reporte
        if (reportType.equals("Reporte de Ventas")) {
            reportStream = getClass().getResourceAsStream("/reports/salesGeneralReport.jasper");
        } else if (reportType.equals("Reporte de Compras")) {
            reportStream = getClass().getResourceAsStream("/reports/purchasesGeneralReport.jasper");
        }

        if (reportStream == null) {
            JOptionPane.showMessageDialog(view, "No se encontró el archivo de reporte.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JRDataSource dataSource = new JRBeanArrayDataSource(reportData.toArray());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ds", dataSource);
        parameters.put("start_date", startDate);
        parameters.put("end_date", endDate);
        parameters.put("totalCompras", Double.valueOf(totalGeneral));


        // Cargar logo si existe
        InputStream logoStream = getClass().getResourceAsStream("/main/resources/images/DalvasandLogo.png");
        if (logoStream != null) {
            parameters.put("logoEmpresa", logoStream);
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        JasperViewer viewer = new JasperViewer(jasperPrint, false);
        viewer.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(view, "Error al generar reporte: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
   
}