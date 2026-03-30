import java.util.ArrayList;
import java.util.List;

public class UniversitySystem {

    private List<Student> students = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Enrollment> enrollments = new ArrayList<>();
    private List<Instructor> instructors = new ArrayList<>();
    private List<PaymentRecord> payments = new ArrayList<>();
    private List<String> logs = new ArrayList<>();

    public String universityName = "Metro University";

    public static final String TYPE_LOCAL = "LOCAL";
    public static final String TYPE_INTERNATIONAL = "INTERNATIONAL";
    public static final String TYPE_SCHOLARSHIP = "SCHOLARSHIP";
    public static final String STATUS_PROBATION = "PROBATION";
    public static final String STATUS_GOOD = "GOOD";
    public static final String STATUS_HONOR = "HONOR";
    public static final String PAYMENT_INSTALLMENT = "INSTALLMENT";
    public static final String PAYMENT_CARD = "CARD";
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_BANK = "BANK";
    public static final String SEMESTER_SUMMER = "SUMMER";
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_F = "F";

    public static final double RATE_LOCAL = 300.0;
    public static final double RATE_INTERNATIONAL = 550.0;
    public static final double RATE_SCHOLARSHIP = 100.0;
    public static final double FEE_INSTALLMENT = 50.0;
    public static final double FEE_CARD = 10.0;
    public static final double FEE_CASH = 0.0;
    public static final double FEE_DEFAULT = 100.0;
    public static final double FEE_SUMMER = 200.0;
    public static final double FEE_SE_COURSE = 75.0;
    public static final double DISCOUNT_CARD = 5.0;
    public static final double DISCOUNT_BANK = 2.0;
    public static final double DISCOUNT_DEFAULT = 10.0;

    public static final int PROBATION_COURSE_LIMIT = 2;
    public static final double MAX_UNPAID_BALANCE = 1000.0;
    public static final double WARNING_BALANCE_THRESHOLD = 500.0;
    public static final double GPA_PROBATION_THRESHOLD = 2.0;
    public static final double GPA_HONOR_THRESHOLD = 3.5;
    public static final double POINTS_A = 4.0;
    public static final double POINTS_B = 3.0;
    public static final double POINTS_C = 2.0;
    public static final double POINTS_D = 1.0;
    public static final double POINTS_F = 0.0;

    public void addStudent(Student student) { students.add(student); }
    public void addCourse(Course course) { courses.add(course); }
    public void addInstructor(Instructor instructor) { instructors.add(instructor); }

    public List<Student> getStudents() { return students; }
    public List<Course> getCourses() { return courses; }
    public List<PaymentRecord> getPayments() { return payments; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Instructor> getInstructors() { return instructors; }
    public List<String> getLogs() { return logs; }

    public Student findStudent(String id) {
        for (Student currentStudent : students) {
            if (currentStudent.getId().equals(id)) {
                return currentStudent;
            }
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course currentCourse : courses) {
            if (currentCourse.getCode().equals(code)) {
                return currentCourse;
            }
        }
        return null;
    }
}