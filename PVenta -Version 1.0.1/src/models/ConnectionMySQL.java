package models;
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.view.JasperViewer;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;

public class ConnectionMySQL {
//declaración de variables de acceso a DB

    private String database_name = "railway";
    private String user = "root";
    private String password = "bKGtGuCQZmEAiHKzZlrFOzvYRkSKbHYS";
    private String url = "jdbc:mysql://yamabiko.proxy.rlwy.net:41844/" + database_name;
    Connection conn = null;

    public Connection getConnection() {
        try {
            //Obtener valor del driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Obtener la conexión
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Ha ocurrido un ClassNotFoundException " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Ha ocurrido un SQLException " + e.getMessage());
        }
        return conn;
    }
}