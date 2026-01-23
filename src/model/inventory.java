package model;

import java.time.LocalDateTime;

public class inventory {

    private int inventory_id;
    private int product_id;
    private int location_id;
    private double quantity;
    private LocalDateTime last_updated;

    public inventory(int inventory_id,
                     int product_id,
                     int location_id,
                     double quantity,
                     LocalDateTime last_updated) {

        this.inventory_id = inventory_id;
        this.product_id = product_id;
        this.location_id = location_id;
        this.quantity = quantity;
        this.last_updated = last_updated;
    }

    public int getInventory_id() {
        return inventory_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public LocalDateTime getLast_updated() {
        return last_updated;
    }

    public void setInventory_id(int inventory_id) {
        this.inventory_id = inventory_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setLast_updated(LocalDateTime last_updated) {
        this.last_updated = last_updated;
    }
}
