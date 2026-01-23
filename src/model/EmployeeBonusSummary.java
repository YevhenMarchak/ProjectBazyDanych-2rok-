package model;

public class EmployeeBonusSummary {

    private final String firstName;
    private final String lastName;
    private final int shipmentsCount;
    private final int bonusPercent;

    public EmployeeBonusSummary(
            String firstName,
            String lastName,
            int shipmentsCount,
            int bonusPercent
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.shipmentsCount = shipmentsCount;
        this.bonusPercent = bonusPercent;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getShipmentsCount() {
        return shipmentsCount;
    }

    public int getBonusPercent() {
        return bonusPercent;
    }
}
