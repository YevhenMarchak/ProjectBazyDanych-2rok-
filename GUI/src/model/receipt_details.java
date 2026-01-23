package model;

import java.math.BigDecimal;

public class receipt_details {

    private long receipt_id;
    private long product_id;
    private BigDecimal expected_quantity;
    private BigDecimal received_quantity;

    public receipt_details(long receipt_id,
                           long product_id,
                           BigDecimal expected_quantity,
                           BigDecimal received_quantity) {

        this.receipt_id = receipt_id;
        this.product_id = product_id;
        this.expected_quantity = expected_quantity;
        this.received_quantity = received_quantity;
    }

    public long getReceipt_id() {
        return receipt_id;
    }

    public long getProduct_id() {
        return product_id;
    }

    public BigDecimal getExpected_quantity() {
        return expected_quantity;
    }

    public BigDecimal getReceived_quantity() {
        return received_quantity;
    }

    public void setReceipt_id(long receipt_id) {
        this.receipt_id = receipt_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public void setExpected_quantity(BigDecimal expected_quantity) {
        this.expected_quantity = expected_quantity;
    }

    public void setReceived_quantity(BigDecimal received_quantity) {
        this.received_quantity = received_quantity;
    }
}
