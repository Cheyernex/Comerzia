package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import models.DynamicComboBox;
import static models.EmployeesDAO.id_user;
import static models.EmployeesDAO.rol_user;
import models.Products;
import models.ProductsDAO;
import models.Purchases;
import models.PurchasesDAO;
import views.SystemView;

public class PurchasesController implements KeyListener, ActionListener, MouseListener {

    private Purchases purchase;
    private PurchasesDAO purchase_dao;
    private SystemView views;
    private int getIdSupplier = 0;
    private int item = 0;
    private PurchasesDAO purchasesDAO;

    private generatePurchaseReport reportGenerator;

    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel temp;

    //Instanciar el modelo de productos
    Products product = new Products();
    ProductsDAO product_dao = new ProductsDAO();
    String rol = rol_user;

    public PurchasesController(Purchases purchase, PurchasesDAO purchase_dao, SystemView views) {
        this.purchase = purchase;
        this.purchase_dao = purchase_dao;
        this.views = views;
        this.views.txt_purchase_product_code.addKeyListener(this);
        this.views.txt_purchase_price.addKeyListener(this);
        this.views.txt_purchase_amount.addKeyListener(this);
        this.views.jLabelPurchases.addMouseListener(this);

        this.views.btn_add_product_to_buy.addActionListener(this);
        this.views.btn_confirm_purchase.addActionListener(this);
        this.views.btn_remove_purchase.addActionListener(this);
        this.views.btn_new_purchase.addActionListener(this);
        this.views.jLabelReports.addMouseListener(this);
        this.purchasesDAO = new PurchasesDAO();
        this.reportGenerator = new generatePurchaseReport();
        initSubtotalListeners();

        this.views.txt_purchase_amount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_purchase_price.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_purchase_product_code.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });
        if (rol.equals("Auxiliar")) {
            views.jTabbedPane1.setEnabledAt(1, false);
            views.jLabelPurchases.setEnabled(false);

        }

        // Escuchar cambios en la tabla
        tableChanges();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_add_product_to_buy) {
            // Validar que el campo de cantidad no esté vacío
            if (views.txt_purchase_amount.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes ingresar la cantidad del producto");
                views.txt_purchase_amount.requestFocus();
                return;
            }

            DynamicComboBox supplier_cmb = (DynamicComboBox) views.cmb_purchase_supplier.getSelectedItem();
            int supplier_id = supplier_cmb.getId();

            if (getIdSupplier == 0) {
                getIdSupplier = supplier_id;
            } else {
                if (getIdSupplier != supplier_id) {
                    JOptionPane.showMessageDialog(null, "No puede realizar una misma compra a varios proveedores");
                    return;
                }
            }

            try {
                int amount = Integer.parseInt(views.txt_purchase_amount.getText());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a cero");
                    views.txt_purchase_amount.requestFocus();
                    return;
                }

                String product_name = views.txt_purchase_product_name.getText();
                Double price = Double.parseDouble(views.txt_purchase_price.getText());
                int purchase_id = Integer.parseInt(views.txt_purchase_id.getText());
                String supplier_name = views.cmb_purchase_supplier.getSelectedItem().toString();

                temp = (DefaultTableModel) views.purchases_table.getModel();
                for (int i = 0; i < views.purchases_table.getRowCount(); i++) {
                    if (views.purchases_table.getValueAt(i, 1).equals(views.txt_purchase_product_name.getText())) {
                        JOptionPane.showMessageDialog(null, "El producto ya está registrado en la tabla de compras");
                        return;
                    }
                }

                ArrayList list = new ArrayList();
                item = 1;
                list.add(item);
                list.add(purchase_id);
                list.add(product_name);
                list.add(amount);
                list.add(price);
                list.add(amount * price);
                list.add(supplier_name);

                Object[] obj = new Object[6];
                obj[0] = list.get(1);
                obj[1] = list.get(2);
                obj[2] = list.get(3);
                obj[3] = list.get(4);
                obj[4] = list.get(5);
                obj[5] = list.get(6);
                temp.addRow(obj);
                views.purchases_table.setModel(temp);
                cleanFieldsPurchases();
                views.cmb_purchase_supplier.setEditable(false);
                views.txt_purchase_product_code.requestFocus();
                calculatePurchase();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser un número válido");
                views.txt_purchase_amount.requestFocus();
            }
        } else if (e.getSource() == views.btn_confirm_purchase) {
            if (this.temp == null) {
                JOptionPane.showMessageDialog(null, "La lista de compra está vacía");
            } else {
                insertPurchase();
                cleanTableTemp();
                cleanFieldsPurchases();
            }
        } else if (e.getSource() == views.btn_remove_purchase) {
            int selectedRow = views.purchases_table.getSelectedRow();
            model = (DefaultTableModel) views.purchases_table.getModel();

            if (selectedRow != -1) {
                model.removeRow(selectedRow);
                calculatePurchase();
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una fila para eliminar.");
            }

            views.txt_purchase_product_code.requestFocus();

        } else if (e.getSource() == views.btn_new_purchase) {
            if (this.temp == null) {
                JOptionPane.showMessageDialog(null, "La lista de compra está vacía");
            } else {
                cleanTableTemp();
                cleanFieldsPurchases();
            }
        }
    }

    public void insertPurchase() {
        // 1. Obtener datos básicos de la compra
        double total = Double.parseDouble(views.txt_purchase_total_to_pay.getText());
        int employee_id = id_user;  // ID del empleado/vendedor actual

        // 2. Registrar la compra principal
        if (purchase_dao.registerPurchaseQuery(getIdSupplier, employee_id, total)) {
            int purchase_id = purchase_dao.purchaseId();
            List<Map<String, Object>> itemsReporte = new ArrayList<>();

            // 3. Procesar cada item de la compra
            for (int i = 0; i < views.purchases_table.getRowCount(); i++) {
                // Obtener datos del producto
                int product_id = Integer.parseInt(views.purchases_table.getValueAt(i, 0).toString());
                int purchase_amount = Integer.parseInt(views.purchases_table.getValueAt(i, 2).toString());
                double purchase_price = Double.parseDouble(views.purchases_table.getValueAt(i, 3).toString());
                double purchase_subtotal = purchase_price * purchase_amount;

                // Registrar detalle en BD
                purchase_dao.registerPurchasesDetailsQuery(purchase_id, purchase_price, purchase_amount, purchase_subtotal, product_id);

                // Actualizar stock del producto
                product = product_dao.searchId(product_id);
                int new_stock = product.getProduct_quantity() + purchase_amount;
                product_dao.updateStockQuerty(new_stock, product_id);

                // Preparar datos para el reporte
                Map<String, Object> item = new HashMap<>();
                item.put("product_name", views.purchases_table.getValueAt(i, 1).toString()); // Nombre del producto
                item.put("purchase_price", purchase_price);
                item.put("purchase_amount", purchase_amount);
                item.put("purchase_subtotal", purchase_subtotal);
                itemsReporte.add(item);
            }

            // 4. Obtener datos adicionales para el reporte
            String nombreProveedor = purchase_dao.getSupplierNameByPurchaseId(purchase_id);
            String nombreComprador = purchase_dao.getEmployeeNameById(employee_id); // Obtenemos el nombre completo
            String fechaHora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());

            // 5. Generar el reporte PDF
            try {
                // Corrección 1: Nombre de clase correcto (GeneradorReportes)
                generatePurchaseReport generador = new generatePurchaseReport();

                // Corrección 2: Nombre de método correcto (generarReporteCompra)
                generador.generarReporteCompra(
                        purchase_id,
                        fechaHora, // Corrección 3: Variable con nombre consistente (fechaHora)
                        nombreProveedor,
                        nombreComprador,
                        total,
                        itemsReporte
                );

                // 6. Mostrar confirmación al usuario
                JOptionPane.showMessageDialog(
                        null,
                        "Compra registrada exitosamente\n"
                        + // Corrección 4: Texto sin errores
                        "Comprobante generado en: comprobantes/compra_" + purchase_id + ".pdf",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {
                // Manejar error solo en la generación del reporte (la compra ya está registrada)
                JOptionPane.showMessageDialog(
                        null,
                        "Compra registrada pero hubo un error al generar el comprobante:\n" + e.getMessage(),
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                e.printStackTrace();
            }

        }
    }

    //Listar compras realizass
    public void listAllPurchases() {
        if (rol.equals("Administrador") || rol.equals("Auxiliar")) {
            List<Purchases> list = purchase_dao.listAllPurchasesQuery();
            model = (DefaultTableModel) views.table_all_purchases.getModel();

            Object[] row = new Object[5];

            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getSupplier_name_product();
                row[2] = list.get(i).getEmployee_name();
                row[3] = list.get(i).getTotal();
                row[4] = list.get(i).getCreated();
                model.addRow(row);
            }
            views.table_all_purchases.setModel(model);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == views.txt_purchase_product_code) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // Verificar si el campo está vacío
                if (views.txt_purchase_product_code.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingresa el código del producto a comprar");
                    return;  // Salir del método si no hay código
                }

                try {
                    // Convertir el código a número
                    int id = Integer.parseInt(views.txt_purchase_product_code.getText().trim());

                    // Buscar el producto en la base de datos
                    product = product_dao.searchCode(id);

                    // Verificar si se encontró el producto
                    if (product != null && product.getId() != 0) {
                        // Mostrar información del producto
                        views.txt_purchase_product_name.setText(product.getName());
                        views.txt_purchase_id.setText(String.valueOf(product.getId()));
                        views.txt_purchase_amount.requestFocus();  // Mover foco al campo de cantidad
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto no encontrado");
                        views.txt_purchase_product_code.requestFocus();  // Regresar foco al campo de código
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Código inválido. Debe ser un número.");
                    views.txt_purchase_product_code.requestFocus();
                }
            }
        } else if (e.getSource() == views.txt_purchase_amount) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // Simular clic en el botón de agregar producto
                views.txt_purchase_price.requestFocus();
            }
        } else if (e.getSource() == views.txt_purchase_price) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // Simular clic en el botón de agregar producto
                views.btn_add_product_to_buy.doClick();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    //Limpiar campos
    public void cleanFieldsPurchases() {
        views.txt_purchase_product_name.setText("");
        views.txt_purchase_price.setText("");
        views.txt_purchase_amount.setText("");
        views.txt_purchase_product_code.setText("");
        views.txt_purchase_subtotal.setText("");
        views.txt_purchase_id.setText("");
        views.txt_purchase_total_to_pay.setText("");
    }

    //Calcular total a pagar
    public void calculatePurchase() {
        double total = 0.00;
        int numRow = views.purchases_table.getRowCount();

        for (int i = 0; i < numRow; i++) {
            //Pasar el indice de la columna que se sumara
            total = total += Double.parseDouble(String.valueOf(views.purchases_table.getValueAt(i, 4)));
        }
        views.txt_purchase_total_to_pay.setText(String.format("%.2f", total));
    }

    public void tableChanges() {
        views.purchases_table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            // Solo responder a cambios en las columnas de cantidad (índice 2) o precio (índice 3)
            if (column == 2 || column == 3) {
                try {
                    int cantidad = Integer.parseInt(views.purchases_table.getValueAt(row, 2).toString());
                    double precio = Double.parseDouble(views.purchases_table.getValueAt(row, 3).toString());
                    double subtotal = cantidad * precio;

                    views.purchases_table.setValueAt(String.format("%.2f", subtotal), row, 4); // Actualiza subtotal
                    calculatePurchase(); // Actualiza el total general
                } catch (NumberFormatException ex) {
                    // Mostrar mensaje de error si el usuario escribe mal un número
                    JOptionPane.showMessageDialog(null, "Cantidad o precio inválido. Verifica los datos.");
                }
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.jLabelPurchases) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane1.setSelectedIndex(1);
            } else {
                JOptionPane.showMessageDialog(null, "No tienes privilegios para ver este modulo");
            }
        } else if (e.getSource() == views.jLabelReports) {
            views.jTabbedPane1.setSelectedIndex(7);
            listAllPurchases();
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

    public void cleanTableTemp() {
        temp.setRowCount(0);
    }

    private void initSubtotalListeners() {
        views.txt_purchase_amount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSubtotal();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSubtotal();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSubtotal();
            }
        });

        views.txt_purchase_price.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSubtotal();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSubtotal();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSubtotal();
            }
        });
    }
    
    private void updateSubtotal() {
    try {
        String qtyText = views.txt_purchase_amount.getText().trim();
        String priceText = views.txt_purchase_price.getText().trim();

        if (!qtyText.isEmpty() && !priceText.isEmpty()) {
            int quantity = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText);
            double subtotal = quantity * price;

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "DO"));
            String formattedSubtotal = currencyFormat.format(subtotal);
            views.txt_purchase_subtotal.setText(formattedSubtotal);
        } else {
            views.txt_purchase_subtotal.setText("RD$0.00");
        }
    } catch (NumberFormatException e) {
        views.txt_purchase_subtotal.setText("RD$0.00");
    }
}
}
