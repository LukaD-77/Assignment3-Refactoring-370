public class Course {
    public String code;
    public String title;
    public String instructorName;
    public int creditHours;
    public int capacity;
    public int enrolled;
    public String prerequisite;
    public String day;
    public String timeSlot;

    public Course(String code, String title, String instructorName, int creditHours, int capacity,
                  String prerequisite, String day, String timeSlot) {
        this.code = code;
        this.title = title;
        this.instructorName = instructorName;
        this.creditHours = creditHours;
        this.capacity = capacity;
        this.enrolled = 0;
        this.prerequisite = prerequisite;
        this.day = day;
        this.timeSlot = timeSlot;
    }
}
