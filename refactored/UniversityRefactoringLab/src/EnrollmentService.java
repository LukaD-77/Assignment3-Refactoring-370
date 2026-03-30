import java.util.Optional;

public class EnrollmentService {

    public static final String SEMESTER_SUMMER = "SUMMER";
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_F = "F";

    public static final int PROBATION_COURSE_LIMIT = 2;
    public static final double MAX_UNPAID_BALANCE = 1000.0;
    public static final double GPA_PROBATION_THRESHOLD = 2.0;
    public static final double GPA_HONOR_THRESHOLD = 3.5;

    public static final double POINTS_A = 4.0;
    public static final double POINTS_B = 3.0;
    public static final double POINTS_C = 2.0;
    public static final double POINTS_D = 1.0;
    public static final double POINTS_F = 0.0;

    private UniversityDatabase database;
    private NotificationService notificationService;

    public EnrollmentService(UniversityDatabase database, NotificationService notificationService) {
        this.database = database;
        this.notificationService = notificationService;
    }

    // -------------------------------------------------------------------------
    // Public Methods (The Orchestrators)
    // -------------------------------------------------------------------------

    public String enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = database.findStudent(studentId);
        Course course = database.findCourse(courseCode);

        String lookupError = validateLookup(student, course, studentId, courseCode);
        if (lookupError != null) return lookupError;

        String eligibilityError = validateEligibility(student, course, studentId, semester);
        if (eligibilityError != null) return eligibilityError;

        double fee = calculateEnrollmentFee(student, course, paymentType, semester);
        commitEnrollment(student, course, semester, fee);

