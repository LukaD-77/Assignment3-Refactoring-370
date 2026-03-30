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

    public String enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = findStudent(studentId);
        Course course = findCourse(courseCode);

        if (student == null) {
            logs.add("Student not found: " + studentId);
            return "Error: Student not found.";
        }

        if (course == null) {
            logs.add("Course not found: " + courseCode);
            return "Error: Course not found.";
        }

        if (student.isBlocked()) {
            logs.add("Blocked student tried enrollment");
            return "Error: Student is blocked.";
        }

        if (student.getStatus().equals(STATUS_PROBATION)) {
            int count = 0;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                    count++;
                }
            }
            if (count >= PROBATION_COURSE_LIMIT) {
                logs.add("Probation limit reached");
                return "Error: Probation student cannot register for more than " + PROBATION_COURSE_LIMIT + " courses.";
            }
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            logs.add("Course full: " + courseCode);
            return "Error: Course is full.";
        }

        if (student.getOutstandingBalance() > MAX_UNPAID_BALANCE) {
            logs.add("Balance issue for " + student.getId());
            return "Error: Student has unpaid balance.";
        }

        if (hasScheduleConflict(studentId, course, semester)) {
            logs.add("Conflict for " + studentId);
            return "Error: Schedule conflict detected.";
        }

        if (!hasPassedPrerequisite(studentId, course.getPrerequisite())) {
            logs.add("Missing prerequisite for " + studentId);
            return "Error: Missing prerequisite for the course.";
        }

        double fee = calculateEnrollmentFee(student, course, paymentType, semester);

        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.getDay(), course.getTimeSlot());
        enrollments.add(newEnrollment);
        course.incrementEnrollment();

        StringBuilder response = new StringBuilder();
        response.append("Enrollment completed.\n");
        response.append("Student: ").append(student.getName()).append("\n");
        response.append("Course: ").append(course.getTitle()).append("\n");
        response.append("Semester: ").append(semester).append("\n");
        response.append("Fee charged: $").append(fee);
        logs.add("Enrolled " + studentId + " into " + courseCode);

        if (isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": enrolled in ").append(course.getTitle());
            logs.add("Enrollment email sent");
        } else {
            response.append("\nInvalid email. Could not send notification.");
            logs.add("Invalid email for " + student.getId());
        }

        return response.toString();
    }

    public String assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(courseCode) && currentEnrollment.getSemester().equals(semester)) {
                currentEnrollment.setGrade(grade);

                double points = getGradePoints(grade);
                Student student = findStudent(studentId);
                Course course = findCourse(courseCode);

                if (student != null && course != null) {
                    student.setTotalCompletedCredits(student.getTotalCompletedCredits() + course.getCreditHours());
                    student.setTotalGradePoints(student.getTotalGradePoints() + (points * course.getCreditHours()));

                    updateStudentAcademicStatus(student);

                    StringBuilder response = new StringBuilder();
                    response.append("Grade assigned successfully.\n");
                    response.append("Updated GPA: ").append(student.getGpa()).append("\n");
                    response.append("Updated Status: ").append(student.getStatus());

                    if (isValidEmail(student.getEmail())) {
                        response.append("\nEmail sent to ").append(student.getEmail()).append(": grade posted");
                    } else {
                        response.append("\nCould not send grade email.");
                    }
                    return response.toString();
                }
            }
        }
        return "Error: Enrollment record not found.";
    }

    public String processPayment(String studentId, double amount, String method) {
        Student student = findStudent(studentId);

        if (student == null) {
            return "Error: Student not found.";
        }

        if (amount <= 0) {
            return "Error: Invalid payment amount.";
        }

        amount = calculateFinalPaymentAmount(amount, method);

        student.setOutstandingBalance(student.getOutstandingBalance() - amount);
        if (student.getOutstandingBalance() < 0) {
            student.setOutstandingBalance(0);
        }

        payments.add(new PaymentRecord(studentId, amount, method, "PAID"));

        StringBuilder response = new StringBuilder();
        response.append("Payment processed for ").append(student.getName()).append("\n");
        response.append("Method: ").append(method).append("\n");
        response.append("Amount accepted: $").append(amount).append("\n");
        response.append("Remaining balance: $").append(student.getOutstandingBalance());

        if (isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": payment received");
        }
        return response.toString();
    }

    public String sendWarningLetters() {
        StringBuilder response = new StringBuilder();
        int sentCount = 0;
        int failedCount = 0;

        for (Student currentStudent : students) {
            if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD || currentStudent.getStatus().equals(STATUS_PROBATION)) {
                if (isValidEmail(currentStudent.getEmail())) {
                    response.append("Sending warning email to ").append(currentStudent.getEmail()).append(" - ");
                    if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD) {
                        response.append("Reason: Unpaid balance. ");
                    }
                    if (currentStudent.getStatus().equals(STATUS_PROBATION)) {
                        response.append("Reason: Academic probation.");
                    }
                    response.append("\n");
                    logs.add("Warning sent to " + currentStudent.getId());
                    sentCount++;
                } else {
                    response.append("Could not send warning to ").append(currentStudent.getName()).append(" (Invalid Email)\n");
                    logs.add("Warning failed for " + currentStudent.getId());
                    failedCount++;
                }
            }
        }
        response.append("Summary: ").append(sentCount).append(" letters sent, ").append(failedCount).append(" failed.");
        return response.toString();
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