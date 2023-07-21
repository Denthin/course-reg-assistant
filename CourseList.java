import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface CourseList {
    /**
     * @param courseName Name of course section (i.e. ASTR 101-001)
     * @param credits Number of course credits towards graduation
     * @return False if course already exists (based on courseName); true otherwise
     */
    boolean addCourse(String courseName, String purpose, int startTime, int endTime, int credits) throws IOException;

    void removeCourse(String courseName) throws IOException;

    /**
     * @return Array of course names
     */
    String[] getCourseList();

    String getCoursePurpose(String courseName);

    int getCourseStartTime(String courseName);

    int getCourseEndTime(String courseName);

    int getCourseCredits(String courseName);

    boolean isCoursePriority(String courseName);

    void setPriority(String purpose, boolean isPriority);

    /**
     * Creates or updates physical file with course information
     */
    void saveCourse(Course course) throws IOException;

    int getExistingScheduleCount();

    /**
     * @return List of all possible schedules given current courses and restrictions
     */
    ArrayList<List<String>> getSchedules(int minCourses, int maxCourses);

    /**
     * @param newSchedules Filtered list from getSchedules
     * @return False if newSchedules is null; true otherwise
     */
    boolean addSchedules(ArrayList<List<String>> newSchedules);
}
