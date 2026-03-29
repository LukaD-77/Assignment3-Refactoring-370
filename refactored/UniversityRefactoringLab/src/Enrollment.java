public class Enrollment {
    public String studentId;
    public String courseCode;
    public String semester;
    public String day;
    public String timeSlot;
    public String grade;

    public Enrollment(String studentId, String courseCode, String semester, String day, String timeSlot) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semester = semester;
        this.day = day;
        this.timeSlot = timeSlot;
        this.grade = "IP";
    }
}
