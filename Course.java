public class Course {
    public final String name;
    public final String purpose;
    public int startTime;
    public int endTime;
    public int credits;
    public boolean priority;

    public Course(String name, String purpose, int startTime, int endTime, int credits, boolean priority) {
        this.name = name;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
        this.credits = credits;
        this.priority = priority;
    }
}
