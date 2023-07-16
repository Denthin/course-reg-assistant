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

    public String toString() {
        return name + ", " + purpose + ", " + startTime + ", " + endTime + ", " + credits + ", " + priority;
    }

    public boolean conflicts(Course otherCourse) {
        return purpose.equals(otherCourse.purpose)
                || (startTime >= otherCourse.startTime && startTime < otherCourse.endTime)
                || (endTime > otherCourse.startTime && endTime <= otherCourse.endTime);
    }
}
