import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CourseListImpl implements CourseList {
    public final String semesterName;
    private final Map<String, Course> courses;
    private ArrayList<ArrayList<String>> schedules;
    private int numOldSchedule;
    private final Map<Integer, ArrayList<String>> times;
    private final int maxTime;
    private final Map<String, ArrayList<String>> purposes;
    private final ArrayList<String> priorities; //List of priority purposes


    public CourseListImpl(String semesterName, int times) throws IOException {
        this.semesterName = semesterName;
        this.times = new HashMap<>();
        maxTime = times - 1;
        purposes = new HashMap<>();
        priorities = new ArrayList<>();

        courses = new HashMap<>();
        Path folderPath = Path.of("semesters/" + semesterName);
        Path coursesPath = folderPath.resolve(semesterName + "courses.txt");
        if (Files.exists(coursesPath)) { //semester exists
            //Read existing courses file, add priority, load contents with addCourse()
            BufferedReader reader = new BufferedReader(new FileReader(coursesPath.toFile()));
            String line;
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                String[] elements = line.split(", ");
                if (elements[5].equals("true") && !priorities.contains(elements[1])) {
                    priorities.add(elements[1]);
                }
                addCourse(elements[0], elements[1], Integer.parseInt(elements[2]),
                        Integer.parseInt(elements[3]), Integer.parseInt(elements[4]));
            }
            reader.close();
        } else {
            Files.createDirectory(folderPath);
            Files.createFile(coursesPath);
        }

        schedules = new ArrayList<>();
        Path schedulePath = folderPath.resolve(semesterName + "schedules.txt");
        if (Files.exists(schedulePath)) {
            //TODO: access existing schedule (file) and load into schedules
            //TODO: initialize schedules w/ length of existing file; ArrayList<>(len)
        }
        numOldSchedule = schedules.size();
    }


    //TODO: Overload private addCourse w/ extra input 'new'?

    @Override
    public boolean addCourse(String courseName, String purpose, int startTime, int endTime, int credits) throws IOException {
        if (startTime < 0 || endTime > maxTime) {
            throw new IndexOutOfBoundsException();
        }
        if (courses.containsKey(courseName)) {
            return false;
        }

        boolean priority = priorities.contains(purpose);
        Course newCourse = new Course(courseName, purpose, startTime, endTime, credits, priority);
        courses.put(courseName, newCourse);

        //Add to Purpose and Timeframe hashmap
        if (purposes.containsKey(purpose)) {
            purposes.get(purpose).add(courseName);
        } else {
            ArrayList<String> course = new ArrayList<>();
            course.add(courseName);
            purposes.put(purpose, course);
        }
        for (; startTime < endTime; startTime++) {
            if (times.containsKey(startTime)) {
                times.get(startTime).add(courseName);
            } else {
                ArrayList<String> time = new ArrayList<>();
                time.add(courseName);
                times.put(startTime, time);
            }
        }

        try {
            saveCourse(newCourse);
        } catch (IOException e) {
            saveCourses();
        }

        return true;
    }

    @Override
    public void removeCourse(String courseName) throws IOException {
        if (courses.containsKey(courseName)) {
            //Remove from purposes hashmap
            String key = getCoursePurpose(courseName);
            ArrayList<String> container = purposes.get(key);
            if (container.size() == 1) { //no other courses with this purpose
                purposes.remove(key);
            } else {
                container.remove(courseName);
            }

            //Remove from times hashmap
            int endTime = getCourseEndTime(courseName);
            for (int key2=getCourseStartTime(courseName); key2<endTime; key2++) {
                ArrayList<String> container2 = times.get(key2);
                if (container2.size() == 1) {
                    times.remove(key2);
                } else {
                    container2.remove(courseName);
                }
            }

            //Remove schedules containing this course and save changes.
            if (schedules.size() > 0) {
                for (int i = schedules.size() - 1; i >= 0; i--) {
                    if (schedules.get(i).contains(courseName)) {
                        schedules.remove(i);
                    }
                }
                overwriteSchedules();
            }

            //Remove from courses file by copying to tmp; if failure, overwrite courses file.
            File courseFile = new File("semesters/" + semesterName + "/courses.txt");
            File newFile = new File("semesters/" + semesterName + "/tmp.txt");
            try {
                if (newFile.createNewFile()) {
                    BufferedReader reader = new BufferedReader(new FileReader(courseFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                    String line;
                    while ((line = reader.readLine()) != null && !line.startsWith(courseName)) {
                        writer.write(line + "\n");
                    }
                    writer.close();
                    reader.close();
                    if (!courseFile.delete() || !newFile.renameTo(courseFile)) {
                        throw new IOException();
                    }
                }
            } catch (IOException e) {
                saveCourses();
            }

            //remove from courses hashmap
            courses.remove(courseName);
        }
    }

    /* Used when times mapped Purpose : int[]
    private int[] removeFromList(int[] list, int item) {
        int[] newList = new int[list.length - 1];
        int newListInd = 0;
        for (int i : list) {
            if (i != item) {
                newList[newListInd] = i;
                newListInd++;
            }
        }
        return newList;
    }
    */

    private void swap(String[] array, int index1, int index2) {
        if (index1 != index2) {
            String holder = array[index1];
            array[index1] = array[index2];
            array[index2] = holder;
        }
    }

    private void quickSort(String[] array, int min, int max) {
        if (min < max) {
            int i = min;
            for (int a = min; a < max; a++) {
                if (array[a].compareTo(array[max]) < 0) {
                    swap(array, i, a);
                    i++;
                }
            }
            swap(array, i, max);

            quickSort(array, min, i - 1);
            quickSort(array, i + 1, max);
        }
    }

    @Override
    public String[] getCourseList() {
        String[] courseList = courses.keySet().toArray(new String[0]);
        quickSort(courseList, 0, courseList.length - 1);
        return courseList;
    }

    @Override
    public String getCoursePurpose(String courseName) {
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).purpose;
    }

    @Override
    public int getCourseStartTime(String courseName){
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).startTime;
    }

    @Override
    public int getCourseEndTime(String courseName){
        if (!courses.containsKey(courseName)) {
            throw new IllegalArgumentException();
        }
        return courses.get(courseName).endTime;
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
    public void setPriority(String purpose, boolean newPriority) {
        if (!purposes.containsKey(purpose)) {
            throw new IllegalArgumentException();
        }

        if (priorities.contains(purpose) && !newPriority) { // true -> false
            //TODO: add flag OR do a schedule creation loop w/ the old course list that excludes this course
            for (String courseName : purposes.get(purpose)) {
                courses.get(courseName).priority = false;
            }
            priorities.remove(purpose);
            schedules = new ArrayList<>();
            overwriteSchedules();
        } else if (!priorities.contains(purpose) && newPriority) { // false -> true
            for (String courseName : purposes.get(purpose)) {
                courses.get(courseName).priority = true;
            }
            priorities.add(purpose);
            schedules = new ArrayList<>();
            overwriteSchedules();
        }
    }

    @Override
    public void saveCourse(Course course) throws IOException {
        File courseFile = new File("semesters/" + semesterName + "/courses.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(courseFile, true));
        if (courses.size() != 1) {
            writer.append("\n");
        }
        writer.append(course.toString());
        writer.close();
    }

    private void saveCourses() throws IOException {
        //Overwrites courses file with data from courses hashmap; in case of IOException.
        File courseFile = new File("semesters/" + semesterName + "/courses.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(courseFile));
        if (courses.size() > 0) {
            String[] courseNames = getCourseList();
            writer.append(courses.get(courseNames[0]).toString());
            for (int i = 1; i < courseNames.length; i++) {
                writer.append("\n").append(courses.get(courseNames[i]).toString());
            }
        }
        writer.close();
    }

    @Override
    public int getExistingScheduleCount() {
        return numOldSchedule;
    }

    @Override
    public ArrayList<ArrayList<String>> getSchedules() {
        //TODO: Create schedules
        return schedules;
    }

    @Override
    public void saveNewSchedules() {
        if (getExistingScheduleCount() == 0) {
            overwriteSchedules();
        } else {
            //TODO (file)
            numOldSchedule = schedules.size();
        }
    }

    private void overwriteSchedules() {
        //TODO (file)
        numOldSchedule = schedules.size();
    }
}
