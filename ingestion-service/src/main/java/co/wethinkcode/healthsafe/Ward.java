package co.wethinkcode.healthsafe;

public class Ward {
    private final String wardId;
    private final String wing;
    private final String department;
    private final int bedsAvailable;

    public Ward(String wardId, String wing, String department, int bedsAvailable) {
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
    }

    public String getWardId() { return wardId; }
    public String getWing() { return wing; }
    public String getDepartment() { return department; }
    public int getBedsAvailable() { return bedsAvailable; }
}