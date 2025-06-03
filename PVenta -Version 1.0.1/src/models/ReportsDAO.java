package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ReportsDAO {

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

public List<ReportsVenta> listAllSalesByDate(String startDate, String endDate) {
    List<ReportsVenta> listSales = new ArrayList<>();
    String query = "SELECT s.id AS invoice, c.fullname AS customer, e.full_name AS employee, s.total, s.sale_date " +
                   "FROM sales s " +
                   "INNER JOIN customers c ON s.customer_id = c.id " +
                   "INNER JOIN employees e ON s.employee_id = e.id " +
                   "WHERE s.sale_date BETWEEN ? AND ? ORDER BY s.id ASC";

    try {
        conn = cn.getConnection();
        pst = conn.prepareStatement(query);
        
        pst.setString(1, startDate);
        pst.setString(2, endDate);
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ReportsVenta report = new ReportsVenta();
                report.setId(rs.getInt("invoice"));
                report.setCustomer_name(rs.getString("customer"));
                report.setEmployee_name(rs.getString("employee"));
                report.setTotal_to_pay(rs.getDouble("total"));
                report.setSale_date(rs.getString("sale_date"));
                listSales.add(report);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al obtener ventas: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    return listSales;
}

    public List<ReportsCompra> listPurchasesByDate(String startDate, String endDate) {
        List<ReportsCompra> list_purchase = new ArrayList<>();

        String query = "SELECT pu.id, pu.total, pu.created, su.name AS supplier_name, e.full_name AS employee_name " +
                       "FROM purchases pu " +
                       "JOIN suppliers su ON pu.supplier_id = su.id " +
                       "JOIN employees e ON pu.employee_id = e.id " +
                       "WHERE pu.created BETWEEN ? AND ? ORDER BY pu.id ASC";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, startDate);
            pst.setString(2, endDate);
            rs = pst.executeQuery();

            while (rs.next()) {
                ReportsCompra reports = new ReportsCompra();
                reports.setId(rs.getInt("id"));
                reports.setSupplier_name(rs.getString("supplier_name"));  // Necesitas agregar este atributo en tu modelo Reports
                reports.setEmployee_name(rs.getString("employee_name"));
                reports.setTotal_to_pay(rs.getDouble("total"));
                reports.setCreated(rs.getString("created"));

                list_purchase.add(reports);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return list_purchase;
    }
}