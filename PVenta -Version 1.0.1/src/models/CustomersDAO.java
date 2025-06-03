package models;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomersDAO {
    //Instanciar la conexión

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //Registrar cliente
    public boolean registerCustomerQuery(Customers customer) {
        String query = "INSERT INTO customers (id, fullname, address, telephone, email, created, updated) VALUES (?,?,?,?,?,?,?)";

        Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, customer.getId());
            pst.setString(2, customer.getFullname());
            pst.setString(3, customer.getAddress());
            pst.setString(4, customer.getTelephone());
            pst.setString(5, customer.getEmail());
            pst.setTimestamp(6, datetime);
            pst.setTimestamp(7, datetime);
            pst.execute();

            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "Error al registrar al cliente" + e);
            return false;
        }
    }

    //Listar cliente
    public List<Customers> listCustomerQuery(String value) {
            List<Customers> list_customers = new ArrayList<>();

            String query = "SELECT * FROM customers";
            String query_search = "SELECT * FROM customers WHERE fullname LIKE ? OR address LIKE ? OR telephone LIKE ? OR email LIKE ?";

            try {
                conn = cn.getConnection();
                if (value == null || value.trim().isEmpty()) {
                    pst = conn.prepareStatement(query);
                } else {
                    pst = conn.prepareStatement(query_search);
                    String searchPattern = "%" + value.trim() + "%";
                    pst.setString(1, searchPattern);
                    pst.setString(2, searchPattern);
                    pst.setString(3, searchPattern);
                    pst.setString(4, searchPattern);
                }

                rs = pst.executeQuery();

                while (rs.next()) {
                    Customers customer = new Customers();
                    customer.setId(rs.getInt("id"));
                    customer.setFullname(rs.getString("fullname"));
                    customer.setAddress(rs.getString("address"));
                    customer.setTelephone(rs.getString("telephone"));
                    customer.setEmail(rs.getString("email"));
                    list_customers.add(customer);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al listar clientes: " + e.getMessage());
    }

    return list_customers;
}



    //Modificar cliente
    public boolean updateCustomerQuery(Customers customer) {
        String query = "UPDATE customers SET fullname = ?, address = ?, telephone = ?, email = ?, updated = ? WHERE id = ?";

        Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, customer.getFullname());
            pst.setString(2, customer.getAddress());
            pst.setString(3, customer.getTelephone());
            pst.setString(4, customer.getEmail());
            pst.setTimestamp(5, datetime);
            pst.setInt(6, customer.getId());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "Error al modificar los datos del cliente" + e);
            return false;
        }
    }

    //Eliminar cliente
    public boolean deleteCustomerQuery(int id) {
        String query = "DELETE FROM customers WHERE id = " + id;
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "No puedes eliminar un cliente que tenga relacion con otra tabla" + e);
            return false;
        }
    }
    public List<DynamicComboBox> getCustomers() {
        List<DynamicComboBox> customersList = new ArrayList<>();
        String query = "SELECT id, full_name FROM customers";

        try (
                Connection conn = cn.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                customersList.add(new DynamicComboBox(id, name));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar clientes: " + e.getMessage());
        }

        return customersList;
    }
    public String getCustomerNameById(int id) {
        String name = null;
        String sql = "SELECT fullname FROM customers WHERE id = ?";

        try  {
            conn = cn.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("fullname");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }
    

}
