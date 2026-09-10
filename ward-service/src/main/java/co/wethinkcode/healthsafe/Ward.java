
package co.wethinkcode.healthsafe;

public class Ward {
    private String wardId;
    private String wing;
    private String department;
    private Integer bedsAvailable;
    private String notes;


    public Ward() {}

    public Ward(String wardId, String wing, String department, int bedsAvailable) {
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
        this.notes = notes;


    }

    public String getWardId() { return wardId; }
    public String getWing() { return wing; }
    public String getDepartment() { return department; }
    public int getBedsAvailable() { return bedsAvailable; }
    public String getNotes() { return notes; }

    public void setWardId(String wardId) { this.wardId = wardId; }
    public void setWing(String wing) { this.wing = wing; }
    public void setDepartment(String department) { this.department = department; }
    public void setBedsAvailable(int bedsAvailable) { this.bedsAvailable = bedsAvailable; }
    public void setNotes(String notes) { this.notes = notes; }
}


