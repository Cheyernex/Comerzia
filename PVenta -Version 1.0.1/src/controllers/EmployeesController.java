package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Employees;
import models.EmployeesDAO;
import static models.EmployeesDAO.id_user;
import static models.EmployeesDAO.rol_user;
import views.SystemView;

public class EmployeesController implements ActionListener, MouseListener, KeyListener {

    private Employees employee;
    private EmployeesDAO employee_dao;
    private SystemView views;

    DefaultTableModel model = new DefaultTableModel();
    //Rol
    String rol = rol_user;

    //Constructor
    public EmployeesController(Employees employee, EmployeesDAO employee_dao, SystemView views) {
        this.employee = employee;
        this.employee_dao = employee_dao;
        this.views = views;

        //Eventos
        this.views.btn_register_employee.addActionListener(this);
        this.views.employees_table.addMouseListener(this);
        this.views.txt_search_employee.addKeyListener(this);
        this.views.btn_update_employee.addActionListener(this);
        this.views.btn_delete_employee.addActionListener(this);
        this.views.btn_cancel_employee.addActionListener(this);
        this.views.btn_modify_data.addActionListener(this);
        this.views.jLabelEmployees.addMouseListener(this);
        this.views.btn_foto.addMouseListener(this);

        if (rol.equals("Auxiliar")) {
            DisableEmployeeFields.disable(views);
            views.jTabbedPane1.setEnabledAt(4, false);
            views.jLabelEmployees.setEnabled(false);
        }

        this.views.txt_employee_id.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });
        this.views.txt_employee_telephone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Registrar empleado
        if (e.getSource() == views.btn_register_employee) {
            if (areFieldsEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                return;
            }
            // Asignar datos del formulario al objeto employee
            employee.setId(Integer.parseInt(views.txt_employee_id.getText().trim()));
            employee.setFull_name(views.txt_employee_fullname.getText().trim());
            employee.setUsername(views.txt_employee_username.getText().trim());
            employee.setAddress(views.txt_employee_address.getText().trim());
            employee.setTelephone(views.txt_employee_telephone.getText().trim());
            employee.setEmail(views.txt_employee_email.getText().trim());
            employee.setPassword(String.valueOf(views.txt_employee_password.getPassword()));
            employee.setRol(views.cmb_rol.getSelectedItem().toString());

            if (employee_dao.registerEmployeeQuery(employee)) {
                cleanTable();
                cleanFields();
                JOptionPane.showMessageDialog(null, "Empleado registrado");
                listAllEmployees();
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el empleado");
            }
        } // Actualizar empleado
        else if (e.getSource() == views.btn_update_employee) {
            if (views.txt_employee_id.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            } else if (areFieldsEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                // Validaciones específicas de campos importantes
                String fullName = views.txt_employee_fullname.getText().trim();
                String username = views.txt_employee_username.getText().trim();
                String password = String.valueOf(views.txt_employee_password.getPassword()).trim();

                if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El nombre completo, usuario y contraseña no pueden estar vacíos");
                    return;
                }

                int employeeIdToUpdate = Integer.parseInt(views.txt_employee_id.getText().trim());
                String selectedRol = views.cmb_rol.getSelectedItem().toString();

                // Validar que el admin no se quite su propio rol
                if (employeeIdToUpdate == id_user && rol_user.equals("Administrador") && !selectedRol.equals("Administrador")) {
                    JOptionPane.showMessageDialog(null, "No puedes cambiar tu propio rol de Administrador");
                    return;
                }

                assignFieldsToEmployee(); // Método que asigna los datos del formulario al objeto

                if (employee_dao.updateEmployeeQuery(employee)) {
                    cleanTable();
                    cleanFields();
                    listAllEmployees();
                    views.btn_register_employee.setEnabled(true);
                    JOptionPane.showMessageDialog(null, "Los datos del empleado han sido modificados");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar el empleado");
                }
            }
        } else if (e.getSource() == views.btn_delete_employee) {
            String idText = views.txt_employee_id.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar");
                return;
            }

            int id = Integer.parseInt(idText);

            // Validación: evitar que el administrador se elimine a sí mismo
            if (id == id_user && rol_user.equals("Administrador")) {
                JOptionPane.showMessageDialog(null, "No puedes eliminar tu propio usuario");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar este empleado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (employee_dao.deleteEmployeeQuery(id)) {
                    cleanTable();
                    cleanFields();
                    listAllEmployees();
                    views.btn_register_employee.setEnabled(true);
                    JOptionPane.showMessageDialog(null, "Empleado eliminado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el empleado");
                }
            }
        } // Cancelar acción
        else if (e.getSource() == views.btn_cancel_employee) {
            cleanFields();
            views.btn_register_employee.setEnabled(true);
            views.txt_employee_password.setEnabled(true);
            views.txt_employee_id.setEnabled(true);
        } // Modificar contraseña
        else if (e.getSource() == views.btn_modify_data) {
            String password = String.valueOf(views.txt_password_modify.getPassword());
            String confirmPassword = String.valueOf(views.txt_password_modify_confirm.getPassword());

            if (!password.isEmpty() && !confirmPassword.isEmpty()) {
                if (password.equals(confirmPassword)) {
                    employee.setPassword(password);

                    if (employee_dao.updateEmployeePassword(employee)) {
                        JOptionPane.showMessageDialog(null, "Contraseña modificada con éxito");
                    } else {
                        JOptionPane.showMessageDialog(null, "Ha ocurrido un error al modificar la contraseña");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            }
        }
    }

    //Listar todos los empleados//
    public void listAllEmployees() {
        if (rol.equals("Administrador")) {
            List<Employees> list = employee_dao.listEmployeeQuery(views.txt_search_employee.getText());
            model = (DefaultTableModel) views.employees_table.getModel();

            Object[] row = new Object[7];
            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getFull_name();
                row[2] = list.get(i).getUsername();
                row[3] = list.get(i).getAddress();
                row[4] = list.get(i).getTelephone();
                row[5] = list.get(i).getEmail();
                row[6] = list.get(i).getRol();

                model.addRow(row);

                views.cmb_rol.setSelectedIndex(0);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == views.employees_table) {
            if (e.getClickCount() == 2) { // Verifica doble clic
                int row = views.employees_table.rowAtPoint(e.getPoint());

                views.txt_employee_id.setText(views.employees_table.getValueAt(row, 0).toString());
                views.txt_employee_fullname.setText(views.employees_table.getValueAt(row, 1).toString());
                views.txt_employee_username.setText(views.employees_table.getValueAt(row, 2).toString());
                views.txt_employee_address.setText(views.employees_table.getValueAt(row, 3).toString());
                views.txt_employee_telephone.setText(views.employees_table.getValueAt(row, 4).toString());
                views.txt_employee_email.setText(views.employees_table.getValueAt(row, 5).toString());
                views.cmb_rol.setSelectedItem(views.employees_table.getValueAt(row, 6).toString());

                //Deshabilitar
                views.txt_employee_id.setEnabled(false);
                views.txt_employee_password.setEnabled(true);
                views.btn_register_employee.setEnabled(false);
            }

        } else if (e.getSource() == views.jLabelEmployees) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane1.setSelectedIndex(4);
                cleanTable();
                cleanFields();
                listAllEmployees();
            } else {
                JOptionPane.showMessageDialog(null, "No tienes privilegios para ver este modulo");
            }
        }
        if (e.getSource() == views.btn_foto) {
            views.jTabbedPane1.setSelectedIndex(8);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == views.txt_search_employee) {
            cleanTable();
            listAllEmployees();
        }
    }

    public void cleanFields() {
        views.txt_employee_id.setText("");
        views.txt_employee_id.setEditable(true);
        views.txt_employee_id.setEnabled(true);
        views.txt_employee_fullname.setText("");
        views.txt_employee_username.setText("");
        views.txt_employee_address.setText("");
        views.txt_employee_telephone.setText("");
        views.txt_employee_email.setText("");
        views.txt_employee_password.setText("");
        views.txt_employee_password.setEditable(true);
        views.txt_employee_password.setEnabled(true);
        views.cmb_rol.setSelectedIndex(0);
    }

    public void cleanTable() {
        model.setRowCount(0);

    }

    public class DisableEmployeeFields {

        public static void disable(SystemView view) {
            // Campos de texto
            view.txt_employee_id.setEnabled(false);
            view.txt_employee_fullname.setEnabled(false);
            view.txt_employee_username.setEnabled(false);
            view.txt_employee_address.setEnabled(false);
            view.txt_employee_telephone.setEnabled(false);
            view.txt_employee_email.setEnabled(false);
            view.txt_employee_password.setEnabled(false);
            view.txt_search_employee.setEnabled(false);

            // ComboBox
            view.cmb_rol.setEnabled(false);

            // Botones
            view.btn_register_employee.setEnabled(false);
            view.btn_update_employee.setEnabled(false);
            view.btn_delete_employee.setEnabled(false);
            view.btn_cancel_employee.setEnabled(false);

            view.jTabbedPane1.setEnabledAt(4, false);
            view.jLabelEmployees.setEnabled(false);
        }
    }

    public boolean areFieldsEmpty() {
        return views.txt_employee_id.getText().trim().isEmpty()
                || views.txt_employee_fullname.getText().trim().isEmpty()
                || views.txt_employee_username.getText().trim().isEmpty()
                || views.txt_employee_address.getText().trim().isEmpty()
                || views.txt_employee_telephone.getText().trim().isEmpty()
                || views.cmb_rol.getSelectedItem() == null
                || views.cmb_rol.getSelectedItem().toString().trim().isEmpty()
                || String.valueOf(views.txt_employee_password.getPassword()).trim().isEmpty();
    }

    public void assignFieldsToEmployee() {
        employee.setId(Integer.parseInt(views.txt_employee_id.getText().trim()));
        employee.setFull_name(views.txt_employee_fullname.getText().trim());
        employee.setUsername(views.txt_employee_username.getText().trim());
        employee.setAddress(views.txt_employee_address.getText().trim());
        employee.setTelephone(views.txt_employee_telephone.getText().trim());
        employee.setEmail(views.txt_employee_email.getText().trim());
        employee.setPassword(String.valueOf(views.txt_employee_password.getPassword()));
        employee.setRol(views.cmb_rol.getSelectedItem().toString());
    }
}
