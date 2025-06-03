package models;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class PurchasesDAO {
    //Instanciar la conexión

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //registro de compras
    public boolean registerPurchaseQuery(int supplier_id, int employee_id, double total) {
        String query = "INSERT INTO purchases (supplier_id, employee_id, total, created) VALUES (?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, supplier_id);
            pst.setInt(2, employee_id);
            pst.setDouble(3, total);
            pst.setTimestamp(4, datetime);
            pst.execute();

            return true;

        } catch (SQLException e) {

            JOptionPane.showInputDialog(null, "Error al insertar la compra" + e);
            return false;

        }
    }

    //Registrar detalles de compra
    public boolean registerPurchasesDetailsQuery(int purchase_id, double purchase_price, int purchase_amount, double purchase_subtotal, int product_id) {
        String query = "INSERT INTO purchase_details (purchase_id, purchase_price, purchase_amount, purchase_subtotal, product_id) VALUES (?,?,?,?,?)";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, purchase_id);
            pst.setDouble(2, purchase_price);
            pst.setInt(3, purchase_amount);
            pst.setDouble(4, purchase_subtotal);
            pst.setInt(5, product_id);

            pst.execute();

            return true;

        } catch (SQLException e) {

            JOptionPane.showInputDialog(null, "Error al registrar los detalles de la compra" + e);
            return false;

        }
    }

    //Obtener id de la compra
    public int purchaseId() {
        int id = 0;
        String query = "SELECT MAX(id) as id FROM purchases";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return id;
    }

    //Listar todas las compras realizadas
    public List listAllPurchasesQuery() {
        List<Purchases> list_purchase = new ArrayList();
        String query = "SELECT \n"
                + "    pu.*, \n"
                + "    su.name AS supplier_name, \n"
                + "    e.full_name AS employee_name\n"
                + "FROM \n"
                + "    purchases pu\n"
                + "JOIN \n"
                + "    suppliers su ON pu.supplier_id = su.id\n"
                + "JOIN \n"
                + "    employees e ON pu.employee_id = e.id\n"
                + "ORDER BY \n"
                + "    pu.id ASC;";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            while (rs.next()) {
                Purchases purchase = new Purchases();
                purchase.setId(rs.getInt("id"));
                purchase.setSupplier_name_product(rs.getString("supplier_name"));
                purchase.setEmployee_name(rs.getString("employee_name")); // <-- nuevo
                purchase.setTotal(rs.getDouble("total"));
                purchase.setCreated(rs.getString("created"));
                list_purchase.add(purchase);
            }

            pst.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return list_purchase;
    }

    //Listar compras para imprimir facturas
    public List listPurchaseDetailsQuery(int id) {
        List<Purchases> list_purchases = new ArrayList();
        String query = "SELECT pu.created, pude.purchase_price, pude.purchase_amount, pude.purchase_subtotal, su.name AS supplier_name, pro.name AS product_name, em.full_name FROM purchases pu INNER JOIN purchase_details pude ON pu.id = pude.purchase_id INNER JOIN products pro ON pude.product_id = pro.id INNER JOIN suppliers su ON pu.supplier_id = su.id INNER JOIN employees em ON pu.employee_id = em.id WHERE pu.id = ?;";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            while (rs.next()) {
                Purchases purchase = new Purchases();
                purchase.setProduct_name(rs.getString("product_name"));
                purchase.setPurchase_amount(rs.getInt("purchase_amount"));
                purchase.setPurchase_price(rs.getDouble("purchase_price"));
                purchase.setPurchase_subtotal(rs.getDouble("purchase_subtotal"));
                purchase.setSupplier_name_product(rs.getString("supplier_name"));
                purchase.setCreated(rs.getString("created"));
                purchase.setPurcharser(rs.getString("full_name"));
                list_purchases.add(purchase);
            }
            pst.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return list_purchases;
    }

    // Método mejorado para registrar compra que devuelve el ID
    public int registerPurchaseAndGetId(int supplier_id, int employee_id, double total) {
        String query = "INSERT INTO purchases (supplier_id, employee_id, total, created) VALUES (?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());
        int generatedId = -1;

        try {
            conn = cn.getConnection();
            // Configuramos para retornar el ID generado
            pst = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setInt(1, supplier_id);
            pst.setInt(2, employee_id);
            pst.setDouble(3, total);
            pst.setTimestamp(4, datetime);

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar la compra: " + e.getMessage());
        } finally {
            closeResources();
        }
        return generatedId;
    }

// Método para obtener los datos completos de la compra
    public Purchases getPurchaseCompleteData(int purchaseId) {
        Purchases purchase = new Purchases();
        String query = "SELECT pu.*, su.name AS supplier_name, em.full_name AS purchaser_name "
                + "FROM purchases pu "
                + "INNER JOIN suppliers su ON pu.supplier_id = su.id "
                + "INNER JOIN employees em ON pu.employee_id = em.id "
                + "WHERE pu.id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, purchaseId);
            rs = pst.executeQuery();

            if (rs.next()) {
                purchase.setId(rs.getInt("id"));
                purchase.setSupplier_name_product(rs.getString("supplier_name"));
                purchase.setTotal(rs.getDouble("total"));
                purchase.setCreated(rs.getString("created"));
                purchase.setPurcharser(rs.getString("purchaser_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos de compra: " + e.getMessage());
        } finally {
            closeResources();
        }
        return purchase;
    }

// Método para cerrar recursos
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
        } finally {
            closeResources();
        }
        return employeeName;
    }

    public String getSupplierNameByPurchaseId(int purchaseId) {
        String supplierName = "Proveedor no encontrado";
        String query = "SELECT s.name FROM purchases p JOIN suppliers s ON p.supplier_id = s.id WHERE p.id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, purchaseId);
            rs = pst.executeQuery();

            if (rs.next()) {
                supplierName = rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombre del proveedor: " + e.getMessage());
        } finally {
            closeResources();
        }

        return supplierName;
    }
    private String employee_name;

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }
}
