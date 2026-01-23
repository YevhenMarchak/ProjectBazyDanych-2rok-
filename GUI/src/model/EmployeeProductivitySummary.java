package model;

public class EmployeeProductivitySummary {

    private final long employeeId;
    private final String fullName;
    private final int clientsCount;
    private final String productivityLevel;

    public EmployeeProductivitySummary(
            long employeeId,
            String fullName,
            int clientsCount,
            String productivityLevel
    ) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.clientsCount = clientsCount;
        this.productivityLevel = productivityLevel;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getClientsCount() {
        return clientsCount;
    }

    public String getProductivityLevel() {
        return productivityLevel;
    }
}
