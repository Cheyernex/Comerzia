package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import static models.EmployeesDAO.id_user;
import static models.EmployeesDAO.rol_user;
import models.Products;
import models.ProductsDAO;
import models.Sales;
import models.SalesDAO;
import views.SystemView;
import models.CustomersDAO;
import models.Customers;

public class SalesController implements ActionListener, KeyListener, MouseListener {

    private Sales sale;
    private SystemView views;
    private SalesDAO sale_dao;
    private int getIdCustomer = 0;
    private int item = 0;
    private Customers customer;
    private CustomersDAO customer_dao;

    private GenerateSalesReport reportGenerator;

    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel temp;

    Products product = new Products();
    ProductsDAO product_dao = new ProductsDAO();
    String rol = rol_user;

    public SalesController(CustomersDAO customer_dao, Sales sale, SalesDAO sale_dao, SystemView views) {
        this.views = views;
        this.sale_dao = sale_dao;
        this.customer_dao = customer_dao;
        this.temp = (DefaultTableModel) this.views.sales_table.getModel();

        // Registrar acción del botón de realizar venta
        this.views.btn_add_product_sale.addActionListener(this);
        this.views.btn_confirm_sale.addActionListener(this);
        this.views.btn_remove_sale.addActionListener(this);
        this.views.btn_new_sale.addActionListener(this);
        this.views.txt_sale_product_code.addKeyListener(this);
        this.views.txt_sale_customer_id.addKeyListener(this);
        initSubtotalListeners();
        this.views.txt_sale_quantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_sale_price.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_sale_product_code.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Bloquea letras, símbolos y espacios
                }
            }
        });

        this.views.txt_sale_customer_id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerName();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_add_product_sale) {
            // Validar campo de cantidad
            if (views.txt_sale_quantity.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes ingresar la cantidad del producto");
                views.txt_sale_quantity.requestFocus();
                return;
            }

            // Validar que se haya seleccionado un cliente
            if (views.txt_sale_customer.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para realizar la venta");
                return;
            }

            try {
                int quantity = Integer.parseInt(views.txt_sale_quantity.getText().trim());
                int stock = Integer.parseInt(views.txt_sale_stock.getText().trim());

                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a cero");
                    views.txt_sale_quantity.requestFocus();
                    return;
                }

                if (quantity > stock) {
                    JOptionPane.showMessageDialog(null, "Stock insuficiente para esta cantidad");
                    views.txt_sale_quantity.requestFocus();
                    return;
                }

                // Obtener los datos del producto y cliente
                String productName = views.txt_sale_product_name.getText().trim();
                double price = Double.parseDouble(views.txt_sale_price.getText().trim());
                int productId = Integer.parseInt(views.txt_sale_product_id.getText().trim());
                String customerName = views.txt_sale_customer.getText().trim();

                // Validar si el producto ya está en la tabla
                DefaultTableModel model = (DefaultTableModel) views.sales_table.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 1).equals(productName)) {
                        JOptionPane.showMessageDialog(null, "El producto ya está registrado en la tabla de ventas");
                        return;
                    }
                }

                // Validar que no se mezclen clientes en la venta
                if (model.getRowCount() > 0) {
                    String clienteEnTabla = model.getValueAt(0, 5).toString();
                    if (!clienteEnTabla.equalsIgnoreCase(customerName)) {
                        JOptionPane.showMessageDialog(null, "No puede realizar una misma venta a varios clientes");
                        return;
                    }
                }

                // Agregar producto a la tabla de ventas
                Object[] row = new Object[6];
                row[0] = productId;
                row[1] = productName;
                row[2] = quantity;
                row[3] = price;
                row[4] = quantity * price;
                row[5] = customerName;
                model.addRow(row);
                views.sales_table.setModel(model);

                // Limpiar campos y enfocar nuevamente
                cleanFieldsSales();
                views.txt_sale_product_code.requestFocus();
                calculateSale();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser un número válido");
                views.txt_sale_quantity.requestFocus();
            }
        } else if (e.getSource() == views.btn_confirm_sale) {
            if (this.temp == null || views.sales_table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "La lista de ventas está vacía");
            } else {
                registerSale();
            }

        } else if (e.getSource() == views.btn_remove_sale) {
            int selectedRow = views.sales_table.getSelectedRow();
            model = (DefaultTableModel) views.sales_table.getModel();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
                calculateSale();
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una fila para eliminar.");
            }
            views.txt_sale_product_code.requestFocus();

        } else if (e.getSource() == views.btn_new_sale) {
            if (this.temp == null || views.sales_table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "La lista de ventas está vacía");
            } else {
                cleanTableTemp();
                cleanFieldsSales();
            }
        }
    }

    private void registerSale() {
        try {
            // 1. Get basic sale data with validation
            double total = Double.parseDouble(views.txt_sale_total_to_pay.getText().trim());
            int employee_id = id_user;
            int customer_id = Integer.parseInt(views.txt_sale_customer_id.getText().trim());

            // 2. Register main sale
            if (!sale_dao.registerSaleQuery(customer_id, employee_id, total)) {
                JOptionPane.showMessageDialog(null, "Error al registrar la venta principal", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int sale_id = sale_dao.saleId();
            List<Map<String, Object>> saleItems = new ArrayList<>();

            // 3. Process each sale item
            for (int i = 0; i < views.sales_table.getRowCount(); i++) {
                int product_id = Integer.parseInt(views.sales_table.getValueAt(i, 0).toString());
                String product_name = views.sales_table.getValueAt(i, 1).toString();
                int quantity = Integer.parseInt(views.sales_table.getValueAt(i, 2).toString());
                double unit_price = Double.parseDouble(views.sales_table.getValueAt(i, 3).toString());
                double subtotal = unit_price * quantity;

                // Register sale detail
                if (!sale_dao.registerSaleDetailQuery(sale_id, product_id, quantity, unit_price, subtotal)) {
                    throw new RuntimeException("Error al registrar detalle de venta para producto ID: " + product_id);
                }

                // Update product stock
                Products product = product_dao.searchId(product_id);
                if (product == null) {
                    throw new RuntimeException("Producto no encontrado ID: " + product_id);
                }

                int new_stock = product.getProduct_quantity() - quantity;
                if (new_stock < 0) {
                    throw new RuntimeException("Stock insuficiente para producto: " + product.getName());
                }

                if (!product_dao.updateStockQuery(product_id, new_stock)) {
                    throw new RuntimeException("Error al actualizar stock para producto ID: " + product_id);
                }

                // Prepare report data - usando las claves correctas que espera el reporte
                Map<String, Object> item = new HashMap<>();
                item.put("product_id", product_id);
                item.put("product_name", product_name);
                item.put("sale_price", unit_price);
                item.put("sale_quantity", quantity);
                item.put("sale_subtotal", subtotal);
                item.put("sale_amount", quantity); // Usamos subtotal como amount o calcula otro valor
                saleItems.add(item);
            }

            // 4. Get additional report data

            int facturaID = sale_dao.getLastSaleId();
            String customerName = sale_dao.getCustomerNameBySaleId(facturaID);
            if ("Cliente no encontrado".equals(customerName)) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo obtener el nombre del cliente asociado a esta venta",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
            System.out.println("ID de venta obtenido: " + facturaID);
            String sellerName = sale_dao.getEmployeeNameById(employee_id);
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

            // 5. Generate PDF report
            try {
                GenerateSalesReport reportGenerator = new GenerateSalesReport();
                reportGenerator.generarReporteVenta(
                        facturaID, // ID de factura
                        timestamp, // Fecha/hora
                        sellerName, // Vendedor
                        customerName, // Cliente (obtenido de la BD)
                        total,
                        saleItems
                );

                // 6. Show success message and clean form
                JOptionPane.showMessageDialog(
                        null,
                        "Venta registrada exitosamente\n"
                        + "Comprobante generado en: comprobantes/venta_" + sale_id + ".pdf",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Limpiar formulario después de la venta exitosa
                cleanTableTemp();
                cleanFieldsSales();
                views.txt_sale_total_to_pay.setText("");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Venta registrada pero hubo un error al generar el comprobante:\n" + e.getMessage(),
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Formato numérico inválido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void clearSaleForm() {
        views.txt_sale_customer_id.setText("");
        views.cmb_category.setSelectedIndex(0);
        views.txt_sale_price.setText("");
        views.txt_sale_product_code.setText("");
        views.txt_sale_product_id.setText("");
        views.txt_sale_product_name.setText("");
        views.txt_sale_quantity.setText("");
        views.txt_sale_stock.setText("");
        views.txt_sale_subtotal.setText("");
        views.txt_sale_total_to_pay.setText("");
        model.setRowCount(0);
    }

    private void cleanFieldsSales() {
        views.txt_sale_product_code.setText("");
        views.txt_sale_product_id.setText("");
        views.txt_sale_product_name.setText("");
        views.txt_sale_quantity.setText("");
        views.txt_sale_price.setText("");
        views.txt_sale_stock.setText("");
        views.txt_sale_subtotal.setText("");
    }

    private void calculateSale() {
        double total = 0.0;
        for (int i = 0; i < views.sales_table.getRowCount(); i++) {
            total += Double.parseDouble(views.sales_table.getValueAt(i, 4).toString());
        }
        views.txt_sale_total_to_pay.setText(String.valueOf(total));
    }

    public void cleanTableTemp() {
        temp.setRowCount(0);
    }

    public void listAllSales() {
        if (rol.equals("Administrador") || rol.equals("Auxiliar")) {
            List<Sales> list = sale_dao.listAllSalesQuery();  // Asegúrate de tener este método en tu DAO
            model = (DefaultTableModel) views.table_all_sales.getModel();
            model.setRowCount(0); // Limpia la tabla antes de llenarla nuevamente

            Object[] row = new Object[5];
            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getCustomer_name();  // Asegúrate de tener este método en tu modelo Sales
                row[2] = list.get(i).getEmployee_name();
                row[3] = list.get(i).getTotal_to_pay();
                row[4] = list.get(i).getSale_date();        // Fecha de la venta
                model.addRow(row);
            }

            views.table_all_sales.setModel(model);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == views.txt_sale_product_code) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // Verificar si el campo está vacío
                if (views.txt_sale_product_code.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingresa el código del producto a vender");
                    return;
                }
                try {
                    // Convertir el código a número
                    int code = Integer.parseInt(views.txt_sale_product_code.getText().trim());
                    // Buscar el producto en la base de datos
                    product = product_dao.searchCodeSale(code);
                    // Verificar si se encontró el producto
                    if (product != null && product.getId() != 0) {
                        // Mostrar información del producto
                        views.txt_sale_product_id.setText(String.valueOf(product.getId()));
                        views.txt_sale_product_name.setText(product.getName());
                        views.txt_sale_stock.setText(String.valueOf(product.getProduct_quantity()));
                        views.txt_sale_price.setText(String.valueOf(product.getUnit_price()));
                        views.txt_sale_quantity.requestFocus();  // Mover foco al campo de cantidad
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto no encontrado");
                        views.txt_sale_product_code.requestFocus();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Código inválido. Debe ser un número.");
                    views.txt_sale_product_code.requestFocus();
                }
            }

        } else if (e.getSource() == views.txt_sale_quantity) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                views.txt_sale_price.requestFocus();
            }

        } else if (e.getSource() == views.txt_sale_price) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                views.btn_add_product_sale.doClick();  // Simula clic en botón de agregar
            }
        } else if (e.getSource() == views.txt_sale_product_code) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (views.txt_sale_product_code.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingresa el código del producto a vender");
                    return;
                }
                try {
                    int id = Integer.parseInt(views.txt_sale_product_code.getText().trim());
                    product = product_dao.searchCode(id);
                    if (product != null && product.getId() != 0) {
                        views.txt_sale_product_id.setText(String.valueOf(product.getId()));
                        views.txt_sale_product_name.setText(product.getName());
                        views.txt_sale_stock.setText(String.valueOf(product.getProduct_quantity()));
                        views.txt_sale_price.setText(String.valueOf(product.getUnit_price()));
                        views.txt_sale_quantity.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto no encontrado");
                        views.txt_sale_product_code.requestFocus();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Código inválido. Debe ser un número.");
                    views.txt_sale_product_code.requestFocus();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

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

    private void loadCustomerName() {
        try {
            int id = Integer.parseInt(views.txt_sale_customer_id.getText());
            String name = customer_dao.getCustomerNameById(id);

            if (name != null) {
                views.txt_sale_customer.setText(name);
            } else {
                views.txt_sale_customer.setText("Cliente no encontrado");
            }
        } catch (NumberFormatException ex) {
            views.txt_sale_customer.setText("ID inválido");
        }
    }

    private void updateSubtotal() {
        try {
            String qtyText = views.txt_sale_quantity.getText().trim();
            String priceText = views.txt_sale_price.getText().trim();

            if (!qtyText.isEmpty() && !priceText.isEmpty()) {
                int quantity = Integer.parseInt(qtyText);
                double price = Double.parseDouble(priceText);
                double subtotal = quantity * price;

                // Formatear como RD$
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "DO"));
                String formattedSubtotal = currencyFormat.format(subtotal);

                views.txt_sale_subtotal.setText(formattedSubtotal);
            } else {
                views.txt_sale_subtotal.setText("RD$0.00");
            }
        } catch (NumberFormatException e) {
            views.txt_sale_subtotal.setText("RD$0.00");
        }
    }

    private void initSubtotalListeners() {
        views.txt_sale_quantity.getDocument().addDocumentListener(new DocumentListener() {
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

        views.txt_sale_price.getDocument().addDocumentListener(new DocumentListener() {
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
}
