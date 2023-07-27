import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RegularBlockImpl implements Block {
    private final String name;
    private final LocalTime startTime;
    private final Duration classLength;
    private final Duration intervalLength;
    private final int numIntervals;
    private final int minInt;

    /**
     * @param classLen in minutes
     * @param breakLen in minutes
     */
    public RegularBlockImpl(String name, String startTime, String endTime, int classLen, int breakLen, int minInt) {
        this.name = name;
        this.startTime = LocalTime.parse(startTime);
        this.classLength = Duration.ofMinutes(classLen);
        this.intervalLength = Duration.ofMinutes(breakLen).plus(this.classLength);
        numIntervals = (int) Math.ceil((float) Duration.between(this.startTime, LocalTime.parse(endTime)).toSeconds() / intervalLength.toSeconds());
        if (numIntervals <= 0) {
            throw new IllegalArgumentException();
        }
        this.minInt = minInt;
    }

    /**
     * @param elements Length of 5
     */
    public RegularBlockImpl(String[] elements, int minInt) {
        this(elements[0], elements[1], elements[2], Integer.parseInt(elements[3]), Integer.parseInt(elements[4]), minInt);
    }

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
        return timeToString(startTime.plus(intervalLength.multipliedBy(numIntervals)));
    }

    @Override
    public int getMaxInt() {
        return minInt + numIntervals;
    }

    @Override
    public String[] getInitData() {
        return new String[]{name, getDayStartTime(), getDayEndTime(), String.valueOf(classLength.getSeconds()/60), String.valueOf(intervalLength.minus(classLength).getSeconds()/60)};
    }

    @Override
    public String[] getStartTimes() {
        String[] startTimes = new String[numIntervals];
        LocalTime time = startTime;
        for (int i=0; i < numIntervals; i++) {
            startTimes[i] = timeToString(time);
            time = time.plus(intervalLength);
        }
        return startTimes;
    }

    @Override
    public String[] getEndTimes() {
        String[] endTimes = new String[numIntervals];
        LocalTime time = startTime.plus(classLength);
        for (int i=0; i < numIntervals; i++) {
            endTimes[i] = timeToString(time);
            time = time.plus(intervalLength);
        }
        return endTimes;
    }

    @Override
    public boolean isRegular() {
        return true;
    }

    @Override
    public float timeToFloat(String time) {
        float afterStart = (float) Duration.between(startTime, LocalTime.parse(time)).toSeconds() / intervalLength.toSeconds();
        return (afterStart >= 0 && afterStart <= numIntervals) ? minInt+afterStart : -1;
    }

    @Override
    public String floatToTime(float time) {
        return (time >= 0 && time <= numIntervals) ? timeToString(startTime.plus(intervalLength.multipliedBy((long) time - minInt))) : null;
    }
}
