import java.util.ArrayList;
import java.util.List;

public class UniversitySystem {

    public List<Student> students = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();
    public List<Enrollment> enrollments = new ArrayList<>();
    public List<Instructor> instructors = new ArrayList<>();
    public List<PaymentRecord> payments = new ArrayList<>();
    public List<String> logs = new ArrayList<>();

    public String universityName = "Metro University";

    // Categorical String Constants
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

    // Financial Constants
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

    // Academic & Threshold Constants
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

    public void enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = null;
        Course course = null;

        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(courseCode)) {
                course = currentCourse;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            logs.add("Student not found: " + studentId);
            return;
        }

        if (course == null) {
            System.out.println("Course not found");
            logs.add("Course not found: " + courseCode);
            return;
        }

        if (student.isBlocked) {
            System.out.println("Student is blocked");
            logs.add("Blocked student tried enrollment");
            return;
        }

        if (student.status.equals(STATUS_PROBATION)) {
            int count = 0;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.semester.equals(semester)) {
                    count++;
                }
            }
            if (count >= PROBATION_COURSE_LIMIT) {
                System.out.println("Probation student cannot register more than " + PROBATION_COURSE_LIMIT + " courses");
                logs.add("Probation limit reached");
                return;
            }
        }

        if (course.enrolled >= course.capacity) {
            System.out.println("Course is full");
            logs.add("Course full: " + courseCode);
            return;
        }

        if (student.outstandingBalance > MAX_UNPAID_BALANCE) {
            System.out.println("Student has unpaid balance");
            logs.add("Balance issue for " + student.id);
            return;
        }

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.semester.equals(semester)) {
                if (currentEnrollment.day.equals(course.day) && currentEnrollment.timeSlot.equals(course.timeSlot)) {
                    System.out.println("Schedule conflict");
                    logs.add("Conflict for " + studentId);
                    return;
                }
            }
        }

        if (course.prerequisite != null && !course.prerequisite.equals("")) {
            boolean passed = false;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.courseCode.equals(course.prerequisite)) {
                    if (currentEnrollment.grade != null && (currentEnrollment.grade.equals(GRADE_A) || currentEnrollment.grade.equals(GRADE_B) || currentEnrollment.grade.equals(GRADE_C))) {
                        passed = true;
                    }
                }
            }
            if (!passed) {
                System.out.println("Missing prerequisite");
                logs.add("Missing prerequisite for " + studentId);
                return;
            }
        }

        double fee = 0;
        if (student.type.equals(TYPE_LOCAL)) {
            fee = course.creditHours * RATE_LOCAL;
        } else if (student.type.equals(TYPE_INTERNATIONAL)) {
            fee = course.creditHours * RATE_INTERNATIONAL;
        } else if (student.type.equals(TYPE_SCHOLARSHIP)) {
            fee = course.creditHours * RATE_SCHOLARSHIP;
        } else {
            fee = course.creditHours * RATE_LOCAL;
        }

        if (paymentType.equals(PAYMENT_INSTALLMENT)) {
            fee = fee + FEE_INSTALLMENT;
        } else if (paymentType.equals(PAYMENT_CARD)) {
            fee = fee + FEE_CARD;
        } else if (paymentType.equals(PAYMENT_CASH)) {
            fee = fee + FEE_CASH;
        } else {
            fee = fee + FEE_DEFAULT;
        }

        if (semester.equals(SEMESTER_SUMMER)) {
            fee = fee + FEE_SUMMER;
        }

        if (courseCode.startsWith("SE")) {
            fee = fee + FEE_SE_COURSE;
        }

        student.outstandingBalance = student.outstandingBalance + fee;
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.day, course.timeSlot);
        enrollments.add(newEnrollment);
        course.enrolled++;

        System.out.println("Enrollment completed");
        System.out.println("Student: " + student.name);
        System.out.println("Course: " + course.title);
        System.out.println("Semester: " + semester);
        System.out.println("Fee charged: " + fee);
        logs.add("Enrolled " + studentId + " into " + courseCode);

        if (student.email != null && student.email.contains("@")) {
            System.out.println("Email sent to " + student.email + ": enrolled in " + course.title);
            logs.add("Enrollment email sent");
        } else {
            System.out.println("Invalid email");
            logs.add("Invalid email for " + student.id);
        }
    }

    public void assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.courseCode.equals(courseCode) && currentEnrollment.semester.equals(semester)) {
                currentEnrollment.grade = grade;
                System.out.println("Grade assigned");

                double points = 0;
                if (grade.equals(GRADE_A)) points = POINTS_A;
                else if (grade.equals(GRADE_B)) points = POINTS_B;
                else if (grade.equals(GRADE_C)) points = POINTS_C;
                else if (grade.equals(GRADE_D)) points = POINTS_D;
                else if (grade.equals(GRADE_F)) points = POINTS_F;

                Student student = null;
                Course course = null;

                for (Student currentStudent : students) {
                    if (currentStudent.id.equals(studentId)) student = currentStudent;
                }

                for (Course currentCourse : courses) {
                    if (currentCourse.code.equals(courseCode)) course = currentCourse;
                }

                if (student != null && course != null) {
                    student.totalCompletedCredits += course.creditHours;
                    student.totalGradePoints += points * course.creditHours;

                    if (student.totalCompletedCredits > 0) {
                        student.gpa = student.totalGradePoints / student.totalCompletedCredits;
                    }

                    if (student.gpa < GPA_PROBATION_THRESHOLD) {
                        student.status = STATUS_PROBATION;
                    } else if (student.gpa >= GPA_PROBATION_THRESHOLD && student.gpa < GPA_HONOR_THRESHOLD) {
                        student.status = STATUS_GOOD;
                    } else {
                        student.status = STATUS_HONOR;
                    }

                    System.out.println("Updated GPA: " + student.gpa);
                    System.out.println("Updated Status: " + student.status);

                    if (student.email != null && student.email.contains("@")) {
                        System.out.println("Email sent to " + student.email + ": grade posted");
                    } else {
                        System.out.println("Could not send grade email");
                    }
                }
            }
        }
    }

    public void processPayment(String studentId, double amount, String method) {
        Student student = null;
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid payment");
            return;
        }

        if (method.equals(PAYMENT_CARD)) {
            amount = amount - DISCOUNT_CARD;
        } else if (method.equals(PAYMENT_BANK)) {
            amount = amount - DISCOUNT_BANK;
        } else if (method.equals(PAYMENT_CASH)) {
            amount = amount - FEE_CASH;
        } else {
            amount = amount - DISCOUNT_DEFAULT;
        }

        student.outstandingBalance = student.outstandingBalance - amount;
        if (student.outstandingBalance < 0) {
            student.outstandingBalance = 0;
        }

        payments.add(new PaymentRecord(studentId, amount, method, "PAID"));

        System.out.println("Payment processed for " + student.name);
        System.out.println("Method: " + method);
        System.out.println("Amount accepted: " + amount);
        System.out.println("Remaining balance: " + student.outstandingBalance);

        if (student.email != null && student.email.contains("@")) {
            System.out.println("Email sent to " + student.email + ": payment received");
        }
    }

    public void printTranscript(String studentId) {
        Student student = null;
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + universityName);
        System.out.println("Name: " + student.name);
        System.out.println("ID: " + student.id);
        System.out.println("Department: " + student.department);
        System.out.println("Status: " + student.status);
        System.out.println("GPA: " + student.gpa);

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId)) {
                String title = "";
                int credits = 0;
                for (Course currentCourse : courses) {
                    if (currentCourse.code.equals(currentEnrollment.courseCode)) {
                        title = currentCourse.title;
                        credits = currentCourse.creditHours;
                    }
                }
                System.out.println(currentEnrollment.courseCode + " - " + title + " - " + credits + " credits - Grade: " + currentEnrollment.grade);
            }
        }

        System.out.println("Outstanding Balance: " + student.outstandingBalance);
        if (student.outstandingBalance > 0) {
            System.out.println("WARNING: unpaid dues");
        }
    }

    public void printCourseRoster(String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(courseCode)) {
                System.out.println("Course: " + currentCourse.title);
                System.out.println("Instructor: " + currentCourse.instructorName);
                System.out.println("Capacity: " + currentCourse.capacity);
                System.out.println("Enrolled: " + currentCourse.enrolled);
            }
        }

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.courseCode.equals(courseCode)) {
                for (Student currentStudent : students) {
                    if (currentStudent.id.equals(currentEnrollment.studentId)) {
                        System.out.println(currentStudent.id + " - " + currentStudent.name + " - " + currentStudent.status);
                    }
                }
            }
        }
    }

    public void printDepartmentSummary(String department) {
        System.out.println("----- DEPARTMENT SUMMARY -----");
        System.out.println("Department: " + department);

        int studentCount = 0;
        int instructorCount = 0;
        int courseCount = 0;
        double avgGpa = 0;
        int gpaCount = 0;

        for (Student currentStudent : students) {
            if (currentStudent.department.equals(department)) {
                studentCount++;
                avgGpa += currentStudent.gpa;
                gpaCount++;
            }
        }

        for (Instructor currentInstructor : instructors) {
            if (currentInstructor.department.equals(department)) {
                instructorCount++;
            }
        }

        for (Course currentCourse : courses) {
            if (currentCourse.code.startsWith(department)) {
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

    public void sendWarningLetters() {
        for (Student currentStudent : students) {
            if (currentStudent.outstandingBalance > WARNING_BALANCE_THRESHOLD || currentStudent.status.equals(STATUS_PROBATION)) {
                if (currentStudent.email != null && currentStudent.email.contains("@")) {
                    System.out.println("Sending warning email to " + currentStudent.email);
                    if (currentStudent.outstandingBalance > WARNING_BALANCE_THRESHOLD) {
                        System.out.println("Reason: unpaid balance");
                    }
                    if (currentStudent.status.equals(STATUS_PROBATION)) {
                        System.out.println("Reason: academic probation");
                    }
                    logs.add("Warning sent to " + currentStudent.id);
                } else {
                    System.out.println("Could not send warning to " + currentStudent.name);
                    logs.add("Warning failed for " + currentStudent.id);
                }
            }
        }
    }

    public Student findStudent(String id) {
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(id)) {
                return currentStudent;
            }
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(code)) {
                return currentCourse;
            }
        }
        return null;
    }
}