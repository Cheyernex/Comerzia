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

public class EmployeesDAO {
    //Instanciar la conexión

    ConnectionMySQL cn = new ConnectionMySQL();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //Variables para enviar datos entre interfaces
    public static int id_user = 0;
    public static String full_name_user = "";
    public static String username_user = "";
    public static String address_user = "";
    public static String telephone_user = "";
    public static String email_user = "";
    public static String rol_user = "";

    //Metodo de login
    public Employees loginQuery(String user, String password) {
        String query = "SELECT * FROM employees WHERE username = ? AND password = ?";
        Employees employee = new Employees();

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);

            //Enviar parámetros
            pst.setString(1, user);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                employee.setId(rs.getInt("id"));
                id_user = employee.getId();

                employee.setFull_name(rs.getString("full_name"));
                full_name_user = employee.getFull_name();

                employee.setUsername(rs.getString("username"));
                username_user = employee.getUsername();

                employee.setAddress(rs.getString("address"));
                address_user = employee.getAddress();

                employee.setTelephone(rs.getString("telephone"));
                telephone_user = employee.getTelephone();

                employee.setEmail(rs.getString("email"));
                email_user = employee.getEmail();

                employee.setRol(rs.getString("rol"));
                rol_user = employee.getRol();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener al empleado " + e);
        }

        return employee;
    }

    //Registrar empleado
    public boolean registerEmployeeQuery(Employees employee) {
        String query = "INSERT INTO employees(id, full_name, username, address, telephone, email, password, rol, created, updated) VALUES(?,?,?,?,?,?,?,?,?,?)";
        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, employee.getId());
            pst.setString(2, employee.getFull_name());
            pst.setString(3, employee.getUsername());
            pst.setString(4, employee.getAddress());
            pst.setString(5, employee.getTelephone());
            pst.setString(6, employee.getEmail());
            pst.setString(7, employee.getPassword());
            pst.setString(8, employee.getRol());
            pst.setTimestamp(9, datetime);
            pst.setTimestamp(10, datetime);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar al empleado" + e);
            return false;
        }
    }
    
    public List<Employees> listEmployeeQuery(String value) {
        List<Employees> list_employees = new ArrayList<>();
        String query = "SELECT * FROM employees ORDER BY rol ASC";
        String query_search_employee = "SELECT * FROM employees WHERE "
                + "CAST(id AS CHAR) LIKE ? OR "
                + "full_name LIKE ? OR "
                + "username LIKE ? OR "
                + "address LIKE ? OR "
                + "telephone LIKE ? OR "
                + "email LIKE ? OR "
                + "rol LIKE ? "
                + "ORDER BY rol ASC";

        try {
            conn = cn.getConnection();
            if (value == null || value.trim().isEmpty()) {
                pst = conn.prepareStatement(query);
            } else {
                pst = conn.prepareStatement(query_search_employee);
                String filter = "%" + value + "%";
                // Establece el mismo filtro para todos los campos
                for (int i = 1; i <= 7; i++) {
                    pst.setString(i, filter);
                }
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Employees employee = new Employees();
                employee.setId(rs.getInt("id"));
                employee.setFull_name(rs.getString("full_name"));
                employee.setUsername(rs.getString("username"));
                employee.setAddress(rs.getString("address"));
                employee.setTelephone(rs.getString("telephone"));
                employee.setEmail(rs.getString("email"));
                employee.setRol(rs.getString("rol"));
                list_employees.add(employee);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return list_employees;
    }

    //Modificar empleado
    public boolean updateEmployeeQuery(Employees employee) {
        String query = "UPDATE employees SET full_name = ?, username = ?, password = ?, address = ?, telephone = ?, email = ?, rol = ?, updated = ? WHERE id = ?";
        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, employee.getFull_name());
            pst.setString(2, employee.getUsername());
            pst.setString(3, employee.getPassword());
            pst.setString(4, employee.getAddress());
            pst.setString(5, employee.getTelephone());
            pst.setString(6, employee.getEmail());
            pst.setString(7, employee.getRol());
            pst.setTimestamp(8, datetime);
            pst.setInt(9, employee.getId());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar los datos del empleado" + e);
            return false;
        }
    }

    //Eliminar Empleados
    public boolean deleteEmployeeQuery(int id) {
        String query = "DELETE FROM employees WHERE id = ?";
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, id); // Asigna el valor del parámetro
            int rowsAffected = pst.executeUpdate(); // Ejecuta la eliminación

            return rowsAffected > 0; // Devuelve true si se eliminó alguna fila
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "No puedes eliminar un empleado que tenga relación con otra tabla.\nDetalles: " + e.getMessage(),
                    "Error al eliminar",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    //Cambiar la contraseña
    public boolean updateEmployeePassword(Employees employee) {
        String query = "UPDATE employees SET password = ? WHERE username ='" + username_user + "'";
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, employee.getPassword());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showInputDialog(null, "Ha ocurrido un error al modificar la contraseñá" + e);
            return false;
        }
    }
}
