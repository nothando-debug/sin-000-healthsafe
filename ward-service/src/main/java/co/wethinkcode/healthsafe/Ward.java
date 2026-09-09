
package co.wethinkcode.healthsafe;

public class Ward {
    private String wardId;
    private String wing;
    private String department;
    private int bedsAvailable;

    public Ward() {}

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
    
    public void setWardId(String wardId) { this.wardId = wardId; }
    public void setWing(String wing) { this.wing = wing; }
    public void setDepartment(String department) { this.department = department; }
    public void setBedsAvailable(int bedsAvailable) { this.bedsAvailable = bedsAvailable; }
} 
    

