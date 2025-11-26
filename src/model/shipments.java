package model;

public class shipments {
    private int shipment_id;
    private int client_id;
    private int employee_id;
    private int shipment_date;
    private int client_order_no;
    private int status;
    public shipments(int shipment_id, int client_id, int employee_id, int shipment_date, int client_order_no, int status)
    {
        this.shipment_id = shipment_id;
        this.client_id = client_id;
        this.employee_id = employee_id;
        this.shipment_date = shipment_date;
        this.client_order_no = client_order_no;
        this.status = status;
    }

    public int getShipment_id() {
        return shipment_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public int getShipment_date() {
        return shipment_date;
    }

    public int getClient_order_no() {
        return client_order_no;
    }

    public int getStatus() {
        return status;
    }
}
//koniec klasy