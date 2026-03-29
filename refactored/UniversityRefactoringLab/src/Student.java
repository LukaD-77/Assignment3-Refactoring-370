public class Student {
    public String id;
    public String name;
    public String email;
    public String department;
    public String type;
    public String status;
    public boolean isBlocked;
    public double outstandingBalance;
    public int totalCompletedCredits;
    public double totalGradePoints;
    public double gpa;

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
}
