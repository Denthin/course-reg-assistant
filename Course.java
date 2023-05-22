public class Course {
    public final String name;
    public final String purpose;
    public final int time;
    public final int credits;
    public final boolean priority;

    public Course(String name, String purpose, int time, int credits, boolean priority) {
        this.name = name;
        this.purpose = purpose;
        this.time = time;
        this.credits = credits;
        this.priority = priority;
    }
}
