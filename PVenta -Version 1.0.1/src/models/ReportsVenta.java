package models;

public class ReportsVenta {

    // Campos comunes
    private int id;
    private double total_to_pay;
    private String employee_name;

    // Campos para ventas
    private String sale_date;
    private String customer_name;

    // Constructores
    public ReportsVenta() {
    }

    // Para reportes de ventas
    public ReportsVenta(int id, String sale_date, double total_to_pay, String customer_name, String employee_name) {
        this.id = id;
        this.sale_date = sale_date;
        this.total_to_pay = total_to_pay;
        this.customer_name = customer_name;
        this.employee_name = employee_name;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal_to_pay() {
        return total_to_pay;
    }

    public void setTotal_to_pay(double total_to_pay) {
        this.total_to_pay = total_to_pay;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getSale_date() {
        return sale_date;
    }

    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    // Alias útiles
    public String getInvoice() {
        return String.valueOf(id);
    }

    public double getTotal() {
        return total_to_pay;
    }

    @Override
    public String toString() {
        return "Reports{"
                + "id=" + id
                + ", total=" + total_to_pay
                + ", sale_date='" + sale_date + '\''
                + ", customer='" + customer_name + '\''
                + ", employee='" + employee_name + '\''
                + '}';
    }

    public boolean isSale() {
        return sale_date != null;
    }

}
