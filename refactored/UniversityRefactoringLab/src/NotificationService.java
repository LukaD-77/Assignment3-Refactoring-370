public class NotificationService {

    private UniversitySystem system;

    public NotificationService(UniversitySystem system) {
        this.system = system;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public String sendWarningLetters() {
        StringBuilder response = new StringBuilder();
        int sentCount = 0;
        int failedCount = 0;

        for (Student currentStudent : system.getStudents()) {
            if (currentStudent.getOutstandingBalance() > UniversitySystem.WARNING_BALANCE_THRESHOLD || currentStudent.getStatus().equals(UniversitySystem.STATUS_PROBATION)) {
                if (isValidEmail(currentStudent.getEmail())) {
                    response.append("Sending warning email to ").append(currentStudent.getEmail()).append(" - ");
                    if (currentStudent.getOutstandingBalance() > UniversitySystem.WARNING_BALANCE_THRESHOLD) {
                        response.append("Reason: Unpaid balance. ");
                    }
                    if (currentStudent.getStatus().equals(UniversitySystem.STATUS_PROBATION)) {
                        response.append("Reason: Academic probation.");
                    }
                    response.append("\n");
                    system.getLogs().add("Warning sent to " + currentStudent.getId());
                    sentCount++;
                } else {
                    response.append("Could not send warning to ").append(currentStudent.getName()).append(" (Invalid Email)\n");
                    system.getLogs().add("Warning failed for " + currentStudent.getId());
                    failedCount++;
                }
            }
        }
        response.append("Summary: ").append(sentCount).append(" letters sent, ").append(failedCount).append(" failed.");
        return response.toString();
    }
}
