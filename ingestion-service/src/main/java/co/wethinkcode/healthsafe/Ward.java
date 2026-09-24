package co.wethinkcode.healthsafe;

public class Ward {
    private String wardId;
    private String wing;
    private String department;
    private Integer bedsAvailable;
    private String notes;


    public Ward(){}

    public Ward(String wardId, String wing, String department, Integer bedsAvailable, String notes) {
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
        this.notes = notes;

    }

    public String getWardId() { return wardId; }
    public String getWing() { return wing; }
    public String getDepartment() { return department; }
    public Integer getBedsAvailable() { return bedsAvailable; }
    public void setNotes(String notes) { this.notes = notes; }
}