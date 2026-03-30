import java.util.List;

public class LegacyReportPrinter {

    public void printStudents(List<Student> students) {
        System.out.println("---- STUDENTS ----");
        for (Student student : students) {
            System.out.println(student.getId() + " | " + student.getName() + " | " + student.getDepartment() + " | " + student.getStatus() + " | " + student.getGpa());
        }
    }

    public void printPayments(List<PaymentRecord> payments) {
        System.out.println("---- PAYMENTS ----");
        for (PaymentRecord payment : payments) {
            System.out.println(payment.getStudentId() + " | " + payment.getAmount() + " | " + payment.getMethod() + " | " + payment.getStatus());
        }
    }

    public void printCourses(List<Course> courses) {
        System.out.println("---- COURSES ----");
        for (Course course : courses) {
            System.out.println(course.getCode() + " | " + course.getTitle() + " | " + course.getInstructorName() + " | " + course.getCreditHours());
        }
    }
}