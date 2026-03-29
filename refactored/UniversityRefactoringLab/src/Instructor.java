public class Instructor {
    public String id;
    public String name;
    public String department;
    public int maxLoad;
    public int currentLoad;

    public Instructor(String id, String name, String department, int maxLoad) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.maxLoad = maxLoad;
        this.currentLoad = 0;
    }
}
