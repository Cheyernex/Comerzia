package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Categories;
import models.CategoriesDAO;
import models.DynamicComboBox;
import static models.EmployeesDAO.rol_user;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import views.SystemView;


public class CategoriesController implements ActionListener, MouseListener, KeyListener{
    private Categories category;
    private CategoriesDAO categories_dao;
    private SystemView views;
    
    String rol = rol_user;
    
    
    DefaultTableModel model = new DefaultTableModel();

    public CategoriesController(Categories category, CategoriesDAO categories_dao, SystemView views) {
        this.category = category;
        this.categories_dao = categories_dao;
        this.views = views;
        
        this.views.btn_register_category.addActionListener(this);
        this.views.btn_update_category.addActionListener(this);
        this.views.btn_delete_category.addActionListener(this);
        this.views.btn_cancel_category.addActionListener(this);
        this.views.jLabelCategories.addMouseListener(this);
        this.views.txt_search_category.addKeyListener(this);
        this.views.categories_table.addMouseListener(this);
        getCategoryName();
        AutoCompleteDecorator.decorate(views.cmb_category);
        
        if (rol.equals("Auxiliar")) {
            views.jTabbedPane1.setEnabledAt(6, false);
            views.jLabelCategories.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_register_category) {
            if (views.txt_category_name.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            }else{
                //category.setId((int) Integer.parseInt(views.txt_category_id.getText().trim()));
                category.setName(views.txt_category_name.getText().trim());
                if (categories_dao.registerCategoryQuery(category)) {
                    cleanTable();
                    cleanFields();
                    JOptionPane.showMessageDialog(null, "Categoria registrada");
                    listAllCategories();
                }else{
                    JOptionPane.showMessageDialog(null, "Error al registrar la categoria");
                }
            }
        }else if(e.getSource() == views.btn_update_category){
            if (views.txt_category_id.equals("")) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            }else{
                
                if (views.txt_category_id.getText().equals("") || views.txt_category_name.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                }else{
                    category.setId((int) Integer.parseInt(views.txt_category_id.getText().trim()));
                    category.setName(views.txt_category_name.getText().trim());
                    
                    if (categories_dao.updateCategoryQuery(category)) {
                        cleanTable();
                        cleanFields();
                        listAllCategories();
                        views.btn_register_category.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "Los datos de la categoria han sido modificados");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al modificar la categoria");
                    }
                }
            }
            
        } else if (e.getSource() == views.btn_delete_category) {
            int row = views.categories_table.getSelectedRow();
            
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debes seleccionar una categoria para eliminar");
            } else {
                int id = Integer.parseInt(views.categories_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Quieres eliminar a esta categoria?");

                if (question == 0 && categories_dao.deleteCategoryQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_category.setEnabled(true);
                    listAllCategories();
                    JOptionPane.showMessageDialog(null, "Categoria eliminada con éxito");
                }
            }
        }else if(e.getSource() == views.btn_cancel_category){
           cleanFields();
           views.btn_register_category.setEnabled(true);
        }
    }
    
    public void listAllCategories(){
        if (rol.equals("Administrador")) {
            List<Categories> list = categories_dao.listCategoriesQuery(views.txt_search_category.getText());
            model = (DefaultTableModel) views.categories_table.getModel();
            
            Object[] row = new Object[2];
            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getName();
                
                model.addRow(row);
            }
        }
    }
    
    public void cleanFields(){
        views.txt_category_id.setText("");
        views.txt_category_name.setText("");
    }
    public void cleanTable(){
        model.setRowCount(0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.categories_table) {
            if (e.getClickCount() == 2) {
                int row = views.categories_table.rowAtPoint(e.getPoint());
                views.txt_category_id.setText(views.categories_table.getValueAt(row, 0).toString());
                views.txt_category_name.setText(views.categories_table.getValueAt(row, 1).toString());
                views.txt_category_id.setEnabled(false);
                views.btn_register_category.setEnabled(false);
            }
        }

        if (e.getSource() == views.jLabelCategories) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane1.setSelectedIndex(6);
                cleanTable();
                cleanFields();
                listAllCategories();
            } else {
                views.jTabbedPane1.setEnabledAt(6, false);
                views.jLabelCategories.setEnabled(false);
                JOptionPane.showMessageDialog(null, "No tienes privilegios para ver este módulo");
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
            if (e.getSource() == views.txt_search_category) {
            cleanTable();
            listAllCategories();
        }
    }
    
    //Metodo para mostrar el nombre de las categorias
    public void getCategoryName() {
        List<Categories> list = categories_dao.listCategoriesQuery(views.txt_search_category.getText());
        
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            views.cmb_category.addItem(new DynamicComboBox(id, name));
        }
    }
    
}
