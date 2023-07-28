import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A CourseList assists students in planning course selections for an upcoming registration period. It stores potential
 * courses, produces a list of potential schedules, and stores the schedules chosen by users.
 * Schedule creation takes into account conflicts in time, conflicts in purpose, and priority courses that the user is
 * determined to take this semester.
 * <p>
 * Limitations: Courses must be assigned exactly one purpose and one time; see <a href="#{@link}">{@link Timeframe}</a>
 * for more information on the latter limitation. Does not consider course credits or newly added courses when creating
 * schedules. Does not properly encapsulate generated schedules or ensure input for the
 * <a href="#{@link}">{@link #addSchedules(ArrayList) addSchedules}</a> method is valid.
 */
public interface CourseList {
    /**
     * @return the name of the course list's semester
     */
    String getName();

    /**
     * Adds a course with the outlined parameters to the course list and stores it in the course file.
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @param credits the number of course credits towards graduation
     * @return false if course already exists (based on courseName); true otherwise
     * @throws IOException if the course could not be written to the course file
     */
    boolean addCourse(String courseName, String purpose, int startTime, int endTime, int credits) throws IOException;

    /**
     * Removes course from courseList and deletes all schedules containing it.
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @throws IOException if the course file or schedule file could not be amended accordingly
     */
    void removeCourse(String courseName) throws IOException;

    /**
     * @return Array of course names
     */
    String[] getCourseList();

    /**
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @return the course's purpose
     */
    String getCoursePurpose(String courseName);

    /**
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @return the course's start time (in int representation)
     */
    int getCourseStartTime(String courseName);

    /**
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @return the course's end time (in int representation)
     */
    int getCourseEndTime(String courseName);

    /**
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @return the course's number of credits towards graduation
     */
    int getCourseCredits(String courseName);

    /**
     * @param courseName the name of the course section (i.e. ASTR 101-001)
     * @return true if the course is a priority course; false otherwise
     */
    boolean isCoursePriority(String courseName);

    /**
     * Changes the priority status of the purpose to match isPriority.
     * @param isPriority the new priority status of purpose
     */
    void setPriority(String purpose, boolean isPriority);

    /**
     * @return the number of schedules currently contained in this course
     */
    int getExistingScheduleCount();

    /**
     * The generated list of schedules is not saved in CourseList; rather, the filtered schedule list should be set
     * with addSchedules().
     * @param minCourses the minimum number of courses in a schedule, inclusive
     * @param maxCourses the maximum number of courses in a schedule, inclusive
     * @return list of all possible schedules given current courses and restrictions
     */
    ArrayList<List<String>> getSchedules(int minCourses, int maxCourses);

    /**
     * Adds a now-filtered list from getSchedules() to the course list's schedule storage.
     * @param newSchedules list of schedules
     * @return false if newSchedules is null; true otherwise
     */
    boolean addSchedules(ArrayList<List<String>> newSchedules);
}
