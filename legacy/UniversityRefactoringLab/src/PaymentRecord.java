public class PaymentRecord {
    public String studentId;
    public double amount;
    public String method;
    public String status;

    public PaymentRecord(String studentId, double amount, String method, String status) {
        this.studentId = studentId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }
}
