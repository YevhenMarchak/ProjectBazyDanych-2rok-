package model;

public class products {

    private int product_id;
    private String sku;
    private String name;
    private String description;
    private int category_id;
    private Integer supplier_id;
    private double weight;
    private String dimensions;

    public products(int product_id,
                    String sku,
                    String name,
                    String description,
                    int category_id,
                    Integer supplier_id,
                    double weight,
                    String dimensions) {

        this.product_id = product_id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category_id = category_id;
        this.supplier_id = supplier_id;
        this.weight = weight;
        this.dimensions = dimensions;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public double getWeight() {
        return weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
}
