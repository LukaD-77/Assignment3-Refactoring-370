public class PaymentService {

    public static final String PAYMENT_INSTALLMENT = "INSTALLMENT";
    public static final String PAYMENT_CARD = "CARD";
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_BANK = "BANK";

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

    private UniversityDatabase database;
    private NotificationService notificationService;

    public PaymentService(UniversityDatabase database, NotificationService notificationService) {
        this.database = database;
        this.notificationService = notificationService;
    }

    private double calculateFinalPaymentAmount(double amount, String method) {
        if (method.equals(PAYMENT_CARD)) return amount - DISCOUNT_CARD;
        if (method.equals(PAYMENT_BANK)) return amount - DISCOUNT_BANK;
        if (method.equals(PAYMENT_CASH)) return amount - FEE_CASH;
        return amount - DISCOUNT_DEFAULT;
    }

    public String processPayment(String studentId, double amount, String method) {
        Student student = database.findStudent(studentId);

        if (student == null) return "Error: Student not found.";
        if (amount <= 0) return "Error: Invalid payment amount.";

        amount = calculateFinalPaymentAmount(amount, method);

        student.setOutstandingBalance(student.getOutstandingBalance() - amount);
        if (student.getOutstandingBalance() < 0) {
            student.setOutstandingBalance(0);
        }

        database.getPayments().add(new PaymentRecord(studentId, amount, method, "PAID"));

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