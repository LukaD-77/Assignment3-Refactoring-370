public class AdminHelper {

    public static boolean validEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@") && email.contains(".")) {
            return true;
        }
        if (email.contains("@")) {
            return true;
        }
        return false;
    }

    public static double calculateLatePenalty(double balance) {
        if (balance > 1000) {
            return 100;
        } else if (balance > 500) {
            return 50;
        } else if (balance > 200) {
            return 20;
        }
        return 0;
    }

    public static String academicRemark(double gpa) {
        if (gpa >= 3.5) {
            return "Excellent";
        } else if (gpa >= 2.0) {
            return "Satisfactory";
        } else {
            return "At Risk";
        }
    }
}
