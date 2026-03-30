public class NotificationService {

    public static final double WARNING_BALANCE_THRESHOLD = 500.0;

    private UniversityDatabase database;

    public NotificationService(UniversityDatabase database) {
        this.database = database;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public String sendWarningLetters() {
        StringBuilder response = new StringBuilder();
        int sentCount = 0;
        int failedCount = 0;

        for (Student currentStudent : database.getStudents()) {
            if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD || currentStudent.getStatus().equals(Student.STATUS_PROBATION)) {
                if (isValidEmail(currentStudent.getEmail())) {
                    response.append("Sending warning email to ").append(currentStudent.getEmail()).append(" - ");
                    if (currentStudent.getOutstandingBalance() > WARNING_BALANCE_THRESHOLD) {
                        response.append("Reason: Unpaid balance. ");
                    }
                    if (currentStudent.getStatus().equals(Student.STATUS_PROBATION)) {
                        response.append("Reason: Academic probation.");
                    }
                    response.append("\n");
                    database.getLogs().add("Warning sent to " + currentStudent.getId());
                    sentCount++;
                } else {
                    response.append("Could not send warning to ").append(currentStudent.getName()).append(" (Invalid Email)\n");
                    database.getLogs().add("Warning failed for " + currentStudent.getId());
                    failedCount++;
                }
            }
        }
        response.append("Summary: ").append(sentCount).append(" letters sent, ").append(failedCount).append(" failed.");
        return response.toString();
    }
}