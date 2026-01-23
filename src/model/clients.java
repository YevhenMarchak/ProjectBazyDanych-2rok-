package model;

public class clients {

    private int client_id;
    private String company_name;
    private String delivery_address;
    private String phone;
    private String email;
    private String tax_id;

    public clients(int client_id,
                   String company_name,
                   String delivery_address,
                   String phone,
                   String email,
                   String tax_id) {

        this.client_id = client_id;
        this.company_name = company_name;
        this.delivery_address = delivery_address;
        this.phone = phone;
        this.email = email;
        this.tax_id = tax_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }
}
