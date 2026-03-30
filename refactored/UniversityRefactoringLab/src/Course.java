public class Course {
    private String code;
    private String title;
    private String instructorName;
    private int creditHours;
    private int capacity;
    private int enrolled;
    private String prerequisite;
    private String day;
    private String timeSlot;

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

    // Getters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getInstructorName() { return instructorName; }
    public int getCreditHours() { return creditHours; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }
    public String getPrerequisite() { return prerequisite; }
    public String getDay() { return day; }
    public String getTimeSlot() { return timeSlot; }

    // Behavior Method (Replaces direct setter)
    public void incrementEnrollment() { this.enrolled++; }
}
