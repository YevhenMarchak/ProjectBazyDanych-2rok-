package model;

public class locations {
    private int location_id;
    private int location_code;
    private int location_type;
    private int max_capacity;
    public locations(int location_id, int location_code, int location_type, int max_capacity)
    {
        this.location_id = location_id;
        this.location_code = location_code;
        this.location_type = location_type;
        this.max_capacity = max_capacity;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getLocation_code() {
        return location_code;
    }

    public int getLocation_type() {
        return location_type;
    }

    public int getMax_capacity() {
        return max_capacity;
    }
}
//koniec klasy
