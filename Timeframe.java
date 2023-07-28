import java.io.IOException;

/**
 * A Timeframe represents the standard layout of a semester's schedule. Essential for transferring time information
 * between CourseList (float format) and the user (String format). The addBlock function allows for flexibility,
 * depending on how many blocks a schedule contains.
 * <p>
 * Limitations: Does not account for courses that don't fit perfectly into one block, such as a Monday-only class in a
 * MWF/TTh block schedule. Blocks cannot be edited or removed; rather, a new Timeframe object must be created.
 */
public interface Timeframe {
    /**
     * @return the name of the timeframe, i.e. Summer
     */
    String getName();

    /**
     * @return an array of the timeframe's blocks' names, in the order they were added
     */
    String[] getBlockNames();

    /**
     * Suggest standard start times of regular blocks to the user
     * @param blockName the name of the block, i.e. MWF
     * @return null if block nonexistent; empty list if block is not regular; list of start times otherwise
     */
    String[] getStartTimes(String blockName);

    /**
     * Suggests standard end times of regular blocks to the user.
     * @param blockName the name of the block, i.e. MWF
     * @return null if block nonexistent; empty list if block is not regular; list of end times otherwise
     */
    String[] getEndTimes(String blockName);

    /**
     * @return the largest integer in range for this timeframe
     */
    int getMaxInt();

    /**
     * @param blockName the name of the block, i.e. MWF
     * @return true if the block is regular; false otherwise
     */
    boolean isRegular(String blockName);

    /**
     * Creates block with an assumed interval of one hour.
     * @param name the name of the block
     * @param startTime the earliest hour a class can start, in military time
     * @param endTime the latest hour a class can end, in military time
     * @return false if block with name already exists; true otherwise
     */
    boolean addBlock(String name, String startTime, String endTime) throws IOException;

    /**
     * Creates block with a standard class length and break length. May extend dayEndTime to ensure even division into
     * classLen and breakLen.
     * @param name the name of the block
     * @param dayStartTime the earliest time a class can start, in military time
     * @param dayEndTime the latest time a class can end, in military time
     * @param classLen the length of each class, in minutes
     * @param breakLen the length between two consecutive classes, in minutes
     * @return false if block with name already exists; true otherwise
     */
    boolean addBlock(String name, String dayStartTime, String dayEndTime, int classLen, int breakLen) throws IOException;

    /**
     * Accesses first block where time is valid. Should be used when only one block exists or there are no overlapping
     * times.
     * @param time In military time, i.e. 17:35 instead of 5:35 PM
     * @return -1.0 if time is invalid in every block; int representation of time otherwise
     */
    float timeToFloat(String time);

    /**
     * @param time in military time, i.e. 17:35 instead of 5:35 PM
     * @param blockName the name of the block, i.e. MWF
     * @return -1 if time or block is invalid; float representation of time otherwise
     */
    float timeToFloat(String time, String blockName);

    /**
     * @param timeNum in military time, i.e. 17:35 instead of 5:35 PM
     * @return null if timeNum is out of range; otherwise, the name of the block containing the requested time
     */
    String floatToBlock(float timeNum);

    /**
     * @param timeNum in military time, i.e. 17:35 instead of 5:35 PM
     * @return null if timeNum is out of range; otherwise, the requested time
     */
    String floatToTime(float timeNum);
}
