package model;

public class inventory {
    private int inventory_id;
    private int product_id;
    private int location_id;
    private int quantity;
    private String last_updated;
    public inventory(int inventory_id, int product_id, int location_id, int quantity, String last_updated)
    {
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

    public int getQuantity() {
        return quantity;
    }

    public String getLast_updated() {
        return last_updated;
    }
}
//koniec klasy