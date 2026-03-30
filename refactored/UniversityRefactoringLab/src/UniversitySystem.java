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
        Student student = null;
        Course course = null;

        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(courseCode)) {
                course = currentCourse;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            logs.add("Student not found: " + studentId);
            return;
        }

        if (course == null) {
            System.out.println("Course not found");
            logs.add("Course not found: " + courseCode);
            return;
        }

        if (student.isBlocked) {
            System.out.println("Student is blocked");
            logs.add("Blocked student tried enrollment");
            return;
        }

        if (student.status.equals("PROBATION")) {
            int count = 0;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.semester.equals(semester)) {
                    count++;
                }
            }
            if (count >= 2) {
                System.out.println("Probation student cannot register more than 2 courses");
                logs.add("Probation limit reached");
                return;
            }
        }

        if (course.enrolled >= course.capacity) {
            System.out.println("Course is full");
            logs.add("Course full: " + courseCode);
            return;
        }

        if (student.outstandingBalance > 1000) {
            System.out.println("Student has unpaid balance");
            logs.add("Balance issue for " + student.id);
            return;
        }

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.semester.equals(semester)) {
                if (currentEnrollment.day.equals(course.day) && currentEnrollment.timeSlot.equals(course.timeSlot)) {
                    System.out.println("Schedule conflict");
                    logs.add("Conflict for " + studentId);
                    return;
                }
            }
        }

        if (course.prerequisite != null && !course.prerequisite.equals("")) {
            boolean passed = false;
            for (Enrollment currentEnrollment : enrollments) {
                if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.courseCode.equals(course.prerequisite)) {
                    if (currentEnrollment.grade != null && (currentEnrollment.grade.equals("A") || currentEnrollment.grade.equals("B") || currentEnrollment.grade.equals("C"))) {
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
        if (student.type.equals("LOCAL")) {
            fee = course.creditHours * 300;
        } else if (student.type.equals("INTERNATIONAL")) {
            fee = course.creditHours * 550;
        } else if (student.type.equals("SCHOLARSHIP")) {
            fee = course.creditHours * 100;
        } else {
            fee = course.creditHours * 300;
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

        student.outstandingBalance = student.outstandingBalance + fee;
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.day, course.timeSlot);
        enrollments.add(newEnrollment);
        course.enrolled++;

        System.out.println("Enrollment completed");
        System.out.println("Student: " + student.name);
        System.out.println("Course: " + course.title);
        System.out.println("Semester: " + semester);
        System.out.println("Fee charged: " + fee);
        logs.add("Enrolled " + studentId + " into " + courseCode);

        if (student.email != null && student.email.contains("@")) {
            System.out.println("Email sent to " + student.email + ": enrolled in " + course.title);
            logs.add("Enrollment email sent");
        } else {
            System.out.println("Invalid email");
            logs.add("Invalid email for " + student.id);
        }
    }

    public void assignGrade(String studentId, String courseCode, String semester, String grade) {
        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId) && currentEnrollment.courseCode.equals(courseCode) && currentEnrollment.semester.equals(semester)) {
                currentEnrollment.grade = grade;
                System.out.println("Grade assigned");

                double points = 0;
                if (grade.equals("A")) points = 4.0;
                else if (grade.equals("B")) points = 3.0;
                else if (grade.equals("C")) points = 2.0;
                else if (grade.equals("D")) points = 1.0;
                else if (grade.equals("F")) points = 0.0;

                Student student = null;
                Course course = null;

                for (Student currentStudent : students) {
                    if (currentStudent.id.equals(studentId)) student = currentStudent;
                }

                for (Course currentCourse : courses) {
                    if (currentCourse.code.equals(courseCode)) course = currentCourse;
                }

                if (student != null && course != null) {
                    student.totalCompletedCredits += course.creditHours;
                    student.totalGradePoints += points * course.creditHours;
                    student.gpa = student.totalGradePoints / student.totalCompletedCredits;

                    if (student.gpa < 2.0) {
                        student.status = "PROBATION";
                    } else if (student.gpa >= 2.0 && student.gpa < 3.5) {
                        student.status = "GOOD";
                    } else {
                        student.status = "HONOR";
                    }

                    System.out.println("Updated GPA: " + student.gpa);
                    System.out.println("Updated Status: " + student.status);

                    if (student.email != null && student.email.contains("@")) {
                        System.out.println("Email sent to " + student.email + ": grade posted");
                    } else {
                        System.out.println("Could not send grade email");
                    }
                }
            }
        }
    }

    public void processPayment(String studentId, double amount, String method) {
        Student student = null;
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        if (student == null) {
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
        } else {
            amount = amount - 10;
        }

        student.outstandingBalance = student.outstandingBalance - amount;
        if (student.outstandingBalance < 0) {
            student.outstandingBalance = 0;
        }

        payments.add(new PaymentRecord(studentId, amount, method, "PAID"));

        System.out.println("Payment processed for " + student.name);
        System.out.println("Method: " + method);
        System.out.println("Amount accepted: " + amount);
        System.out.println("Remaining balance: " + student.outstandingBalance);

        if (student.email != null && student.email.contains("@")) {
            System.out.println("Email sent to " + student.email + ": payment received");
        }
    }

    public void printTranscript(String studentId) {
        Student student = null;
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(studentId)) {
                student = currentStudent;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + universityName);
        System.out.println("Name: " + student.name);
        System.out.println("ID: " + student.id);
        System.out.println("Department: " + student.department);
        System.out.println("Status: " + student.status);
        System.out.println("GPA: " + student.gpa);

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.studentId.equals(studentId)) {
                String title = "";
                int credits = 0;
                for (Course currentCourse : courses) {
                    if (currentCourse.code.equals(currentEnrollment.courseCode)) {
                        title = currentCourse.title;
                        credits = currentCourse.creditHours;
                    }
                }
                System.out.println(currentEnrollment.courseCode + " - " + title + " - " + credits + " credits - Grade: " + currentEnrollment.grade);
            }
        }

        System.out.println("Outstanding Balance: " + student.outstandingBalance);
        if (student.outstandingBalance > 0) {
            System.out.println("WARNING: unpaid dues");
        }
    }

    public void printCourseRoster(String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(courseCode)) {
                System.out.println("Course: " + currentCourse.title);
                System.out.println("Instructor: " + currentCourse.instructorName);
                System.out.println("Capacity: " + currentCourse.capacity);
                System.out.println("Enrolled: " + currentCourse.enrolled);
            }
        }

        for (Enrollment currentEnrollment : enrollments) {
            if (currentEnrollment.courseCode.equals(courseCode)) {
                for (Student currentStudent : students) {
                    if (currentStudent.id.equals(currentEnrollment.studentId)) {
                        System.out.println(currentStudent.id + " - " + currentStudent.name + " - " + currentStudent.status);
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

        for (Student currentStudent : students) {
            if (currentStudent.department.equals(department)) {
                studentCount++;
                avgGpa += currentStudent.gpa;
                gpaCount++;
            }
        }

        for (Instructor currentInstructor : instructors) {
            if (currentInstructor.department.equals(department)) {
                instructorCount++;
            }
        }

        for (Course currentCourse : courses) {
            if (currentCourse.code.startsWith(department)) {
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
        for (Student currentStudent : students) {
            if (currentStudent.outstandingBalance > 500 || currentStudent.status.equals("PROBATION")) {
                if (currentStudent.email != null && currentStudent.email.contains("@")) {
                    System.out.println("Sending warning email to " + currentStudent.email);
                    if (currentStudent.outstandingBalance > 500) {
                        System.out.println("Reason: unpaid balance");
                    }
                    if (currentStudent.status.equals("PROBATION")) {
                        System.out.println("Reason: academic probation");
                    }
                    logs.add("Warning sent to " + currentStudent.id);
                } else {
                    System.out.println("Could not send warning to " + currentStudent.name);
                    logs.add("Warning failed for " + currentStudent.id);
                }
            }
        }
    }

    public Student findStudent(String id) {
        for (Student currentStudent : students) {
            if (currentStudent.id.equals(id)) {
                return currentStudent;
            }
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course currentCourse : courses) {
            if (currentCourse.code.equals(code)) {
                return currentCourse;
            }
        }
        return null;
    }
}