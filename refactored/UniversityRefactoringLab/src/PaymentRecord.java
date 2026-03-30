public class PaymentRecord {
    private String studentId;
    private double amount;
    private String method;
    private String status;

    public PaymentRecord(String studentId, double amount, String method, String status) {
        this.studentId = studentId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}