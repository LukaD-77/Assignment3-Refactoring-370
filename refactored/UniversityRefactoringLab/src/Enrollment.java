public class Enrollment {
    private String studentId;
    private String courseCode;
    private String semester;
    private String day;
    private String timeSlot;
    private String grade;

    public Enrollment(String studentId, String courseCode, String semester, String day, String timeSlot) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.day = day;
        this.timeSlot = timeSlot;
        this.grade = "IN PROGRESS";
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public String getSemester() { return semester; }
    public String getDay() { return day; }
    public String getTimeSlot() { return timeSlot; }
    public String getGrade() { return grade; }

    // Setters
    public void setGrade(String grade) { this.grade = grade; }
}