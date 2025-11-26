package model;

public class Suppliers {
    private int supplier_id;
    private String company_name;
    private String address;
    private int phone;
    private String email;
    private int tax_id;
    public Suppliers(int supplier_id, String company_name, String address, int phone, String email, int tax_id)
    {
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

    public String getCompany_name() {
        return company_name;
    }

    public String getAddress() {
        return address;
    }

    public int getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getTax_id() {
        return tax_id;
    }
}
//koniec klasy