        return buildEnrollmentResponse(student, course, semester, fee);
    }

    public String assignGrade(String studentId, String courseCode, String semester, String grade) {
        Enrollment enrollment = findEnrollment(studentId, courseCode, semester);
        if (enrollment == null) return "Error: Enrollment record not found.";

        Student student = database.findStudent(studentId);
        Course course = database.findCourse(courseCode);
        if (student == null || course == null) return "Error: System data missing.";

        enrollment.setGrade(grade);
        applyGradeToStudent(student, course, grade);
        updateStudentAcademicStatus(student);

        return buildGradeResponse(student);
    }

    // -------------------------------------------------------------------------
    // Validation Helpers
    // -------------------------------------------------------------------------

    private String validateLookup(Student student, Course course, String studentId, String courseCode) {
        if (student == null) {
            database.getLogs().add("Student not found: " + studentId);
            return "Error: Student not found.";
        }
        if (course == null) {
            database.getLogs().add("Course not found: " + courseCode);
            return "Error: Course not found.";
        }
        return null;
    }

    private String validateEligibility(Student student, Course course, String studentId, String semester) {
        if (student.isBlocked()) {
            database.getLogs().add("Blocked student tried enrollment");
            return "Error: Student is blocked.";
        }
        if (isProbationLimitReached(student, studentId, semester)) {
            database.getLogs().add("Probation limit reached");
            return "Error: Probation student cannot register for more than " + PROBATION_COURSE_LIMIT + " courses.";
        }
        if (course.getEnrolled() >= course.getCapacity()) {
            database.getLogs().add("Course full: " + course.getCode());
            return "Error: Course is full.";
        }
        if (student.getOutstandingBalance() > MAX_UNPAID_BALANCE) {
            database.getLogs().add("Balance issue for " + student.getId());
            return "Error: Student has unpaid balance.";
        }
        if (hasScheduleConflict(studentId, course, semester)) {
            database.getLogs().add("Conflict for " + studentId);
            return "Error: Schedule conflict detected.";
        }
        if (!hasPassedPrerequisite(studentId, course.getPrerequisite())) {
            database.getLogs().add("Missing prerequisite for " + studentId);
            return "Error: Missing prerequisite for the course.";
        }
        return null;
    }

    private boolean isProbationLimitReached(Student student, String studentId, String semester) {
        if (!student.getStatus().equals(Student.STATUS_PROBATION)) return false;

        long semesterEnrollmentCount = database.getEnrollments().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getSemester().equals(semester))
                .count();

        return semesterEnrollmentCount >= PROBATION_COURSE_LIMIT;
    }

    private boolean hasScheduleConflict(String studentId, Course course, String semester) {
        return database.getEnrollments().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getSemester().equals(semester))
                .anyMatch(e -> e.getDay().equals(course.getDay()) && e.getTimeSlot().equals(course.getTimeSlot()));
    }

    private boolean hasPassedPrerequisite(String studentId, String prerequisite) {
        if (prerequisite == null || prerequisite.isEmpty()) return true;

        return database.getEnrollments().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equals(prerequisite))
                .anyMatch(e -> isPassingGrade(e.getGrade()));
    }

    private boolean isPassingGrade(String grade) {
        return grade != null && (grade.equals(GRADE_A) || grade.equals(GRADE_B) || grade.equals(GRADE_C));
    }

    // -------------------------------------------------------------------------
    // Fee Calculation Helpers
    // -------------------------------------------------------------------------

    private double calculateEnrollmentFee(Student student, Course course, String paymentType, String semester) {
        double fee = getBaseRate(student.getType()) * course.getCreditHours();
        fee += getPaymentTypeFee(paymentType);

        if (semester.equals(SEMESTER_SUMMER)) fee += PaymentService.FEE_SUMMER;
        if (course.getCode().startsWith("SE")) fee += PaymentService.FEE_SE_COURSE;

        return fee;
    }

    private double getBaseRate(String studentType) {
        switch (studentType) {
            case Student.TYPE_INTERNATIONAL: return PaymentService.RATE_INTERNATIONAL;
            case Student.TYPE_SCHOLARSHIP:  return PaymentService.RATE_SCHOLARSHIP;
            case Student.TYPE_LOCAL:
            default:                        return PaymentService.RATE_LOCAL;
        }
    }

    private double getPaymentTypeFee(String paymentType) {
        switch (paymentType) {
            case PaymentService.PAYMENT_INSTALLMENT: return PaymentService.FEE_INSTALLMENT;
            case PaymentService.PAYMENT_CARD:        return PaymentService.FEE_CARD;
            case PaymentService.PAYMENT_CASH:        return PaymentService.FEE_CASH;
            default:                                 return PaymentService.FEE_DEFAULT;
        }
    }

    // -------------------------------------------------------------------------
    // Enrollment Commit & Response Helpers
    // -------------------------------------------------------------------------

    private void commitEnrollment(Student student, Course course, String semester, double fee) {
        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        database.getEnrollments().add(new Enrollment(student.getId(), course.getCode(), semester, course.getDay(), course.getTimeSlot()));
        course.incrementEnrollment();
        database.getLogs().add("Enrolled " + student.getId() + " into " + course.getCode());
    }

    private String buildEnrollmentResponse(Student student, Course course, String semester, double fee) {
        StringBuilder response = new StringBuilder();
        response.append("Enrollment completed.\n");
        response.append("Student: ").append(student.getName()).append("\n");
        response.append("Course: ").append(course.getTitle()).append("\n");
        response.append("Semester: ").append(semester).append("\n");
        response.append("Fee charged: $").append(fee);

        appendEmailNotification(response, student, "enrolled in " + course.getTitle(), "Enrollment email sent");

        return response.toString();
    }

    private void appendEmailNotification(StringBuilder response, Student student, String message, String successLog) {
        if (notificationService.isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": ").append(message);
            database.getLogs().add(successLog);
        } else {
            response.append("\nInvalid email. Could not send notification.");
            database.getLogs().add("Invalid email for " + student.getId());
        }
    }

    // -------------------------------------------------------------------------
    // Grade Assignment Helpers
    // -------------------------------------------------------------------------

    private Enrollment findEnrollment(String studentId, String courseCode, String semester) {
        return database.getEnrollments().stream()
                .filter(e -> e.getStudentId().equals(studentId)
                        && e.getCourseCode().equals(courseCode)
                        && e.getSemester().equals(semester))
                .findFirst()
                .orElse(null);
    }

    private void applyGradeToStudent(Student student, Course course, String grade) {
        double points = getGradePoints(grade);
        student.setTotalCompletedCredits(student.getTotalCompletedCredits() + course.getCreditHours());
        student.setTotalGradePoints(student.getTotalGradePoints() + (points * course.getCreditHours()));
    }

    private double getGradePoints(String grade) {
        switch (grade) {
            case GRADE_A: return POINTS_A;
            case GRADE_B: return POINTS_B;
            case GRADE_C: return POINTS_C;
            case GRADE_D: return POINTS_D;
            default:      return POINTS_F;
        }
    }

    private String buildGradeResponse(Student student) {
        StringBuilder response = new StringBuilder();
        response.append("Grade assigned successfully.\n");
        response.append("Updated GPA: ").append(student.getGpa()).append("\n");
        response.append("Updated Status: ").append(student.getStatus());

        appendEmailNotification(response, student, "grade posted", "Grade email sent");

        return response.toString();
    }

    // -------------------------------------------------------------------------
    // Academic Status Helper
    // -------------------------------------------------------------------------

    private void updateStudentAcademicStatus(Student student) {
        if (student.getTotalCompletedCredits() > 0) {
            student.setGpa(student.getTotalGradePoints() / student.getTotalCompletedCredits());
        }

        if (student.getGpa() < GPA_PROBATION_THRESHOLD) {
            student.setStatus(Student.STATUS_PROBATION);
        } else if (student.getGpa() < GPA_HONOR_THRESHOLD) {
            student.setStatus(Student.STATUS_GOOD);
        } else {
            student.setStatus(Student.STATUS_HONOR);
        }
    }
}