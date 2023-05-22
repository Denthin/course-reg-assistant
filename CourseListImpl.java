import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CourseListImpl implements CourseList {
    public final String semesterName;
    private final Map<String, Course> courses;
    private final ArrayList<ArrayList<String>> schedules;

    public CourseListImpl(String semesterName, int times, boolean exists) {
        this.semesterName = semesterName;
        //TODO: create Timeframe hashmap
        courses = new HashMap<>();
        if (exists) {
            //TODO: Access existing course (file) and load contents into courses
        }
        schedules = new ArrayList<>();
        boolean scheduleExists = false; //TODO
        if (scheduleExists) {
            //TODO: access existing schedule (file) and load into schedules
        }
    }

    @Override
    public boolean addCourse(String courseAbbr, String courseSection, int time, int credits, boolean priority) {
        String courseName = String.join(courseAbbr, courseSection);
        if (courses.containsKey(courseName)) {
            return false;
        }
        //TODO: use purpose (file) and courseAbbr to identify this course's purpose
        String purpose = "placeholder";
        courses.put(courseName, new Course(courseName, purpose, time, credits, priority));
        return true;
    }

    @Override
    public void removeCourse(String courseName) {
        courses.remove(courseName);
    }

    @Override
    public String[] getCourseList() {
        String[] courseList = courses.keySet().toArray(new String[0]);
        //TODO: sort courseList alphabetically
        return courseList;
    }

    @Override
    public int getCourseTime(String courseName){
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).time;
    }

    @Override
    public int getCourseCredits(String courseName) {
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).credits;
    }

    @Override
    public boolean isCoursePriority(String courseName) {
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).priority;
    }

    @Override
    public void saveCourses() {
        //TODO (file)
    }

    @Override
    public ArrayList<ArrayList<String>> getSchedules() {
        //TODO
        return schedules;
    }

    @Override
    public void saveSchedules() {
        //TODO (file)
    }
}
