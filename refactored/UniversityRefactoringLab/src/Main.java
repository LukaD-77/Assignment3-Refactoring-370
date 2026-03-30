import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Final Dependency Injection Setup
        UniversityDatabase database = new UniversityDatabase();
        NotificationService notificationService = new NotificationService(database);
        PaymentService paymentService = new PaymentService(database, notificationService);
        EnrollmentService enrollmentService = new EnrollmentService(database, notificationService);
        ReportPrinter reportPrinter = new ReportPrinter();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice(scanner);

            switch (choice) {
                case 1: addStudent(scanner, database); break;
                case 2: addCourse(scanner, database); break;
                case 3: addInstructor(scanner, database); break;
                case 4: enrollStudent(scanner, enrollmentService); break;
                case 5: assignGrade(scanner, enrollmentService); break;
                case 6: processPayment(scanner, paymentService); break;
                case 7: viewTranscript(scanner, database, reportPrinter); break;
                case 8: viewCourseRoster(scanner, database, reportPrinter); break;
                case 9: viewDepartmentSummary(scanner, database, reportPrinter); break;
                case 10:
                    String result = notificationService.sendWarningLetters();
                    System.out.println(result);
                    break;
                case 11: reportPrinter.printStudents(database.getStudents()); break;
                case 12: reportPrinter.printCourses(database.getCourses()); break;
                case 13: reportPrinter.printPayments(database.getPayments()); break;
                case 14:
                    System.out.println("Exiting system...");
                    running = false;
                    break;
                default:
                    if (choice != -1) System.out.println("Invalid option.");
                    break;
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== UNIVERSITY SYSTEM =====");
        System.out.println("1. Add Student");
        System.out.println("2. Add Course");
        System.out.println("3. Add Instructor");
        System.out.println("4. Enroll Student");
        System.out.println("5. Assign Grade");
        System.out.println("6. Process Payment");
        System.out.println("7. View Transcript");
        System.out.println("8. View Course Roster");
        System.out.println("9. View Department Summary");
        System.out.println("10. Send Warning Letters");
        System.out.println("11. View All Students");
        System.out.println("12. View All Courses");
        System.out.println("13. View All Payments");
        System.out.println("14. Exit");
        System.out.print("Choose option: ");
    }

    private static int getUserChoice(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception ex) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private static void addStudent(Scanner scanner, UniversityDatabase database) {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter Type (LOCAL/INTERNATIONAL/SCHOLARSHIP): ");
        String type = scanner.nextLine();

        database.addStudent(new Student(id, name, email, department, type));
        System.out.println("Student added.");
    }

    private static void addCourse(Scanner scanner, UniversityDatabase database) {
        System.out.print("Enter Course Code: ");
        String code = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Instructor Name: ");
        String instructor = scanner.nextLine();
        System.out.print("Enter Credit Hours: ");
        int credits = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Prerequisite (or leave empty): ");
        String pre = scanner.nextLine();
        System.out.print("Enter Day: ");
        String day = scanner.nextLine();
        System.out.print("Enter Time Slot: ");
        String time = scanner.nextLine();

        database.addCourse(new Course(code, title, instructor, credits, capacity, pre, day, time));
        System.out.println("Course added.");
    }

    private static void addInstructor(Scanner scanner, UniversityDatabase database) {
        System.out.print("Enter Instructor ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Instructor Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter Maximum Teaching Load: ");
        int maxLoad = Integer.parseInt(scanner.nextLine());

        database.addInstructor(new Instructor(id, name, department, maxLoad));
        System.out.println("Instructor added.");
    }

    private static void enrollStudent(Scanner scanner, EnrollmentService enrollmentService) {
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();
        System.out.print("Enter Semester: ");
        String semester = scanner.nextLine();
        System.out.print("Enter Payment Type (CARD/CASH/BANK/INSTALLMENT): ");
        String pay = scanner.nextLine();

        String result = enrollmentService.enrollStudent(studentId, courseCode, semester, pay);
        System.out.println(result);
    }

    private static void assignGrade(Scanner scanner, EnrollmentService enrollmentService) {
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();
        System.out.print("Enter Semester: ");
        String semester = scanner.nextLine();
        System.out.print("Enter Grade (A/B/C/D/F): ");
        String grade = scanner.nextLine();

        String result = enrollmentService.assignGrade(studentId, courseCode, semester, grade);
        System.out.println(result);
    }

    private static void processPayment(Scanner scanner, PaymentService paymentService) {
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter Method (CARD/BANK/CASH): ");
        String paymentMethod = scanner.nextLine();

        String result = paymentService.processPayment(studentId, amount, paymentMethod);
        System.out.println(result);
    }

    private static void viewTranscript(Scanner scanner, UniversityDatabase database, ReportPrinter reportPrinter) {
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        reportPrinter.printTranscript(database, studentId);
    }

    private static void viewCourseRoster(Scanner scanner, UniversityDatabase database, ReportPrinter reportPrinter) {
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();
        reportPrinter.printCourseRoster(database, courseCode);
    }

    private static void viewDepartmentSummary(Scanner scanner, UniversityDatabase database, ReportPrinter reportPrinter) {
        System.out.print("Enter Department Code (e.g., CS, SE, IT): ");
        String department = scanner.nextLine();
        reportPrinter.printDepartmentSummary(database, department);
    }
}