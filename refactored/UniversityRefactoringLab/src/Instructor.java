public class Instructor {
    private String id;
    private String name;
    private String department;
    private int maxLoad;
    private int currentLoad;

    public Instructor(String id, String name, String department, int maxLoad) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.maxLoad = maxLoad;
        this.currentLoad = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getMaxLoad() { return maxLoad; }
    public int getCurrentLoad() { return currentLoad; }

    // Setters
    public void setCurrentLoad(int currentLoad) { this.currentLoad = currentLoad; }
}