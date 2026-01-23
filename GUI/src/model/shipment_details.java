package model;

public class shipment_details {

    private int shipment_id;
    private int product_id;
    private double quantity_to_ship;

    public shipment_details(int shipment_id, int product_id, double quantity_to_ship) {
        this.shipment_id = shipment_id;
        this.product_id = product_id;
        this.quantity_to_ship = quantity_to_ship;
    }

    public int getShipment_id() {
        return shipment_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public double getQuantity_to_ship() {
        return quantity_to_ship;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setQuantity_to_ship(double quantity_to_ship) {
        this.quantity_to_ship = quantity_to_ship;
    }
}
