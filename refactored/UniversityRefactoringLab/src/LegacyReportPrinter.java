import java.util.List;

public class LegacyReportPrinter {

    public void printStudents(List<Student> students) {
        System.out.println("---- STUDENTS ----");
        for (Student s : students) {
            System.out.println(s.id + " | " + s.name + " | " + s.department + " | " + s.status + " | " + s.gpa);
        }
    }

    public void printPayments(List<PaymentRecord> payments) {
        System.out.println("---- PAYMENTS ----");
        for (PaymentRecord p : payments) {
            System.out.println(p.studentId + " | " + p.amount + " | " + p.method + " | " + p.status);
        }
    }

    public void printCourses(List<Course> courses) {
        System.out.println("---- COURSES ----");
        for (Course c : courses) {
            System.out.println(c.code + " | " + c.title + " | " + c.instructorName + " | " + c.creditHours);
        }
    }
}
