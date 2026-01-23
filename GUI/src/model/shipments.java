package model;

import java.time.LocalDate;

public class shipments {

    private int shipment_id;
    private int client_id;
    private Integer employee_id;
    private LocalDate shipment_date;
    private String client_order_no;
    private String status;

    public shipments(int shipment_id,
                     int client_id,
                     Integer employee_id,
                     LocalDate shipment_date,
                     String client_order_no,
                     String status) {

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

    public Integer getEmployee_id() {
        return employee_id;
    }

    public LocalDate getShipment_date() {
        return shipment_date;
    }

    public String getClient_order_no() {
        return client_order_no;
    }

    public String getStatus() {
        return status;
    }
    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setEmployee_id(Integer employee_id) {
        this.employee_id = employee_id;
    }

    public void setShipment_date(LocalDate shipment_date) {
        this.shipment_date = shipment_date;
    }

    public void setClient_order_no(String client_order_no) {
        this.client_order_no = client_order_no;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
