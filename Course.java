public class Course {
    public final String name;
    public final String purpose;
    public final int time;
    public final int credits;
    public final int priority;

    public Course(String abbr, String section, String purpose, int time, int credits, int priority) {
        this.name = String.join("-", abbr, section);
        this.purpose = purpose;
        this.time = time;
        this.credits = credits;
        this.priority = priority;
    }
}
