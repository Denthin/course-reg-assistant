public class Course {
    public final String name;
    public final String purpose;
    public final int time;
    public final int credits;

    public Course(String abbr, String section, String purpose, int time, int credits) {
        this.name = String.join("-", abbr, section);
        this.purpose = purpose;
        this.time = time;
        this.credits = credits;
    }
}
