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
import models.DynamicComboBox;
import static models.EmployeesDAO.rol_user;

import models.Suppliers;
import models.SuppliersDAO;
import views.SystemView;

public class SuppliersController implements ActionListener, MouseListener, KeyListener {

    private Suppliers supplier;
    private SuppliersDAO supplier_dao;
    private SystemView views;
    String rol = rol_user;

    DefaultTableModel model = new DefaultTableModel();

    public SuppliersController(Suppliers supplier, SuppliersDAO supplier_dao, SystemView views) {
        this.supplier = supplier;
        this.supplier_dao = supplier_dao;
        this.views = views;

        //Boton de registrar proveedores
        this.views.btn_register_supplier.addActionListener(this);
        this.views.btn_update_supplier.addActionListener(this);
        this.views.btn_delete_supplier.addActionListener(this);
        this.views.suppliers_table.addMouseListener(this);
        this.views.jLabelSuppliers.addMouseListener(this);
        this.views.txt_search_supplier.addKeyListener(this);
        this.views.btn_cancel_supplier.addActionListener(this);
        getSupplierName();
        
        if (rol.equals("Auxiliar")) {
            views.jTabbedPane1.setEnabledAt(5, false);
            views.jLabelSuppliers.setEnabled(false);
        }

        listAllSuppliers();

        this.views.txt_supplier_telephone.addKeyListener(new KeyAdapter() {
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
        if (e.getSource() == views.btn_register_supplier) {
            if (areFieldsEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                supplier.setName(views.txt_supplier_name.getText().trim());
                supplier.setDescription(views.txt_supplier_description.getText().trim());
                supplier.setAddress(views.txt_supplier_address.getText().trim());
                supplier.setTelephone(views.txt_supplier_telephone.getText().trim());
                supplier.setEmail(views.txt_supplier_email.getText().trim());
                supplier.setCity(views.cmb_supplier_city.getSelectedItem().toString());

                if (supplier_dao.registerSupplierQuery(supplier)) {
                    cleanFields();
                    model.setRowCount(0);
                    JOptionPane.showMessageDialog(null, "Proveedor registrado con exito");
                    listAllSuppliers();
                } else {
                    JOptionPane.showMessageDialog(null, "Ha ocurrido un error al registrar al proveedor");
                }
            }
        } else if (e.getSource() == views.btn_update_supplier) {
            //verificar si los campos estan vacios
            if (areFieldsEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                //Realizar inserción de registro
                supplier.setId((int) Integer.parseInt(views.txt_supplier_id.getText().trim()));
                supplier.setName(views.txt_supplier_name.getText().trim());
                supplier.setDescription(views.txt_supplier_description.getText().trim());
                supplier.setAddress(views.txt_supplier_address.getText().trim());
                supplier.setTelephone(views.txt_supplier_telephone.getText().trim());
                supplier.setEmail(views.txt_supplier_email.getText().trim());
                supplier.setCity(views.cmb_supplier_city.getSelectedItem().toString());

                if (supplier_dao.updateSupplierQuery(supplier)) {
                    cleanTable();
                    cleanFields();
                    listAllSuppliers();
                    JOptionPane.showMessageDialog(null, "Los datos del supplidor han sido modificados");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar el supplidor");
                }
            }
              
        } else if (e.getSource() == views.btn_delete_supplier) {
            int row = views.suppliers_table.getSelectedRow();
            //Verificar seleccion de registro
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un proveedor para eliminar");
            } else {
                //Realizar eliminación de registro seleccionado
                int id = Integer.parseInt(views.suppliers_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Quieres eliminar a este proveedor?");
                //Cuestionar eliminación
                if (question == 0 && supplier_dao.deleteSupplierQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_supplier.setEnabled(true);
                    listAllSuppliers();
                    JOptionPane.showMessageDialog(null, "Proveedor eliminado con éxito");
                }
            }
        } else if (e.getSource() == views.btn_cancel_supplier) {
            cleanFields();
            views.btn_register_supplier.setEnabled(true);
        }
    }

    public void listAllSuppliers() {
        if (rol.equals("Administrador")) {
            // Obtener los datos filtrados por texto de búsqueda
            List<Suppliers> list = supplier_dao.listSupplierQuery(views.txt_search_supplier.getText());
            // Obtener el modelo actual de la tabla de clientes
            model = (DefaultTableModel) views.suppliers_table.getModel();

            // Limpiar la tabla antes de llenarla
            model.setRowCount(0);

            // Rellenar los datos de los clientes
            for (Suppliers s : list) {
                Object[] row = new Object[7];
                row[0] = s.getId();
                row[1] = s.getName();
                row[2] = s.getDescription();
                row[3] = s.getAddress();
                row[4] = s.getTelephone();
                row[5] = s.getEmail();
                row[6] = s.getCity();
                model.addRow(row);
            }
            // Establecer nuevamente el modelo a la tabla por si hubo cambios
            views.suppliers_table.setModel(model);
        }
    }
    
    

    @Override
    public void mouseClicked(MouseEvent e) {
        //Cargar datos de tabla en cuadros de texto
        if (e.getSource() == views.suppliers_table) {
            if (e.getClickCount() == 2) { // Verifica doble clic
                int row = views.suppliers_table.rowAtPoint(e.getPoint());
                views.txt_supplier_id.setText(views.suppliers_table.getValueAt(row, 0).toString());
                views.txt_supplier_name.setText(views.suppliers_table.getValueAt(row, 1).toString());
                views.txt_supplier_description.setText(views.suppliers_table.getValueAt(row, 2).toString());
                views.txt_supplier_address.setText(views.suppliers_table.getValueAt(row, 3).toString());
                views.txt_supplier_telephone.setText(views.suppliers_table.getValueAt(row, 4).toString());
                views.txt_supplier_email.setText(views.suppliers_table.getValueAt(row, 5).toString());
                views.cmb_supplier_city.setSelectedItem(views.suppliers_table.getValueAt(row, 6).toString());

                views.btn_register_supplier.setEnabled(false);
                views.txt_supplier_id.setEditable(false);
            }
        } else if (e.getSource() == views.jLabelSuppliers) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane1.setSelectedIndex(5);
                cleanTable();
                cleanFields();
                listAllSuppliers();
            } else {
                JOptionPane.showMessageDialog(null, "No tienes privilegios para ver este modulo");
            }
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
        if (e.getSource() == views.txt_search_supplier) {
            cleanTable();
            listAllSuppliers();
        }
    }

    public void cleanTable() {
        model.setRowCount(0);
    }

    public void cleanFields() {
        views.txt_supplier_id.setText("");
        views.txt_supplier_name.setText("");
        views.txt_supplier_address.setText("");
        views.txt_supplier_description.setText("");
        views.txt_supplier_telephone.setText("");
        views.txt_supplier_email.setText("");
        views.cmb_supplier_city.setSelectedIndex(0);
        
        views.btn_register_supplier.setEnabled(true);
    }

    public boolean areFieldsEmpty() {
        return views.txt_supplier_name.getText().trim().isEmpty()
                || views.txt_supplier_description.getText().trim().isEmpty()
                || views.txt_supplier_address.getText().trim().isEmpty()
                || views.txt_supplier_telephone.getText().trim().isEmpty()
                || views.txt_supplier_email.getText().trim().isEmpty()
                || views.cmb_supplier_city.getSelectedItem() == null
                || views.cmb_supplier_city.getSelectedItem().toString().trim().isEmpty();
    }
    //Mostrar nombre de proveedor
    public void getSupplierName() {
        List<Suppliers> list = supplier_dao.listSupplierQuery(views.txt_search_supplier.getText());

        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            views.cmb_purchase_supplier.addItem(new DynamicComboBox(id, name));
        }
    }
}
