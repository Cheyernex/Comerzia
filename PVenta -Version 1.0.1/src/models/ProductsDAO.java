package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class ProductsDAO {
    //Instanciar la conexión

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //Registrar productos
    public boolean registerProductQuery(Products product) {
        String query = "INSERT INTO products (code, name, description, unit_price, created, updated, category_id) VALUES (?,?,?,?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, product.getCode());
            pst.setString(2, product.getName());
            pst.setString(3, product.getDescription());
            pst.setDouble(4, product.getUnit_price());
            pst.setTimestamp(5, datetime);
            pst.setTimestamp(6, datetime);
            pst.setInt(7, product.getCategory_id());
            pst.execute();

            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "Error al registrar el producto" + e);
            return false;
        }
    }

    //Listar productos
    public List listProductQuerty(String value) {
        List<Products> list_product = new ArrayList();
        String query = "SELECT pro.*, ca.name as category_name FROM products pro, categories ca WHERE pro.category_id = ca.id";
        String query_search_product = "SELECT pro.*, ca.name as category_name FROM products pro INNER JOIN categories ca ON pro.category_id = ca.id LIKE '%" + value + "%'";

        try {
            conn = cn.getConnection();
            if (value.equalsIgnoreCase("")) {
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
            } else {
                pst = conn.prepareStatement(query_search_product);
                rs = pst.executeQuery();
            }

            while (rs.next()) {
                Products product = new Products();
                product.setId(rs.getInt("id"));
                product.setCode(rs.getInt("code"));
                product.setName(rs.getString("name"));
                product.setUnit_price(rs.getDouble("unit_price"));
                product.setProduct_quantity(rs.getInt("product_quantity"));
                product.setCategory_name(rs.getString("category_name"));
                list_product.add(product);

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return list_product;
    }

    //Modificar productos
    public boolean updateProductQuery(Products product) {
        String query = "UPDATE products SET code = ?, name = ?, description = ?, unit_price = ?, updated = ?, category_id = ? WHERE id = ?";
        Timestamp datetime = new Timestamp(new Date().getTime());
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, product.getCode());
            pst.setString(2, product.getName());
            pst.setString(3, product.getDescription());
            pst.setDouble(4, product.getUnit_price());
            pst.setTimestamp(5, datetime);
            pst.setInt(6, product.getCategory_id());
            pst.setInt(7, product.getId());
            pst.execute();

            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "Error al modificar el producto" + e);
            return false;
        }
    }

    //Eliminar productos
    public boolean deleteProductQuery(int id) {
        String query = "DELETE FROM products WHERE id = " + id;
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "No puedes eliminar un producto que tenga relacion con otra tabla" + e);
            return false;
        }
    }

    //Buscar producto
    public Products searchProduct(int id) {
        String query = "SELECT pro.*, ca.name as category_name FROM products pro INNER JOIN categories ca ON pro.category_id where pro.id = ?";
        Products product = new Products();

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                product.setId(rs.getInt("id"));
                product.setCode(rs.getInt("code"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setUnit_price(rs.getDouble("unit_price"));
                product.setCategory_id(rs.getInt("category_id"));
                product.setCategory_name(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return product;
    }

    //Buscar producto por codigo
public Products searchCode(int code) {
        Products product = null;
        String sql = "SELECT * FROM products WHERE id = ?";

        try (
            Connection conn = cn.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product = new Products();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }
    public Products searchCodeSale(int code) {
            Products product = null;
            String sql = "SELECT * FROM products WHERE id = ?";

            try (
                Connection conn = cn.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        product = new Products();
                        product.setId(rs.getInt("id"));
                        product.setName(rs.getString("name"));
                        product.setUnit_price(rs.getDouble("unit_price")); // Asegúrate de que este campo exista
                        product.setProduct_quantity(rs.getInt("product_quantity")); // Asegúrate de que este campo exista
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return product;
    }

    
    
    //Traer cantidad actual de productos
    public Products searchId(int id) {
        String query = "SELECT pro.product_quantity FROM products pro WHERE pro.id = ?";
        Products product = new Products(); 
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            
            if(rs.next()){
                product.setProduct_quantity(rs.getInt("product_quantity"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return product;
        }
    
//Actualizar stock
    public boolean updateStockQuerty(int amount, int product_id) {
        String query = "UPDATE products SET product_quantity = ? WHERE id = ?";
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, amount);           // cantidad a sumar
            pst.setInt(2, product_id);       // ID del producto
            pst.executeUpdate();             // ejecuta la consulta
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el stock: " + e.getMessage());
            return false;
        }
    }
    public boolean updateStockQuery(int product_id, int new_quantity) {
        String query = "UPDATE products SET product_quantity = ? WHERE id = ?";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);

            pst.setInt(1, new_quantity);
            pst.setInt(2, product_id);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}

    
    