package models;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class SuppliersDAO {
    
    //Instanciar la conexión  
    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;
    
    //Registrar proveedor
    public boolean registerSupplierQuery(Suppliers supplier){
    String query = "INSERT INTO suppliers(name, description, address, telephone, email, city, created, updated) VALUES (?,?,?,?,?,?,?,?)";
    
    Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, supplier.getName());
            pst.setString(2, supplier.getDescription());
            pst.setString(3, supplier.getAddress());
            pst.setString(4, supplier.getTelephone());
            pst.setString(5, supplier.getEmail());
            pst.setString(6, supplier.getCity());
            pst.setTimestamp(7, datetime);
            pst.setTimestamp(8, datetime);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "error al registrar al proveedor");
            return false;
        }
    }
    
    //Listar proveedores
    /*public List listSuppliersQuery(String value) {
        List<Suppliers> list_suppliers = new ArrayList();
        String query = "SELECT * FROM suppliers";
        String query_search_supplier = "SELECT * FROM suppliers where id LIKE '%" + value + "%'";
        try {
            conn = cn.getConnection();
            if (value.equalsIgnoreCase("")) {
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
            } else {
                pst = conn.prepareStatement(query_search_supplier);
                rs = pst.executeQuery();
            }

            while (rs.next()) {
                Suppliers supplier = new Suppliers();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("name"));
                supplier.setDescription(rs.getString("description"));
                supplier.setAddress(rs.getString("address"));
                supplier.setTelephone(rs.getString("telephone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setCity(rs.getString("city"));
                list_suppliers.add(supplier);

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return list_suppliers;
    }*/
    
    public List<Suppliers> listSupplierQuery(String value) {
        List<Suppliers> list_suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers ORDER BY name ASC";
        String query_search_supplier = "SELECT * FROM suppliers WHERE "
                + "CAST(id AS CHAR) LIKE ? OR "
                + "name LIKE ? OR "
                + "description LIKE ? OR "
                + "address LIKE ? OR "
                + "telephone LIKE ? OR "
                + "email LIKE ? OR "
                + "city LIKE ? "
                + "ORDER BY name ASC";
        try {
            conn = cn.getConnection();
            if (value == null || value.trim().isEmpty()) {
                pst = conn.prepareStatement(query);
            } else {
                pst = conn.prepareStatement(query_search_supplier);
                String filter = "%" + value + "%";
                for (int i = 1; i <= 7; i++) {
                    pst.setString(i, filter);
                }
            }
            rs = pst.executeQuery();
            while (rs.next()) {
                Suppliers s = new Suppliers();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setDescription(rs.getString("description"));
                s.setAddress(rs.getString("address"));
                s.setTelephone(rs.getString("telephone"));
                s.setEmail(rs.getString("email"));
                s.setCity(rs.getString("city"));

                // Convierte la fecha a string legible si setUpdated espera String
                Timestamp ts = rs.getTimestamp("updated");
                if (ts != null) {
                    String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
                    s.setUpdated(formatted);
                } else {
                    s.setUpdated(""); // o null, según cómo manejes campos vacíos
                }

                list_suppliers.add(s);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar proveedores: " + e.toString());
        }
        return list_suppliers;
    }
    
    
    //Modificar proveedor
    public boolean updateSupplierQuery(Suppliers supplier){
    String query = "UPDATE suppliers SET name = ?, description = ?, address = ?, telephone = ?, email = ?, city = ?, updated = ? WHERE id = ?";
    
    Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, supplier.getName());
            pst.setString(2, supplier.getDescription());
            pst.setString(3, supplier.getAddress());
            pst.setString(4, supplier.getTelephone());
            pst.setString(5, supplier.getEmail());
            pst.setString(6, supplier.getCity());
            pst.setTimestamp(7, datetime);
            pst.setInt(8, supplier.getId());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "error al modificar los datos del proveedor");
            return false;
        }
    }
    
    //Eliminar proveedor
    public boolean deleteSupplierQuery(int id) {
        String query = "DELETE FROM suppliers WHERE id = " + id;
        
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "No puedes eliminar un propveedor que tenga relacion con otra tabla" + e);
            return false;
        }
    }
}


