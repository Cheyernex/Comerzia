package models;

import controllers.GenerateSalesReport.DetalleVenta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class SalesDAO {
    //Instanciar la conexión

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //Registrar compras
    public boolean registerSaleQuery(int customer_id, int employee_id, double total) {

        String query = "INSERT INTO sales (customer_id, employee_id, total, sale_date) VALUES (?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, customer_id);
            pst.setInt(2, employee_id);
            pst.setDouble(3, total);
            pst.setTimestamp(4, datetime);
            pst.execute();

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    //Registrar detalles de compra
    public boolean registerSaleDetailQuery(int sale_id, int product_id, int sale_quantity, double sale_price, double sale_subtotal) {
        String query = "INSERT INTO sales_details (sale_id, product_id, sale_quantity, sale_price, sale_subtotal) VALUES (?,?,?,?,?)";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, sale_id);
            pst.setInt(2, product_id);
            pst.setInt(3, sale_quantity);
            pst.setDouble(4, sale_price);
            pst.setDouble(5, sale_subtotal);
            pst.execute();

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar detalle de venta: " + e.getMessage());
            return false;
        }
    }

    //obtener ventas
    public int saleId() {
        int id = 0;
        String query = "SELECT MAX(id) AS id FROM sales";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.execute();

            if (rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return id;
    }

    public List listAllSalesQuery() {

        List<Sales> list_sales = new ArrayList();

        String query = "SELECT s.id AS invoice, c.fullname AS customer, e.full_name AS employee, s.total, s.sale_date FROM sales s "
                + "INNER JOIN customers c ON s.customer_id = c.id INNER JOIN employees e ON s.employee_id = e.id ORDER BY s.id ASC";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                Sales sale = new Sales();
                sale.setId(rs.getInt("invoice"));
                sale.setCustomer_name(rs.getString("customer"));
                sale.setEmployee_name(rs.getString("employee"));
                sale.setTotal_to_pay(rs.getDouble("total"));
                sale.setSale_date(rs.getString("sale_date"));

                list_sales.add(sale);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return list_sales;
    }

    public boolean registerSaleDetailsBacth(int saleId, List<DetalleVenta> detalles) {
        String query = "INSERT INTO sales_details (sale_id, product_id, sale_price, sale_quantity, sale_subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);

            // Asumo que tienes una lista llamada 'detalles'
            for (DetalleVenta detalle : detalles) {  // Bucle foreach correcto
                pst.setInt(1, detalle.getId());  // Índice normalmente empieza en 1
                pst.setDouble(2, detalle.getPrice());
                pst.setInt(3, detalle.getQuantity());
                pst.setDouble(4, detalle.getSubtotal());
                pst.addBatch();
            }

            pst.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCustomerNameBySellerId(int sellerId) {
        String sellerName = "Cliente no encontrado";
        String query = "SELECT c.fullname FROM customers c \n"
                + "JOIN sales s ON c.id = s.customer_id \n"
                + "WHERE s.employee_id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, sellerId);
            rs = pst.executeQuery();

            if (rs.next()) {
                sellerName = rs.getString("fullname");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombre del cliente: " + e.getMessage());
        }

        return sellerName;
    }

    public String getEmployeeNameById(int employeeId) {
        String employeeName = "Empleado no encontrado";
        String query = "SELECT full_name FROM employees WHERE id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, employeeId);
            rs = pst.executeQuery();

            if (rs.next()) {
                employeeName = rs.getString("full_name");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombre de empleado: " + e.getMessage());
        }
        return employeeName;
    }

    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando recursos: " + e.getMessage());
        }
    }

    public boolean registerSaleQuery(Connection conn, int customer_id, int employee_id, double total) throws SQLException {
        String query = "INSERT INTO sales (customer_id, employee_id, total, sale_date) VALUES (?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());

        try (
                PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, customer_id);
            pst.setInt(2, employee_id);
            pst.setDouble(3, total);
            pst.setTimestamp(4, datetime);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean updateStockQuery(Connection conn, int product_id, int new_quantity) throws SQLException {
        String query = "UPDATE products SET product_quantity = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, new_quantity);
            pst.setInt(2, product_id);
            return pst.executeUpdate() > 0;
        }
    }
    

    public String getCustomerNameBySaleId(int saleId) {
        String customerName = "Cliente no encontrado";

        // Consulta mejorada que:
        // 1. Une las tablas sales y customers
        // 2. Usa nombres de columnas consistentes
        String query = "SELECT c.fullname FROM sales s JOIN customers c ON s.customer_id = c.id WHERE s.id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, saleId);
            rs = pst.executeQuery();

            // Mensaje de diagnóstico
            System.out.println("Buscando cliente para venta ID: " + saleId);

            if (rs.next()) {
                customerName = rs.getString("fullname");
                System.out.println("Cliente encontrado: " + customerName);
            } else {
                System.out.println("No se encontró cliente para esta venta");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en getCustomerNameBySaleId: " + e.getMessage());
            e.printStackTrace();
        }
        return customerName;
    }
    
    public int getLastSaleId() {
        int lastId = 0;
        String query = "SELECT MAX(id) FROM sales";
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo último ID: " + e.getMessage());
        }
        return lastId;
    }
    
}
