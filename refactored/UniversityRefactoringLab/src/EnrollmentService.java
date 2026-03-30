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

    private boolean hasScheduleConflict(String studentId, Course course, String semester) {
        for (Enrollment currentEnrollment : database.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                if (currentEnrollment.getDay().equals(course.getDay()) && currentEnrollment.getTimeSlot().equals(course.getTimeSlot())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPassedPrerequisite(String studentId, String prerequisite) {
        if (prerequisite == null || prerequisite.isEmpty()) return true;

        for (Enrollment currentEnrollment : database.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(prerequisite)) {
                String g = currentEnrollment.getGrade();
                if (g != null && (g.equals(GRADE_A) || g.equals(GRADE_B) || g.equals(GRADE_C))) return true;
            }
        }
        return false;
    }

    private double calculateEnrollmentFee(Student student, Course course, String paymentType, String semester) {
        double fee = 0;
        if (student.getType().equals(Student.TYPE_LOCAL)) fee = course.getCreditHours() * PaymentService.RATE_LOCAL;
        else if (student.getType().equals(Student.TYPE_INTERNATIONAL)) fee = course.getCreditHours() * PaymentService.RATE_INTERNATIONAL;
        else if (student.getType().equals(Student.TYPE_SCHOLARSHIP)) fee = course.getCreditHours() * PaymentService.RATE_SCHOLARSHIP;
        else fee = course.getCreditHours() * PaymentService.RATE_LOCAL;

        if (paymentType.equals(PaymentService.PAYMENT_INSTALLMENT)) fee += PaymentService.FEE_INSTALLMENT;
        else if (paymentType.equals(PaymentService.PAYMENT_CARD)) fee += PaymentService.FEE_CARD;
        else if (paymentType.equals(PaymentService.PAYMENT_CASH)) fee += PaymentService.FEE_CASH;
        else fee += PaymentService.FEE_DEFAULT;

        if (semester.equals(SEMESTER_SUMMER)) fee += PaymentService.FEE_SUMMER;
        if (course.getCode().startsWith("SE")) fee += PaymentService.FEE_SE_COURSE;

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
            student.setStatus(Student.STATUS_PROBATION);
        } else if (student.getGpa() >= GPA_PROBATION_THRESHOLD && student.getGpa() < GPA_HONOR_THRESHOLD) {
            student.setStatus(Student.STATUS_GOOD);
        } else {
            student.setStatus(Student.STATUS_HONOR);
        }
    }

    public String enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = database.findStudent(studentId);
        Course course = database.findCourse(courseCode);

        if (student == null) {
            database.getLogs().add("Student not found: " + studentId);
            return "Error: Student not found.";
        }
        if (course == null) {
            database.getLogs().add("Course not found: " + courseCode);
            return "Error: Course not found.";
        }
        if (student.isBlocked()) {
            database.getLogs().add("Blocked student tried enrollment");
            return "Error: Student is blocked.";
        }

        if (student.getStatus().equals(Student.STATUS_PROBATION)) {
            int count = 0;
            for (Enrollment currentEnrollment : database.getEnrollments()) {
                if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                    count++;
                }
            }
            if (count >= PROBATION_COURSE_LIMIT) {
                database.getLogs().add("Probation limit reached");
                return "Error: Probation student cannot register for more than " + PROBATION_COURSE_LIMIT + " courses.";
            }
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            database.getLogs().add("Course full: " + courseCode);
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

        double fee = calculateEnrollmentFee(student, course, paymentType, semester);

        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.getDay(), course.getTimeSlot());
        database.getEnrollments().add(newEnrollment);
        course.incrementEnrollment();

        StringBuilder response = new StringBuilder();
        response.append("Enrollment completed.\n");
        response.append("Student: ").append(student.getName()).append("\n");
        response.append("Course: ").append(course.getTitle()).append("\n");
        response.append("Semester: ").append(semester).append("\n");
        response.append("Fee charged: $").append(fee);
        database.getLogs().add("Enrolled " + studentId + " into " + courseCode);

        if (notificationService.isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": enrolled in ").append(course.getTitle());
            database.getLogs().add("Enrollment email sent");
        } else {
            response.append("\nInvalid email. Could not send notification.");
            database.getLogs().add("Invalid email for " + student.getId());
        }

        return response.toString();
    }

    public String assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : database.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(courseCode) && currentEnrollment.getSemester().equals(semester)) {
                currentEnrollment.setGrade(grade);

                double points = getGradePoints(grade);
                Student student = database.findStudent(studentId);
                Course course = database.findCourse(courseCode);

                if (student != null && course != null) {
                    student.setTotalCompletedCredits(student.getTotalCompletedCredits() + course.getCreditHours());
                    student.setTotalGradePoints(student.getTotalGradePoints() + (points * course.getCreditHours()));

                    updateStudentAcademicStatus(student);

                    StringBuilder response = new StringBuilder();
                    response.append("Grade assigned successfully.\n");
                    response.append("Updated GPA: ").append(student.getGpa()).append("\n");
                    response.append("Updated Status: ").append(student.getStatus());

                    if (notificationService.isValidEmail(student.getEmail())) {
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
}