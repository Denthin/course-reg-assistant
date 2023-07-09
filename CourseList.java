import java.io.IOException;
import java.util.ArrayList;

public interface CourseList {
    /**
     * @param courseAbbr Department abbreviation and course number (i.e. ASTR 101)
     * @param credits Number of course credits towards graduation
     * @return False if course already exists (based on courseAbbr and Section); true otherwise
     */
    boolean addCourse(String courseAbbr, String courseSection, int startTime, int endTime, int credits) throws IOException;

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

    void setPriority(String purpose, boolean newPriority);

    /**
     * Creates or updates physical file with course information
     */
    void saveCourse(Course course) throws IOException;

    int getExistingScheduleCount();

    /**
     * @return List of all possible schedules given current courses and restrictions
     */
    ArrayList<ArrayList<String>> getSchedules();

    /**
     * Creates or updates physical file with generated schedule information
     */
    void saveNewSchedules();
}
