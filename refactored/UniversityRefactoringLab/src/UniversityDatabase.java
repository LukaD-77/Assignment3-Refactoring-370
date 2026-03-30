import java.util.ArrayList;
import java.util.List;

public class UniversityDatabase {

    private List<Student> students = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Enrollment> enrollments = new ArrayList<>();
    private List<Instructor> instructors = new ArrayList<>();
    private List<PaymentRecord> payments = new ArrayList<>();
    private List<String> logs = new ArrayList<>();

    public String universityName = "Metro University";

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
            if (currentStudent.getId().equals(id)) return currentStudent;
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course currentCourse : courses) {
            if (currentCourse.getCode().equals(code)) return currentCourse;
        }
        return null;
    }
}