package model;

public class receipt_details {
    private int receipt_id;
    private int product_id;
    private int expected_quantity;
    private int received_quantity;
    public receipt_details(int receipt_id, int product_id, int expected_quantity, int received_quantity)
    {
        this.receipt_id = receipt_id;
        this.product_id = product_id;
        this.expected_quantity = expected_quantity;
        this.received_quantity = received_quantity;
    }

    public int getReceipt_id() {
        return receipt_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getExpected_quantity() {
        return expected_quantity;
    }

    public int getReceived_quantity() {
        return received_quantity;
    }
}
//koniec klasy