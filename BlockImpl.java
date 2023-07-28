import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BlockImpl implements Block {
    private final String name;
    private final LocalTime startTime;
    private final int numIntervals;
    private final int minInt;

    public BlockImpl(String name, String startTime, String endTime, int minInt) {
        this.name = name;
        this.startTime = LocalTime.parse(startTime);
        numIntervals = (int) Math.ceil(ChronoUnit.HOURS.between(this.startTime, LocalTime.parse(endTime)));
        if (numIntervals <= 0) {
            throw new IllegalArgumentException();
        }
        this.minInt = minInt;
    }

    /**
     * @param elements contains name, startTime, and endTime
     */
    public BlockImpl(String[] elements, int minInt) {
        this(elements[0], elements[1], elements[2], minInt);
    }

    /**
     * Formats time as a string and removes :SS from the end.
     * @param time the time to convert into a string
     * @return the time in HH:MM format
     */
    private String timeToString(LocalTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0, 5);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDayStartTime() {
        return timeToString(startTime);
    }

    @Override
    public String getDayEndTime() {
        return timeToString(startTime.plus(Duration.of(numIntervals, ChronoUnit.HOURS)));
    }

    @Override
    public int getMaxInt() {
        return minInt + numIntervals;
    }

    public String[] getInitData() {
        return new String[]{name, getDayStartTime(), getDayEndTime()};
    }

    @Override
    public String[] getStartTimes() {
        return new String[0];
    }

    @Override
    public String[] getEndTimes() {
        return new String[0];
    }

    @Override
    public boolean isRegular() {
        return false;
    }

    @Override
    public float timeToFloat(String time) {
        float afterStart = (float) (Duration.between(startTime, LocalTime.parse(time)).getSeconds() / 360.0);
        return (afterStart >= 0 && afterStart <= numIntervals) ? minInt+afterStart : -1;
    }

    @Override
    public String floatToTime(float time) {
        return (time >= 0 && time <= numIntervals) ? timeToString(startTime.plus(Duration.of((long) time - minInt, ChronoUnit.HOURS))) : null;
    }
}
