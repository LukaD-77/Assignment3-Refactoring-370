import java.util.ArrayList;
import java.util.List;

public class UniversitySystem {

    public List<Student> students = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();
    public List<Enrollment> enrollments = new ArrayList<>();
    public List<Instructor> instructors = new ArrayList<>();
    public List<PaymentRecord> payments = new ArrayList<>();
    public List<String> logs = new ArrayList<>();

    public String universityName = "Metro University";
    public double localRate = 300;
    public double internationalRate = 550;
    public double scholarshipRate = 100;

    public void enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student s = null;
        Course c = null;

        for (Student st : students) {
            if (st.id.equals(studentId)) {
                s = st;
            }
        }

        for (Course co : courses) {
            if (co.code.equals(courseCode)) {
                c = co;
            }
        }

        if (s == null) {
            System.out.println("Student not found");
            logs.add("Student not found: " + studentId);
            return;
        }

        if (c == null) {
            System.out.println("Course not found");
            logs.add("Course not found: " + courseCode);
            return;
        }

        if (s.isBlocked) {
            System.out.println("Student is blocked");
            logs.add("Blocked student tried enrollment");
            return;
        }

        if (s.status.equals("PROBATION")) {
            int count = 0;
            for (Enrollment e : enrollments) {
                if (e.studentId.equals(studentId) && e.semester.equals(semester)) {
                    count++;
                }
            }
            if (count >= 2) {
                System.out.println("Probation student cannot register more than 2 courses");
                logs.add("Probation limit reached");
                return;
            }
        }

        if (c.enrolled >= c.capacity) {
            System.out.println("Course is full");
            logs.add("Course full: " + courseCode);
            return;
        }

        if (s.outstandingBalance > 1000) {
            System.out.println("Student has unpaid balance");
            logs.add("Balance issue for " + s.id);
            return;
        }

        for (Enrollment e : enrollments) {
            if (e.studentId.equals(studentId) && e.semester.equals(semester)) {
                if (e.day.equals(c.day) && e.timeSlot.equals(c.timeSlot)) {
                    System.out.println("Schedule conflict");
                    logs.add("Conflict for " + studentId);
                    return;
                }
            }
        }

        if (c.prerequisite != null && !c.prerequisite.equals("")) {
            boolean passed = false;
            for (Enrollment e : enrollments) {
                if (e.studentId.equals(studentId) && e.courseCode.equals(c.prerequisite)) {
                    if (e.grade != null && (e.grade.equals("A") || e.grade.equals("B") || e.grade.equals("C"))) {
                        passed = true;
                    }
                }
            }
            if (!passed) {
                System.out.println("Missing prerequisite");
                logs.add("Missing prerequisite for " + studentId);
                return;
            }
        }

        double fee = 0;
        if (s.type.equals("LOCAL")) {
            fee = c.creditHours * 300;
        } else if (s.type.equals("INTERNATIONAL")) {
            fee = c.creditHours * 550;
        } else if (s.type.equals("SCHOLARSHIP")) {
            fee = c.creditHours * 100;
        } else {
            fee = c.creditHours * 300;
        }

        if (paymentType.equals("INSTALLMENT")) {
            fee = fee + 50;
        } else if (paymentType.equals("CARD")) {
            fee = fee + 10;
        } else if (paymentType.equals("CASH")) {
            fee = fee + 0;
        } else {
            fee = fee + 100;
        }

        if (semester.equals("SUMMER")) {
            fee = fee + 200;
        }

        if (courseCode.startsWith("SE")) {
            fee = fee + 75;
        }

