public interface Block {
    /**
     * @return the name of the block, i.e. MWF
     */
    String getName();

    /**
     * @return the earliest time a class can start in this block, in military time
     */
    String getDayStartTime();

    /**
     * @return the latest time a class can end in this block, in military time
     */
    String getDayEndTime();

    /**
     * This function includes offsets from previous blocks in the encapsulating timeframe.
     * @return the largest integer in range for this block
     */
    int getMaxInt();

    /**
     * For saving initializing data to a file
     * @return list of variables
     */
    String[] getInitData();

    /**
     * @return List of class start times if regular; empty list otherwise
     */
    String[] getStartTimes();

    /**
     * @return List of class end times if regular; empty list otherwise
     */
    String[] getEndTimes();

    /**
     * @return true if the block is regular; false otherwise
     */
    boolean isRegular();

    /**
     * @param time In military time, i.e. 17:35 instead of 5:35 PM
     * @return -1.0 if time is invalid; int representation of time otherwise
     */
    float timeToFloat(String time);

    /**
     * @param timeNum in military time, i.e. 17:35 instead of 5:35 PM
     * @return null if timeNum is out of range; otherwise, the requested time
     */
    String floatToTime(float timeNum);
}
