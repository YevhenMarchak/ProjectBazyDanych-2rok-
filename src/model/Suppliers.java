package model;

public class Suppliers {

    private int supplier_id;
    private String company_name;
    private String address;
    private String phone;
    private String email;
    private String tax_id;

    // ✅ DODAJ TO
    public Suppliers() {
    }

    public Suppliers(int supplier_id,
                     String company_name,
                     String address,
                     String phone,
                     String email,
                     String tax_id) {

        this.supplier_id = supplier_id;
        this.company_name = company_name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.tax_id = tax_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }
}
