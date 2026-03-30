import java.util.List;

public class ReportPrinter {

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

    public void printTranscript(UniversityDatabase database, String studentId) {
        Student student = database.findStudent(studentId);
        if (student == null) {
            System.out.println("Error: Student not found.");
            return;
        }

        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + database.universityName);
        System.out.println("Name: " + student.getName());
        System.out.println("ID: " + student.getId());
        System.out.println("Department: " + student.getDepartment());
        System.out.println("Status: " + student.getStatus());
        System.out.println("GPA: " + student.getGpa());

        for (Enrollment currentEnrollment : database.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId)) {
                String title = "";
                int credits = 0;
                Course course = database.findCourse(currentEnrollment.getCourseCode());
                if (course != null) {
                    title = course.getTitle();
                    credits = course.getCreditHours();
                }
                System.out.println(currentEnrollment.getCourseCode() + " - " + title + " - " + credits + " credits - Grade: " + currentEnrollment.getGrade());
            }
        }

        System.out.println("Outstanding Balance: $" + student.getOutstandingBalance());
        if (student.getOutstandingBalance() > 0) {
            System.out.println("WARNING: Unpaid dues");
        }
    }

    public void printCourseRoster(UniversityDatabase database, String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        Course currentCourse = database.findCourse(courseCode);

        if (currentCourse != null) {
            System.out.println("Course: " + currentCourse.getTitle());
            System.out.println("Instructor: " + currentCourse.getInstructorName());
            System.out.println("Capacity: " + currentCourse.getCapacity());
            System.out.println("Enrolled: " + currentCourse.getEnrolled());
        } else {
            System.out.println("Error: Course not found.");
            return;
        }

        System.out.println("-- Students --");
        for (Enrollment currentEnrollment : database.getEnrollments()) {
            if (currentEnrollment.getCourseCode().equals(courseCode)) {
                Student student = database.findStudent(currentEnrollment.getStudentId());
                if (student != null) {
                    System.out.println(student.getId() + " - " + student.getName() + " - " + student.getStatus());
                }
            }
        }
    }

    public void printDepartmentSummary(UniversityDatabase database, String department) {
        System.out.println("----- DEPARTMENT SUMMARY -----");
        System.out.println("Department: " + department);

        int studentCount = 0;
        int instructorCount = 0;
        int courseCount = 0;
        double avgGpa = 0;
        int gpaCount = 0;

        for (Student currentStudent : database.getStudents()) {
            if (currentStudent.getDepartment().equals(department)) {
                studentCount++;
                avgGpa += currentStudent.getGpa();
                gpaCount++;
            }
        }

        for (Instructor currentInstructor : database.getInstructors()) {
            if (currentInstructor.getDepartment().equals(department)) {
                instructorCount++;
            }
        }

        for (Course currentCourse : database.getCourses()) {
            if (currentCourse.getCode().startsWith(department)) {
                courseCount++;
            }
        }

        if (gpaCount > 0) {
            avgGpa = avgGpa / gpaCount;
        }

        System.out.println("Students: " + studentCount);
        System.out.println("Instructors: " + instructorCount);
        System.out.println("Courses: " + courseCount);
        System.out.println("Average GPA: " + avgGpa);
    }
}