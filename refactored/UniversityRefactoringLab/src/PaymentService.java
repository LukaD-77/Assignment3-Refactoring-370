public class PaymentService {

    private UniversitySystem system;
    private NotificationService notificationService;

    public PaymentService(UniversitySystem system, NotificationService notificationService) {
        this.system = system;
        this.notificationService = notificationService;
    }

    private double calculateFinalPaymentAmount(double amount, String method) {
        if (method.equals(UniversitySystem.PAYMENT_CARD)) return amount - UniversitySystem.DISCOUNT_CARD;
        if (method.equals(UniversitySystem.PAYMENT_BANK)) return amount - UniversitySystem.DISCOUNT_BANK;
        if (method.equals(UniversitySystem.PAYMENT_CASH)) return amount - UniversitySystem.FEE_CASH;
        return amount - UniversitySystem.DISCOUNT_DEFAULT;
    }

    public String processPayment(String studentId, double amount, String method) {
        Student student = system.findStudent(studentId);

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

        system.getPayments().add(new PaymentRecord(studentId, amount, method, "PAID"));

        StringBuilder response = new StringBuilder();
        response.append("Payment processed for ").append(student.getName()).append("\n");
        response.append("Method: ").append(method).append("\n");
        response.append("Amount accepted: $").append(amount).append("\n");
        response.append("Remaining balance: $").append(student.getOutstandingBalance());

        if (notificationService.isValidEmail(student.getEmail())) {
            response.append("\nEmail sent to ").append(student.getEmail()).append(": payment received");
        }
        return response.toString();
    }
}