package model;

public class MostShippedProduct {

    private final long productId;
    private final String productName;
    private final double totalQuantity;

    public MostShippedProduct(long productId, String productName, double totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }
}
