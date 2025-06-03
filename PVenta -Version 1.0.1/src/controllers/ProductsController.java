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
import javax.swing.SwingUtilities;

import javax.swing.table.DefaultTableModel;
import models.DynamicComboBox;
import static models.EmployeesDAO.rol_user;
import models.Products;
import models.ProductsDAO;
import views.ReportViewer;
import views.SystemView;

public class ProductsController implements ActionListener, MouseListener, KeyListener {


    private Products products;
    private ProductsDAO products_dao;
    private SystemView views;
    String rol = rol_user;

    DefaultTableModel model = new DefaultTableModel();

    public ProductsController(Products products, ProductsDAO products_dao, SystemView views) {
        this.products = products;
        this.products_dao = products_dao;
        this.views = views;

        this.views.btn_register_product.addActionListener(this);
        this.views.btn_update_product.addActionListener(this);
        this.views.btn_delete_product.addActionListener(this);
        this.views.btn_cancel_product.addActionListener(this);
        this.views.txt_search_product.addKeyListener(this);
        this.views.products_table.addMouseListener(this);
        this.views.jLabelProducts.addMouseListener(this);
        this.views.jTabbedPane1.addMouseListener(this);
        this.views.jLabelReportsGral.addMouseListener(this);

        this.views.txt_product_unit_price.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_product_code.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });
        
        if (rol.equals("Auxiliar")) {
            views.jTabbedPane1.setEnabledAt(0, false);
            views.jLabelProducts.setEnabled(false);
            views.jTabbedPane1.setSelectedIndex(2);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_register_product) {
            if (EmptyFields()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                products.setCode(Integer.parseInt(views.txt_product_code.getText()));
                products.setName(views.txt_product_name.getText().trim());
                products.setDescription(views.txt_product_description.getText().trim());
                products.setUnit_price(Double.parseDouble(views.txt_product_unit_price.getText()));
                DynamicComboBox category_id = (DynamicComboBox) views.cmb_category.getSelectedItem();
                products.setCategory_id(category_id.getId());

                if (products_dao.registerProductQuery(products)) {
                    cleanFields();
                    cleanTable();
                    listAllProducts();
                    JOptionPane.showMessageDialog(null, "Producto registrado con exito");
                } else {
                    JOptionPane.showMessageDialog(null, "Ha ocurrido un problema al registrar al producto");
                }
            }
        } else if (e.getSource() == views.btn_update_product) {
            if (views.txt_product_id.equals("")) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            } else {
                if (EmptyFields()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                } else {
                    products.setCode(Integer.parseInt(views.txt_product_code.getText()));
                    products.setName(views.txt_product_name.getText().trim());
                    products.setDescription(views.txt_product_description.getText().trim());
                    products.setUnit_price(Double.parseDouble(views.txt_product_unit_price.getText()));
                    DynamicComboBox category_id = (DynamicComboBox) views.cmb_category.getSelectedItem();
                    products.setCategory_id(category_id.getId());

                    products.setId(Integer.parseInt(views.txt_product_id.getText()));

                    if (products_dao.updateProductQuery(products)) {
                        cleanTable();
                        cleanFields();
                        listAllProducts();

                        JOptionPane.showMessageDialog(null, "Los datos del producto han sido modificados");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al modificar el producto");
                    }
                }
            }
        } else if (e.getSource() == views.btn_delete_product) {
            int row = views.products_table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un producto para eliminar");
            } else {
                int id = Integer.parseInt(views.products_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Quieres eliminar a este proveedor?");

                if (question == 0 && products_dao.deleteProductQuery(id) != false) {

                    views.btn_register_product.setEnabled(true);
                    listAllProducts();
                    JOptionPane.showMessageDialog(null, "Proveedor eliminado con éxito");
                    cleanTable();
                    cleanFields();
                }
            }
        } else if (e.getSource() == views.btn_cancel_product) {
            cleanFields();
        }
    }

    public void listAllProducts() {
        if (rol.equals("Administrador") || rol.equals("Auxiliar")) {
            List<Products> list = products_dao.listProductQuerty(views.txt_search_product.getText());
            model = (DefaultTableModel) views.products_table.getModel();

            Object[] row = new Object[6];
            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getCode();
                row[2] = list.get(i).getName();
                row[3] = list.get(i).getUnit_price();
                row[4] = list.get(i).getProduct_quantity();
                row[5] = list.get(i).getCategory_name();
                model.addRow(row);
            }
            views.products_table.setModel(model);
            if (rol.equals("Auxiliar")) {
                views.btn_register_product.setEnabled(false);
                views.btn_update_product.setEnabled(false);
                views.btn_delete_product.setEnabled(false);
                views.btn_cancel_product.setEnabled(false);
                views.txt_product_code.setEnabled(false);
                views.txt_product_description.setEnabled(false);
                views.txt_product_id.setEditable(false);
                views.txt_product_name.setEditable(false);
                views.txt_product_unit_price.setEditable(false);
            }
        }
    }

    public boolean EmptyFields() {
        return views.txt_product_code.getText().isEmpty()
                || views.txt_product_description.getText().isEmpty()
                || views.txt_product_name.getText().isEmpty()
                || views.txt_product_unit_price.getText().isEmpty()
                || views.cmb_category.getSelectedItem() == null
                || views.cmb_category.getSelectedItem().toString().trim().isEmpty();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.products_table) {
            if (e.getClickCount() == 2) {
                int row = views.products_table.rowAtPoint(e.getPoint());
                views.txt_product_id.setText(views.products_table.getValueAt(row, 0).toString());
                products = products_dao.searchProduct(Integer.parseInt(views.txt_product_id.getText()));
                views.txt_product_code.setText("" + products.getCode());
                views.txt_product_name.setText(products.getName());
                views.txt_product_description.setText(products.getDescription());
                views.txt_product_unit_price.setText("" + products.getUnit_price());
                views.cmb_category.setSelectedItem(new DynamicComboBox(products.getCategory_id(), products.getCategory_name()));
                views.btn_register_product.setEnabled(false);
            }
        } else if (e.getSource() == views.jLabelProducts) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane1.setSelectedIndex(0);
                cleanTable();
                cleanFields();
                listAllProducts();
            } else {
                JOptionPane.showMessageDialog(null, "No tienes privilegios para ver este modulo");
            }
        }
        if (views.jTabbedPane1.getSelectedIndex()==0) {
            cleanTable();
            listAllProducts();
        }
                System.out.println("Evento de click detectado");

        if (e.getSource() == views.jLabelReportsGral) {
            System.out.println("Click en jLabelReportsGral confirmado");

            // Crea y muestra el ReportViewer en el Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                ReportViewer reportViewer = new ReportViewer();
                reportViewer.setVisible(true);
                System.out.println("ReportViewer debería ser visible ahora");
            });
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
        if (e.getSource() == views.txt_search_product) {
            cleanTable();
            listAllProducts();
        }
    }

    public void cleanTable() {
        DefaultTableModel model = (DefaultTableModel) views.products_table.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    public void cleanFields() {
        views.txt_product_id.setText("");
        views.txt_product_code.setText("");
        views.txt_product_description.setText("");
        views.txt_product_name.setText("");
        views.txt_product_unit_price.setText("");
        views.cmb_category.setSelectedIndex(0);
        views.btn_register_product.setEnabled(true);
    }

}
