package model;

public class locations {

    private int location_id;
    private String location_code;
    private String location_type;
    private double max_capacity;

    public locations(int location_id,
                     String location_code,
                     String location_type,
                     double max_capacity) {

        this.location_id = location_id;
        this.location_code = location_code;
        this.location_type = location_type;
        this.max_capacity = max_capacity;
    }

    public int getLocation_id() {
        return location_id;
    }

    public String getLocation_code() {
        return location_code;
    }

    public String getLocation_type() {
        return location_type;
    }

    public double getMax_capacity() {
        return max_capacity;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }

    public void setMax_capacity(double max_capacity) {
        this.max_capacity = max_capacity;
    }
}
