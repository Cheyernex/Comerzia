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

import models.Customers;
import models.CustomersDAO;
import views.SystemView;

public class CustomersController implements ActionListener, MouseListener, KeyListener {

    private Customers customer;
    private CustomersDAO customer_dao;
    private SystemView views;

    
    DefaultTableModel model = new DefaultTableModel();

    public CustomersController(Customers customer, CustomersDAO customer_dao, SystemView views) {

        this.customer = customer;
        this.customer_dao = customer_dao;
        this.views = views;
        
        //Boton de registrar cliente
        this.views.btn_register_customer.addActionListener(this);
        this.views.customers_table.addMouseListener(this);
        this.views.txt_search_customer.addKeyListener(this);
        this.views.btn_update_customer.addActionListener(this);
        this.views.btn_delete_customer.addActionListener(this);
        this.views.btn_cancel_customer.addActionListener(this);
        this.views.jLabelCustomers.addMouseListener(this);
        listAllCustomers();
        
        this.views.txt_customer_telephone.addKeyListener(new KeyAdapter() {
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
        //verificar si los campos estan vacios
        if (e.getSource() == views.btn_register_customer) {
            if (areFieldsEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                } else {
                //Realizar inserción de registro
                customer.setId(Integer.parseInt(views.txt_customer_id.getText().trim()));
                customer.setFullname(views.txt_customer_fullname.getText().trim());
                customer.setAddress(views.txt_customer_address.getText().trim());
                customer.setTelephone(views.txt_customer_telephone.getText().trim());
                customer.setEmail(views.txt_customer_email.getText().trim());

                if (customer_dao.registerCustomerQuery(customer)) {
                    cleanTable();
                    cleanFields();                  
                    JOptionPane.showMessageDialog(null, "Cliente registrado con éxito");
                    listAllCustomers();
                    
                } else {
                    JOptionPane.showMessageDialog(null, "Ha ocurrido un error al registrar al cliente");
                }
            }
        } else if (e.getSource() == views.btn_update_customer) {
            if (views.txt_customer_id.equals("")) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            } else {
                //Verificar si los campos estan vacios
                if (areFieldsEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debes seleccionar un cliente para modificar");
                } else {
                    //Realizar modificación de registro seleccionado
                    customer.setId((int) Integer.parseInt(views.txt_customer_id.getText().trim()));
                    customer.setFullname(views.txt_customer_fullname.getText().trim());
                    customer.setAddress(views.txt_customer_address.getText().trim());
                    customer.setTelephone(views.txt_customer_telephone.getText().trim());
                    customer.setEmail(views.txt_customer_email.getText().trim());

                    if (customer_dao.updateCustomerQuery(customer)) {
                        cleanTable();
                        cleanFields();
                        listAllCustomers();
                        JOptionPane.showMessageDialog(null, "Los datos del cliente han sido modificados");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al modificar el cliente");
                    }
                }
            }

        }else if(e.getSource() == views.btn_delete_customer){
            int row = views.customers_table.getSelectedRow();
            //Verificar seleccion de registro
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un cliente para eliminar");
            }else{                
                //Realizar eliminación de registro seleccionado
                int id = Integer.parseInt(views.customers_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Quieres eliminar a este cliente?");
                //Cuestionar eliminación
                if (question == 0 && customer_dao.deleteCustomerQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_customer.setEnabled(true);
                    listAllCustomers();
                    JOptionPane.showMessageDialog(null, "Empleado eliminado con éxito");
                }
            }
        }else if(e.getSource() == views.btn_cancel_customer){
           cleanFields();
           views.btn_register_customer.setEnabled(true);
           views.txt_customer_id.setEnabled(true);
        }
    }
    
    public void listAllCustomers() {
        // Obtener los datos filtrados por texto de búsqueda
        List<Customers> list = customer_dao.listCustomerQuery(views.txt_search_customer.getText());
        // Obtener el modelo actual de la tabla de clientes
        model = (DefaultTableModel) views.customers_table.getModel();

        // Limpiar la tabla antes de llenarla
        model.setRowCount(0);

        // Rellenar los datos de los clientes
        for (Customers c : list) {
            Object[] row = new Object[5];
            row[0] = c.getId();
            row[1] = c.getFullname();
            row[2] = c.getAddress();
            row[3] = c.getTelephone();
            row[4] = c.getEmail();
            model.addRow(row);
        }

        // Establecer nuevamente el modelo a la tabla por si hubo cambios
        views.customers_table.setModel(model);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.customers_table) {
            if (e.getClickCount() == 2) { // Verifica doble clic
                int row = views.customers_table.rowAtPoint(e.getPoint());
                //Definir objetos a mostrar en la tabla
                views.txt_customer_id.setText(views.customers_table.getValueAt(row, 0).toString());
                views.txt_customer_fullname.setText(views.customers_table.getValueAt(row, 1).toString());
                views.txt_customer_address.setText(views.customers_table.getValueAt(row, 2).toString());
                views.txt_customer_telephone.setText(views.customers_table.getValueAt(row, 3).toString());
                views.txt_customer_email.setText(views.customers_table.getValueAt(row, 4).toString());

                //Deshabilitar botones
                views.btn_register_customer.setEnabled(false);
                views.txt_customer_id.setEditable(false);
            }

        } else if (e.getSource() == views.jLabelCustomers) {
            views.jTabbedPane1.setSelectedIndex(3);
            cleanTable();
            cleanFields();
            listAllCustomers();
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
        if (e.getSource() == views.txt_search_customer) {
            cleanTable();
            listAllCustomers();
        }
    }

    public void cleanTable() {
        model.setRowCount(0);
    }
    public void cleanFields(){
        views.txt_customer_id.setText("");
        views.txt_customer_fullname.setText("");
        views.txt_customer_address.setText("");
        views.txt_customer_telephone.setText("");
        views.txt_customer_email.setText("");
        
        views.txt_customer_id.setEditable(true);
        views.btn_register_customer.setEnabled(true);
    }

    public boolean areFieldsEmpty() {
        return views.txt_customer_id.getText().trim().isEmpty()
                || views.txt_customer_fullname.getText().trim().isEmpty()
                || views.txt_customer_address.getText().trim().isEmpty()
                || views.txt_customer_telephone.getText().trim().isEmpty()
                || views.txt_customer_email.getText().trim().isEmpty();
    }
}
