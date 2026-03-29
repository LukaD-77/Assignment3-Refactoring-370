import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UniversitySystem system = new UniversitySystem();

        while (true) {
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

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception ex) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (choice == 1) {
                System.out.print("Enter Student ID: ");
                String id = scanner.nextLine();

                System.out.print("Enter Name: ");
                String name = scanner.nextLine();

                System.out.print("Enter Email: ");
                String email = scanner.nextLine();

                System.out.print("Enter Department: ");
                String dept = scanner.nextLine();

                System.out.print("Enter Type (LOCAL/INTERNATIONAL/SCHOLARSHIP): ");
                String type = scanner.nextLine();

                system.students.add(new Student(id, name, email, dept, type));
                System.out.println("Student added.");

            } else if (choice == 2) {
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

                system.courses.add(new Course(code, title, instructor, credits, capacity, pre, day, time));
                System.out.println("Course added.");

            } else if (choice == 3) {
                System.out.print("Enter Instructor ID: ");
                String id = scanner.nextLine();

                System.out.print("Enter Instructor Name: ");
                String name = scanner.nextLine();

                System.out.print("Enter Department: ");
                String dept = scanner.nextLine();

                System.out.print("Enter Maximum Teaching Load: ");
                int maxLoad = Integer.parseInt(scanner.nextLine());

                system.instructors.add(new Instructor(id, name, dept, maxLoad));
                System.out.println("Instructor added.");

            } else if (choice == 4) {
                System.out.print("Enter Student ID: ");
                String sid = scanner.nextLine();

                System.out.print("Enter Course Code: ");
                String ccode = scanner.nextLine();

                System.out.print("Enter Semester: ");
                String sem = scanner.nextLine();

                System.out.print("Enter Payment Type (CARD/CASH/BANK/INSTALLMENT): ");
                String pay = scanner.nextLine();

                system.enrollStudent(sid, ccode, sem, pay);

            } else if (choice == 5) {
                System.out.print("Enter Student ID: ");
                String sid = scanner.nextLine();

                System.out.print("Enter Course Code: ");
                String ccode = scanner.nextLine();

                System.out.print("Enter Semester: ");
                String sem = scanner.nextLine();

                System.out.print("Enter Grade (A/B/C/D/F): ");
                String grade = scanner.nextLine();

                system.assignGrade(sid, ccode, sem, grade);

            } else if (choice == 6) {
                System.out.print("Enter Student ID: ");
                String sid = scanner.nextLine();

                System.out.print("Enter Amount: ");
                double amount = Double.parseDouble(scanner.nextLine());

                System.out.print("Enter Method (CARD/BANK/CASH): ");
                String method = scanner.nextLine();

                system.processPayment(sid, amount, method);

            } else if (choice == 7) {
                System.out.print("Enter Student ID: ");
                String sid = scanner.nextLine();
                system.printTranscript(sid);

            } else if (choice == 8) {
                System.out.print("Enter Course Code: ");
                String ccode = scanner.nextLine();
                system.printCourseRoster(ccode);

            } else if (choice == 9) {
                System.out.print("Enter Department Code (e.g., CS, SE, IT): ");
                String dept = scanner.nextLine();
                system.printDepartmentSummary(dept);

            } else if (choice == 10) {
                system.sendWarningLetters();

            } else if (choice == 11) {
                LegacyReportPrinter printer = new LegacyReportPrinter();
                printer.printStudents(system.students);

            } else if (choice == 12) {
                LegacyReportPrinter printer = new LegacyReportPrinter();
                printer.printCourses(system.courses);

            } else if (choice == 13) {
                LegacyReportPrinter printer = new LegacyReportPrinter();
                printer.printPayments(system.payments);

            } else if (choice == 14) {
                System.out.println("Exiting system...");
                break;

            } else {
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }
}
