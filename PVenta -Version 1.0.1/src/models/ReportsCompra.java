package models;

public class ReportsCompra {

    // Campos comunes
    private int id;
    private double total_to_pay;
    private String employee_name;

    // Campos para compras
    private String created;
    private String supplier_name;

    public ReportsCompra() {
    }

    public ReportsCompra(int id, double total_to_pay, String employee_name, String created, String supplier_name) {
        this.id = id;
        this.total_to_pay = total_to_pay;
        this.employee_name = employee_name;
        this.created = created;
        this.supplier_name = supplier_name;
    }

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    @Override
    public String toString() {
        return "Reports{"
                + "id=" + id
                + ", total=" + total_to_pay
                + ", created='" + created + '\''
                + ", supplier='" + supplier_name + '\''
                + ", employee='" + employee_name + '\''
                + '}';
    }
        // Alias útiles
    public String getInvoice() {
        return String.valueOf(id);
    }

    public double getTotal() {
        return total_to_pay;
    }

    public boolean isPurchase() {
        return created != null;
    }
}
