package model;

public class shipment_details {
    private int shipment_id;
    private int product_id;
    private int quantity_to_ship;
    public shipment_details(int shipment_id, int product_id, int quantity_to_ship)
    {
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

    public int getQuantity_to_ship() {
        return quantity_to_ship;
    }
}
//koniec klasy