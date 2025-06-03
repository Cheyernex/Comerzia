package views;

import controllers.ReportsController;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import models.ReportsDAO;

public class ReportViewer extends javax.swing.JFrame {

    ReportsDAO reportDAO = new ReportsDAO();
    ReportsController reportsController;  // Declara el controlador como atributo

    public ReportViewer() {
        initComponents();
        setTitle("Reporte de Ventas");
        setLocationRelativeTo(null); // Centrar ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cerrar esta ventana
        setResizable(false);
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/main/resources/images/tienda.png"));
        setIconImage(appIcon.getImage());
        reportsController = new ReportsController(this, reportDAO);

        // Configuración adicional si es necesaria
        setupView();

    }

    private void setupView() {
        // Configura el cursor para el JLabel de búsqueda
        jLabelSearchReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        table_all_sales = new javax.swing.JTable();
        jLabel_print_report = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbl_Logo = new javax.swing.JLabel();
        cmb_list_report = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jdc_initial_date = new com.toedter.calendar.JDateChooser();
        jdc_final_date = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabelSearchReport = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1220, 651));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(18, 45, 61));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(201, 235, 224));

        table_all_sales.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        table_all_sales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Factura de venta", "Cliente", "Empleado", "Total", "Fecha de venta"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(table_all_sales);

        jLabel_print_report.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel_print_report.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_print_report.setText("Imprimir");
        jLabel_print_report.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel_print_report)
                        .addGap(20, 20, 20)))
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel_print_report)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 1088, 490));

        jPanel5.setBackground(new java.awt.Color(201, 235, 224));

        lbl_Logo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_Logo.setForeground(new java.awt.Color(255, 255, 255));
        lbl_Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/images/Comerzia_Logo_220x150.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        cmb_list_report.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cmb_list_report.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Reporte de Ventas", "Reporte de Compras" }));
        jPanel1.add(cmb_list_report, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 90, 320, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Reporte:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fecha Inicio:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 70, -1, -1));

        jdc_initial_date.setDateFormatString("yyyy-MM-dd");
        jdc_initial_date.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jdc_initial_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 90, 108, 30));

        jdc_final_date.setDateFormatString("yyyy-MM-dd");
        jdc_final_date.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jdc_final_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 90, 108, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Fecha Final:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 70, -1, -1));

        jLabelSearchReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/images/search.png"))); // NOI18N
        jLabelSearchReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(jLabelSearchReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 90, 30, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 1190, 650));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportViewer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox<Object> cmb_list_report;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabelSearchReport;
    public javax.swing.JLabel jLabel_print_report;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane9;
    public com.toedter.calendar.JDateChooser jdc_final_date;
    public com.toedter.calendar.JDateChooser jdc_initial_date;
    private javax.swing.JLabel lbl_Logo;
    public javax.swing.JTable table_all_sales;
    // End of variables declaration//GEN-END:variables
}
