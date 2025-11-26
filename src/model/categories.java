package model;

public class categories {
    private int category_id;
    private String name;
    private  String description;
    public categories(int category_id, String name, String description)
    {
        this.category_id = category_id;
        this.name = name;
        this.description = description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
//koniec klasy