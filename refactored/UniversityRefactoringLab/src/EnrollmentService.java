public class EnrollmentService {

    private UniversitySystem system;
    private NotificationService notificationService;

    public EnrollmentService(UniversitySystem system, NotificationService notificationService) {
        this.system = system;
        this.notificationService = notificationService;
    }

    private boolean hasScheduleConflict(String studentId, Course course, String semester) {
        for (Enrollment currentEnrollment : system.getEnrollments()) {
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
        for (Enrollment currentEnrollment : system.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(prerequisite)) {
                String g = currentEnrollment.getGrade();
                if (g != null && (g.equals(UniversitySystem.GRADE_A) || g.equals(UniversitySystem.GRADE_B) || g.equals(UniversitySystem.GRADE_C))) {
                    return true;
                }
            }
        }
        return false;
    }

    private double calculateEnrollmentFee(Student student, Course course, String paymentType, String semester) {
        double fee = 0;
        if (student.getType().equals(UniversitySystem.TYPE_LOCAL)) fee = course.getCreditHours() * UniversitySystem.RATE_LOCAL;
        else if (student.getType().equals(UniversitySystem.TYPE_INTERNATIONAL)) fee = course.getCreditHours() * UniversitySystem.RATE_INTERNATIONAL;
        else if (student.getType().equals(UniversitySystem.TYPE_SCHOLARSHIP)) fee = course.getCreditHours() * UniversitySystem.RATE_SCHOLARSHIP;
        else fee = course.getCreditHours() * UniversitySystem.RATE_LOCAL;

        if (paymentType.equals(UniversitySystem.PAYMENT_INSTALLMENT)) fee += UniversitySystem.FEE_INSTALLMENT;
        else if (paymentType.equals(UniversitySystem.PAYMENT_CARD)) fee += UniversitySystem.FEE_CARD;
        else if (paymentType.equals(UniversitySystem.PAYMENT_CASH)) fee += UniversitySystem.FEE_CASH;
        else fee += UniversitySystem.FEE_DEFAULT;

        if (semester.equals(UniversitySystem.SEMESTER_SUMMER)) fee += UniversitySystem.FEE_SUMMER;
        if (course.getCode().startsWith("SE")) fee += UniversitySystem.FEE_SE_COURSE;

        return fee;
    }

    private double getGradePoints(String grade) {
        if (grade.equals(UniversitySystem.GRADE_A)) return UniversitySystem.POINTS_A;
        if (grade.equals(UniversitySystem.GRADE_B)) return UniversitySystem.POINTS_B;
        if (grade.equals(UniversitySystem.GRADE_C)) return UniversitySystem.POINTS_C;
        if (grade.equals(UniversitySystem.GRADE_D)) return UniversitySystem.POINTS_D;
        return UniversitySystem.POINTS_F;
    }

    private void updateStudentAcademicStatus(Student student) {
        if (student.getTotalCompletedCredits() > 0) {
            student.setGpa(student.getTotalGradePoints() / student.getTotalCompletedCredits());
        }

        if (student.getGpa() < UniversitySystem.GPA_PROBATION_THRESHOLD) {
            student.setStatus(UniversitySystem.STATUS_PROBATION);
        } else if (student.getGpa() >= UniversitySystem.GPA_PROBATION_THRESHOLD && student.getGpa() < UniversitySystem.GPA_HONOR_THRESHOLD) {
            student.setStatus(UniversitySystem.STATUS_GOOD);
        } else {
            student.setStatus(UniversitySystem.STATUS_HONOR);
        }
    }

    public String enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = system.findStudent(studentId);
        Course course = system.findCourse(courseCode);

        if (student == null) {
            system.getLogs().add("Student not found: " + studentId);
            return "Error: Student not found.";
        }

        if (course == null) {
            system.getLogs().add("Course not found: " + courseCode);
            return "Error: Course not found.";
        }

        if (student.isBlocked()) {
            system.getLogs().add("Blocked student tried enrollment");
            return "Error: Student is blocked.";
        }

        if (student.getStatus().equals(UniversitySystem.STATUS_PROBATION)) {
            int count = 0;
            for (Enrollment currentEnrollment : system.getEnrollments()) {
                if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getSemester().equals(semester)) {
                    count++;
                }
            }
            if (count >= UniversitySystem.PROBATION_COURSE_LIMIT) {
                system.getLogs().add("Probation limit reached");
                return "Error: Probation student cannot register for more than " + UniversitySystem.PROBATION_COURSE_LIMIT + " courses.";
            }
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            system.getLogs().add("Course full: " + courseCode);
            return "Error: Course is full.";
        }

        if (student.getOutstandingBalance() > UniversitySystem.MAX_UNPAID_BALANCE) {
            system.getLogs().add("Balance issue for " + student.getId());
            return "Error: Student has unpaid balance.";
        }

        if (hasScheduleConflict(studentId, course, semester)) {
            system.getLogs().add("Conflict for " + studentId);
            return "Error: Schedule conflict detected.";
        }

        if (!hasPassedPrerequisite(studentId, course.getPrerequisite())) {
            system.getLogs().add("Missing prerequisite for " + studentId);
            return "Error: Missing prerequisite for the course.";
        }

        double fee = calculateEnrollmentFee(student, course, paymentType, semester);

        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.getDay(), course.getTimeSlot());
        system.getEnrollments().add(newEnrollment);
        course.incrementEnrollment();

        StringBuilder response = new StringBuilder();
        response.append("Enrollment completed.\n");
        response.append("Student: ").append(student.getName()).append("\n");
        response.append("Course: ").append(course.getTitle()).append("\n");
        response.append("Semester: ").append(semester).append("\n");
        response.append("Fee charged: $").append(fee);
        system.getLogs().add("Enrolled " + studentId + " into " + courseCode);

        if (notificationService.isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": enrolled in ").append(course.getTitle());
            system.getLogs().add("Enrollment email sent");
        } else {
            response.append("\nInvalid email. Could not send notification.");
            system.getLogs().add("Invalid email for " + student.getId());
        }

        return response.toString();
    }

    public String assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : system.getEnrollments()) {
            if (currentEnrollment.getStudentId().equals(studentId) && currentEnrollment.getCourseCode().equals(courseCode) && currentEnrollment.getSemester().equals(semester)) {
                currentEnrollment.setGrade(grade);

                double points = getGradePoints(grade);
                Student student = system.findStudent(studentId);
                Course course = system.findCourse(courseCode);

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
