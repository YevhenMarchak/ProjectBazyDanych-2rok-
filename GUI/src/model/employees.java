package model;

import java.time.LocalDate;

public class employees {

    private int employee_id;
    private String first_name;
    private String last_name;
    private String position;
    private LocalDate hire_date;
    private String phone;
    private String email;

    public employees(int employee_id,
                     String first_name,
                     String last_name,
                     String position,
                     LocalDate hire_date,
                     String phone,
                     String email) {

        this.employee_id = employee_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.position = position;
        this.hire_date = hire_date;
        this.phone = phone;
        this.email = email;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getHire_date() {
        return hire_date;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setHire_date(LocalDate hire_date) {
        this.hire_date = hire_date;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
