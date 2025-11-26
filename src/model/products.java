package model;

public class products {
    private int product_id;
    private int sku;
    private String name;
    private String description;
    private int category_id;
    private int supplier_id;
    private int weight;
    private int dimensions;
    public products(int product_id, int sku, String name, String description, int category_id, int supplier_id, int weight, int dimensions)
    {
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

    public int getSku() {
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

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getWeight() {
        return weight;
    }

    public int getDimensions() {
        return dimensions;
    }
}
//koniec klasy
