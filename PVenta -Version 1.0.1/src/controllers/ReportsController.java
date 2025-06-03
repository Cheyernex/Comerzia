package controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.ReportsCompra;
import models.ReportsVenta;
import models.ReportsDAO;
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
        // Hacer el JLabel clickeable
        view.jLabelSearchReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        view.jLabelSearchReport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchReport();
            }
        });
    }

    private void setupTableModel() {
        model.setColumnIdentifiers(new Object[]{
            "Factura", "Cliente/Proveedor", "Empleado", "Total", "Fecha"
        });
    }

    private void searchReport() {
        try {
            String reportType = view.cmb_list_report.getSelectedItem().toString();

            // Validar que se hayan seleccionado ambas fechas
            if (view.jdc_initial_date.getDate() == null || view.jdc_final_date.getDate() == null) {
                JOptionPane.showMessageDialog(view, "Por favor seleccione ambas fechas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date initialDate = view.jdc_initial_date.getDate();
            Date finalDate = view.jdc_final_date.getDate();

            // Validar que la fecha final sea mayor o igual a la fecha inicial
            if (finalDate.before(initialDate)) {
                JOptionPane.showMessageDialog(view, "La fecha final debe ser mayor o igual a la fecha inicial.",
                        "Error en fechas", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String startDate = sdf.format(initialDate);
            String endDate = sdf.format(finalDate);

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
            e.printStackTrace(); // Para depuración
        }
    }

    private void loadSalesData(String startDate, String endDate) {
        List<ReportsVenta> list = reportDAO.listAllSalesByDate(startDate, endDate);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay ventas en el rango de fechas seleccionado.", 
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
            JOptionPane.showMessageDialog(view, "No hay compras en el rango de fechas seleccionado.", 
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
}