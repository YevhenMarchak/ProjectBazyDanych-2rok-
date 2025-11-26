package model;

public class employees {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String hireDate;
    private int phone;
    private String email;
    public employees(int employeeId, String firstName, String lastName, String position, String hireDate, int phone, String email){
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.hireDate = hireDate;
        this.phone = phone;
        this.email = email;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getHireDate() {
        return hireDate;
    }

    public int getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}//koniec class
