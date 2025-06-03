package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import models.Employees;
import models.EmployeesDAO;
import views.LoginView;
import views.SystemView;

public class LoginController implements ActionListener, KeyListener{
    private Employees employee;
    private EmployeesDAO employee_dao;
    private LoginView login_view;

    public LoginController(Employees employee, EmployeesDAO employee_dao, LoginView login_view) {
        this.employee = employee;
        this.employee_dao = employee_dao;
        this.login_view = login_view;
        this.login_view.btn_enter.addActionListener(this);
        this.login_view.txt_username.addKeyListener(this);
        this.login_view.txt_password.addKeyListener(this);
        
                
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Obtener datos de la vista
        String user = login_view.txt_username.getText().trim();
        String pass = String.valueOf(login_view.txt_password.getPassword());
        
        if(e.getSource() == login_view.btn_enter){
            //validar que los campos no estan vacios
            if(!user.equals("") || !pass.equals("")){
                //pasar los parametros al metodo login
                employee = employee_dao.loginQuery(user, pass);
                //verificar la existencia del usuario
                if(employee.getUsername()!= null){
                    if(employee.getRol().equals("Administrador")){
                        SystemView admin = new SystemView();
                        admin.setVisible(true);
                    } else{
                        SystemView aux = new SystemView();
                        aux.setVisible(true);
                    }
                    this.login_view.dispose();
                } else{
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecto");
                }
            }else{
                JOptionPane.showMessageDialog(null, "Los campos estan vacios");
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == login_view.txt_username) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                login_view.txt_password.requestFocus(); // Pasa al campo de contraseña
            }
        } else if (e.getSource() == login_view.txt_password) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                login_view.btn_enter.doClick(); // Simula clic en el botón "Acceder"
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    
}
