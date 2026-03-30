public class Student {
    private String id;
    private String name;
    private String email;
    private String department;
    private String type;
    private String status;
    private boolean isBlocked;
    private double outstandingBalance;
    private int totalCompletedCredits;
    private double totalGradePoints;
    private double gpa;

    public Student(String id, String name, String email, String department, String type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.type = type;
        this.status = "GOOD";
        this.isBlocked = false;
        this.outstandingBalance = 0;
        this.totalCompletedCredits = 0;
        this.totalGradePoints = 0;
        this.gpa = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getType() { return type; }
    public boolean isBlocked() { return isBlocked; }
    public String getStatus() { return status; }
    public double getOutstandingBalance() { return outstandingBalance; }
    public int getTotalCompletedCredits() { return totalCompletedCredits; }
    public double getTotalGradePoints() { return totalGradePoints; }
    public double getGpa() { return gpa; }

    // Setters
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
    public void setStatus(String status) { this.status = status; }
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    public void setTotalCompletedCredits(int totalCompletedCredits) { this.totalCompletedCredits = totalCompletedCredits; }
    public void setTotalGradePoints(double totalGradePoints) { this.totalGradePoints = totalGradePoints; }
    public void setGpa(double gpa) { this.gpa = gpa; }
}