        s.outstandingBalance = s.outstandingBalance + fee;
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, c.day, c.timeSlot);
        enrollments.add(newEnrollment);
        c.enrolled++;

        System.out.println("Enrollment completed");
        System.out.println("Student: " + s.name);
        System.out.println("Course: " + c.title);
        System.out.println("Semester: " + semester);
        System.out.println("Fee charged: " + fee);
        logs.add("Enrolled " + studentId + " into " + courseCode);

        if (s.email != null && s.email.contains("@")) {
            System.out.println("Email sent to " + s.email + ": enrolled in " + c.title);
            logs.add("Enrollment email sent");
        } else {
            System.out.println("Invalid email");
            logs.add("Invalid email for " + s.id);
        }
    }

    public void assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment e : enrollments) {
            if (e.studentId.equals(studentId) && e.courseCode.equals(courseCode) && e.semester.equals(semester)) {
                e.grade = grade;
                System.out.println("Grade assigned");

                double points = 0;
                if (grade.equals("A")) points = 4.0;
                else if (grade.equals("B")) points = 3.0;
                else if (grade.equals("C")) points = 2.0;
                else if (grade.equals("D")) points = 1.0;
                else if (grade.equals("F")) points = 0.0;

                Student s = null;
                Course c = null;

                for (Student st : students) {
                    if (st.id.equals(studentId)) s = st;
                }

                for (Course co : courses) {
                    if (co.code.equals(courseCode)) c = co;
                }

                if (s != null && c != null) {
                    s.totalCompletedCredits += c.creditHours;
                    s.totalGradePoints += points * c.creditHours;
                    s.gpa = s.totalGradePoints / s.totalCompletedCredits;

                    if (s.gpa < 2.0) {
                        s.status = "PROBATION";
                    } else if (s.gpa >= 2.0 && s.gpa < 3.5) {
                        s.status = "GOOD";
                    } else {
                        s.status = "HONOR";
                    }

                    System.out.println("Updated GPA: " + s.gpa);
                    System.out.println("Updated Status: " + s.status);

                    if (s.email != null && s.email.contains("@")) {
                        System.out.println("Email sent to " + s.email + ": grade posted");
                    } else {
                        System.out.println("Could not send grade email");
                    }
                }
            }
        }
    }

    public void processPayment(String studentId, double amount, String method) {
        Student s = null;
        for (Student st : students) {
            if (st.id.equals(studentId)) {
                s = st;
            }
        }

        if (s == null) {
            System.out.println("Student not found");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid payment");
            return;
        }

        if (method.equals("CARD")) {
            amount = amount - 5;
        } else if (method.equals("BANK")) {
            amount = amount - 2;
        } else if (method.equals("CASH")) {
            amount = amount;
        } else {
            amount = amount - 10;
        }

        s.outstandingBalance = s.outstandingBalance - amount;
        if (s.outstandingBalance < 0) {
            s.outstandingBalance = 0;
        }

        payments.add(new PaymentRecord(studentId, amount, method, "PAID"));

        System.out.println("Payment processed for " + s.name);
        System.out.println("Method: " + method);
        System.out.println("Amount accepted: " + amount);
        System.out.println("Remaining balance: " + s.outstandingBalance);

        if (s.email != null && s.email.contains("@")) {
            System.out.println("Email sent to " + s.email + ": payment received");
        }
    }

    public void printTranscript(String studentId) {
        Student s = null;
        for (Student st : students) {
            if (st.id.equals(studentId)) {
                s = st;
            }
        }

        if (s == null) {
            System.out.println("Student not found");
            return;
        }

        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + universityName);
        System.out.println("Name: " + s.name);
        System.out.println("ID: " + s.id);
        System.out.println("Department: " + s.department);
        System.out.println("Status: " + s.status);
        System.out.println("GPA: " + s.gpa);

        for (Enrollment e : enrollments) {
            if (e.studentId.equals(studentId)) {
                String title = "";
                int credits = 0;
                for (Course c : courses) {
                    if (c.code.equals(e.courseCode)) {
                        title = c.title;
                        credits = c.creditHours;
                    }
                }
                System.out.println(e.courseCode + " - " + title + " - " + credits + " credits - Grade: " + e.grade);
            }
        }

        System.out.println("Outstanding Balance: " + s.outstandingBalance);
        if (s.outstandingBalance > 0) {
            System.out.println("WARNING: unpaid dues");
        }
    }

    public void printCourseRoster(String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        for (Course c : courses) {
            if (c.code.equals(courseCode)) {
                System.out.println("Course: " + c.title);
                System.out.println("Instructor: " + c.instructorName);
                System.out.println("Capacity: " + c.capacity);
                System.out.println("Enrolled: " + c.enrolled);
            }
        }

        for (Enrollment e : enrollments) {
            if (e.courseCode.equals(courseCode)) {
                for (Student s : students) {
                    if (s.id.equals(e.studentId)) {
                        System.out.println(s.id + " - " + s.name + " - " + s.status);
                    }
                }
            }
        }
    }

    public void printDepartmentSummary(String department) {
        System.out.println("----- DEPARTMENT SUMMARY -----");
        System.out.println("Department: " + department);

        int studentCount = 0;
        int instructorCount = 0;
        int courseCount = 0;
        double avgGpa = 0;
        int gpaCount = 0;

        for (Student s : students) {
            if (s.department.equals(department)) {
                studentCount++;
                avgGpa += s.gpa;
                gpaCount++;
            }
        }

        for (Instructor i : instructors) {
            if (i.department.equals(department)) {
                instructorCount++;
            }
        }

        for (Course c : courses) {
            if (c.code.startsWith(department)) {
                courseCount++;
            }
        }

        if (gpaCount > 0) {
            avgGpa = avgGpa / gpaCount;
        }

        System.out.println("Students: " + studentCount);
        System.out.println("Instructors: " + instructorCount);
        System.out.println("Courses: " + courseCount);
        System.out.println("Average GPA: " + avgGpa);
    }

    public void sendWarningLetters() {
        for (Student s : students) {
            if (s.outstandingBalance > 500 || s.status.equals("PROBATION")) {
                if (s.email != null && s.email.contains("@")) {
                    System.out.println("Sending warning email to " + s.email);
                    if (s.outstandingBalance > 500) {
                        System.out.println("Reason: unpaid balance");
                    }
                    if (s.status.equals("PROBATION")) {
                        System.out.println("Reason: academic probation");
                    }
                    logs.add("Warning sent to " + s.id);
                } else {
                    System.out.println("Could not send warning to " + s.name);
                    logs.add("Warning failed for " + s.id);
                }
            }
        }
    }

    public Student findStudent(String id) {
        for (Student s : students) {
            if (s.id.equals(id)) {
                return s;
            }
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course c : courses) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
