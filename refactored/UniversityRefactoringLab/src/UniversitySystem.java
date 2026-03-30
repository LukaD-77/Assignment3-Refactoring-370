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

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean hasScheduleConflict(String studentId, Course course, String semester) {
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                if (currentEnrollment.getDay().equals(course.getDay()) && currentEnrollment.getTimeSlot().equals(course.getTimeSlot())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPassedPrerequisite(String studentId, String prerequisite) {
        if (prerequisite == null || prerequisite.isEmpty()) {
            return true;
        }
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(prerequisite)) {
                String g = currentEnrollment.getGrade();
                if (g != null && (g.equals(GRADE_A) || g.equals(GRADE_B) || g.equals(GRADE_C))) {
                    return true;
                }
            }
        }
        return false;
    }

    private double calculateEnrollmentFee(Student student, Course course, String paymentType, String semester) {
        double fee = 0;
        if (student.getType().equals(TYPE_LOCAL)) fee = course.getCreditHours() * RATE_LOCAL;
        else if (student.getType().equals(TYPE_INTERNATIONAL)) fee = course.getCreditHours() * RATE_INTERNATIONAL;
        else if (student.getType().equals(TYPE_SCHOLARSHIP)) fee = course.getCreditHours() * RATE_SCHOLARSHIP;
        else fee = course.getCreditHours() * RATE_LOCAL;

        if (paymentType.equals(PAYMENT_INSTALLMENT)) fee += FEE_INSTALLMENT;
        else if (paymentType.equals(PAYMENT_CARD)) fee += FEE_CARD;
        else if (paymentType.equals(PAYMENT_CASH)) fee += FEE_CASH;
        else fee += FEE_DEFAULT;

        if (semester.equals(SEMESTER_SUMMER)) fee += FEE_SUMMER;
        if (course.getCode().startsWith("SE")) fee += FEE_SE_COURSE;

        return fee;
    }

    private double getGradePoints(String grade) {
        if (grade.equals(GRADE_A)) return POINTS_A;
        if (grade.equals(GRADE_B)) return POINTS_B;
        if (grade.equals(GRADE_C)) return POINTS_C;
        if (grade.equals(GRADE_D)) return POINTS_D;
        return POINTS_F;
    }

    private void updateStudentAcademicStatus(Student student) {
        if (student.getTotalCompletedCredits() > 0) {
            student.setGpa(student.getTotalGradePoints() / student.getTotalCompletedCredits());
        }

        if (student.getGpa() < GPA_PROBATION_THRESHOLD) {
            student.setStatus(STATUS_PROBATION);
        } else if (student.getGpa() >= GPA_PROBATION_THRESHOLD && student.getGpa() < GPA_HONOR_THRESHOLD) {
            student.setStatus(STATUS_GOOD);
        } else {
            student.setStatus(STATUS_HONOR);
        }
    }

    private double calculateFinalPaymentAmount(double amount, String method) {
        if (method.equals(PAYMENT_CARD)) return amount - DISCOUNT_CARD;
        if (method.equals(PAYMENT_BANK)) return amount - DISCOUNT_BANK;
        if (method.equals(PAYMENT_CASH)) return amount - FEE_CASH;
        return amount - DISCOUNT_DEFAULT;
    }

    public void enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = findStudent(studentId);
        Course course = findCourse(courseCode);

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

        if (student.isBlocked()) {
            System.out.println("Student is blocked");
            logs.add("Blocked student tried enrollment");
            return;
        }

        if (student.getStatus().equals(STATUS_PROBATION)) {
            int count = 0;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                    count++;
                }
            }
            if (count >= PROBATION_COURSE_LIMIT) {
                System.out.println("Probation student cannot register more than " + PROBATION_COURSE_LIMIT + " courses");
                logs.add("Probation limit reached");
                return;
            }
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            System.out.println("Course is full");
            logs.add("Course full: " + courseCode);
            return;
        }

        if (student.getOutstandingBalance() > MAX_UNPAID_BALANCE) {
            System.out.println("Student has unpaid balance");
            logs.add("Balance issue for " + student.getId());
            return;
        }

        if (hasScheduleConflict(studentId, course, semester)) {
            System.out.println("Schedule conflict");
            logs.add("Conflict for " + studentId);
            return;
        }

        if (!hasPassedPrerequisite(studentId, course.getPrerequisite())) {
            System.out.println("Missing prerequisite");
            logs.add("Missing prerequisite for " + studentId);
            return;
        }

        double fee = calculateEnrollmentFee(student, course, paymentType, semester);

        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.getDay(), course.getTimeSlot());
        enrollments.add(newEnrollment);
        course.incrementEnrollment();

        System.out.println("Enrollment completed");
        System.out.println("Student: " + student.getName());
        System.out.println("Course: " + course.getTitle());
        System.out.println("Semester: " + semester);
        System.out.println("Fee charged: " + fee);
        logs.add("Enrolled " + studentId + " into " + courseCode);

        if (isValidEmail(student.getEmail())) {
            System.out.println("Email sent to " + student.getEmail() + ": enrolled in " + course.getTitle());
            logs.add("Enrollment email sent");
        } else {
            System.out.println("Invalid email");
            logs.add("Invalid email for " + student.getId());
        }
    }

    public void assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(courseCode) && currentEnrollment.getSemester().equals(semester)) {
                currentEnrollment.setGrade(grade);
                System.out.println("Grade assigned");

                double points = getGradePoints(grade);
                Student student = findStudent(studentId);
                Course course = findCourse(courseCode);

                if (student != null && course != null) {
                    student.setTotalCompletedCredits(student.getTotalCompletedCredits() + course.getCreditHours());
                    student.setTotalGradePoints(student.getTotalGradePoints() + (points * course.getCreditHours()));

                    updateStudentAcademicStatus(student);

                    System.out.println("Updated GPA: " + student.getGpa());
                    System.out.println("Updated Status: " + student.getStatus());

                    if (isValidEmail(student.getEmail())) {
                        System.out.println("Email sent to " + student.getEmail() + ": grade posted");
                    } else {
                        System.out.println("Could not send grade email");
                    }
                }
            }
        }
    }

    public void processPayment(String studentId, double amount, String method) {
        Student student = findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid payment");
            return;
        }

        amount = calculateFinalPaymentAmount(amount, method);

        student.setOutstandingBalance(student.getOutstandingBalance() - amount);
        if (student.getOutstandingBalance() < 0) {
            student.setOutstandingBalance(0);
        }

        payments.add(new PaymentRecord(studentId, amount, method, "PAID"));

        System.out.println("Payment processed for " + student.getName());
        System.out.println("Method: " + method);
        System.out.println("Amount accepted: " + amount);
        System.out.println("Remaining balance: " + student.getOutstandingBalance());

        if (isValidEmail(student.getEmail())) {
            System.out.println("Email sent to " + student.getEmail() + ": payment received");
        }
    }

    public void printTranscript(String studentId) {
        Student student = findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found");
            return;
        }
        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + universityName);
        System.out.println("Name: " + student.getName());
        System.out.println("ID: " + student.getId());
        System.out.println("Department: " + student.getDepartment());
        System.out.println("Status: " + student.getStatus());
        System.out.println("GPA: " + student.getGpa());
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getStudentId().equals(studentId)) {
                String title = "";
                int credits = 0;
                Course course = findCourse(currentEnrollment.getCourseCode());
                if (course != null) {
                    title = course.getTitle();
                    credits = course.getCreditHours();
                }
                System.out.println(currentEnrollment.getCourseCode() + " - " + title + " - " + credits + " credits - Grade: " + currentEnrollment.getGrade());
            }
        }
        System.out.println("Outstanding Balance: " + student.getOutstandingBalance());
        if (student.getOutstandingBalance() > 0) {
            System.out.println("WARNING: unpaid dues");
        }
    }

    public void printCourseRoster(String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        Course currentCourse = findCourse(courseCode);
        if (currentCourse != null) {
            System.out.println("Course: " + currentCourse.getTitle());
            System.out.println("Instructor: " + currentCourse.getInstructorName());
            System.out.println("Capacity: " + currentCourse.getCapacity());
            System.out.println("Enrolled: " + currentCourse.getEnrolled());
        }
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getCourseCode().equals(courseCode)) {
                Student student = findStudent(currentEnrollment.getStudentId());
                if (student != null) {
                    System.out.println(student.getId() + " - " + student.getName() + " - " + student.getStatus());
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
            if (currentStudent.getDepartment().equals(department)) {
                studentCount++;
                avgGpa += currentStudent.getGpa();
                gpaCount++;
            }
        }
        for (Instructor currentInstructor : instructors) {
            if (currentInstructor.getDepartment().equals(department)) {
                instructorCount++;
            }
        }
        for (Course currentCourse : courses) {
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

    public void sendWarningLetters() {
        for (Student currentStudent : students) {
            if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD || currentStudent.getStatus().equals(STATUS_PROBATION)) {
                if (isValidEmail(currentStudent.getEmail())) {
                    System.out.println("Sending warning email to " + currentStudent.getEmail());
                    if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD) {
                        System.out.println("Reason: unpaid balance");
                    }
                    if (currentStudent.getStatus().equals(STATUS_PROBATION)) {
                        System.out.println("Reason: academic probation");
                    }
                    logs.add("Warning sent to " + currentStudent.getId());
                } else {
                    System.out.println("Could not send warning to " + currentStudent.getName());
                    logs.add("Warning failed for " + currentStudent.getId());
                }
            }
        }
    }

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