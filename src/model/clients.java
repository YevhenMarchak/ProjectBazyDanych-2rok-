package model;

public class clients {
    private int clientId;
    private String companyName;
    private String deliveryAddres;
    private int phone;
    private String email;
    private int taxId;

    public clients(int clientId, String companyName, String deliveryAddres, int phone, String email, int taxId){
        this.clientId = clientId;
        this.companyName = companyName;
        this.deliveryAddres = deliveryAddres;
        this.phone = phone;
        this.email = email;
        this.taxId = taxId;
    }

    public int getPhone() {
        return phone;
    }

    public String getDeliveryAddres() {
        return deliveryAddres;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getClientId() {
        return clientId;
    }

    public String getEmail() {
        return email;
    }

    public int getTaxId() {
        return taxId;
    }
}//koniec class

