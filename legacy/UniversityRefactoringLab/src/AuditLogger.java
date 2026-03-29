import java.util.ArrayList;
import java.util.List;

public class AuditLogger {
    public List<String> entries = new ArrayList<>();

    public void log(String text) {
        entries.add(text);
        System.out.println("LOG: " + text);
    }

    public void printAll() {
        System.out.println("---- LOGS ----");
        for (String s : entries) {
            System.out.println(s);
        }
    }
}
