import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class CourseListImpl implements CourseList {
    public final String semesterName;
    private final Map<String, Course> courses;
    private ArrayList<List<String>> schedules;
    private int numOldSchedule;
    private final int maxTime;
    private final Map<String, ArrayList<String>> priorities;


    public CourseListImpl(String semesterName, int times) throws IOException {
        this.semesterName = semesterName;
        maxTime = times - 1;
        priorities = new HashMap<>();

        courses = new HashMap<>();
        Path folderPath = Path.of("semesters/" + semesterName);
        Path coursesPath = folderPath.resolve(semesterName + "courses.txt");
        if (Files.exists(coursesPath)) { //semester exists
            //Read existing courses file, add priority, load contents with addCourse()
            BufferedReader reader = new BufferedReader(new FileReader(coursesPath.toFile()));
            String line;
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                String[] elements = line.split(", ");
                if (elements[5].equals("true") && !priorities.containsKey(elements[1])) {
                    priorities.put(elements[1], new ArrayList<>(Collections.singletonList(elements[0])));
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

        boolean priority = priorities.containsKey(purpose);
        Course newCourse = new Course(courseName, purpose, startTime, endTime, credits, priority);
        courses.put(courseName, newCourse);

        //Add to Priority hashmap
        if (priority) priorities.get(purpose).add(courseName);

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
            //Remove from priorities hashmap
            String key = getCoursePurpose(courseName);
            ArrayList<String> container = priorities.get(key);
            boolean wasPriority = container != null;
            if (wasPriority) container.remove(courseName);

            //Remove schedules containing this course and save changes.
            if (wasPriority && container.size() == 0) {
                schedules.clear();
                numOldSchedule = 0;
                saveNewSchedules();
            }
            else {
                if (schedules.size() > 0) {
                    int oldSize = schedules.size();
                    for (int i = schedules.size() - 1; i >= 0; i--) {
                        if (schedules.get(i).contains(courseName)) {
                            schedules.remove(i);
                        }
                    }
                    if (oldSize != schedules.size()) {
                        numOldSchedule = 0;
                        saveNewSchedules();
                    }
                }
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
        if (priorities.containsKey(purpose) && !newPriority) { // true -> false
            //TODO: add flag OR do a schedule creation loop w/ the old course list that excludes this course
            for (String courseName : priorities.get(purpose)) {
                courses.get(courseName).priority = false;
            }
            priorities.remove(purpose);
            schedules = new ArrayList<>();
            saveNewSchedules();
        } else if (!priorities.containsKey(purpose) && newPriority) { // false -> true
            //Add priority courses to new list
            priorities.put(purpose, new ArrayList<>());
            for (Course course : courses.values()) {
                if (course.purpose.equals(purpose)) {
                    course.priority = true;
                    priorities.get(purpose).add(course.name);
                }
            }
            schedules = new ArrayList<>();
            saveNewSchedules();
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

    private void undoLastSelection(int[] lens, int i, Set<String> remain, Stack<String> removed, LinkedList<String> schedule) {
        for (; lens[i] > 0; lens[i]--) {
            remain.add(removed.pop());
        }
        schedule.removeLast();
    }

    private void moveCourse(int[] lens, int i, Set<String> remain, Stack<String> removed, String course) {
        remain.remove(course);
        removed.add(course);
        lens[i]++;
    }

    @Override
    public ArrayList<List<String>> getSchedules(int minCourses, int maxCourses) {
        //TODO: In controller implementation, ensure minCourses >= priorities.size()
        Set<String> remainingCourses = courses.keySet();
        Stack<String> removedCourses = new Stack<>();
        int[] lengths = new int[maxCourses + 1];

        LinkedList<String> schedule = new LinkedList<>();
        String[] priorPurposes = priorities.keySet().toArray(new String[0]);
        while (courses.size() - lengths[0] >= minCourses) {
            //Add courses for all priorities
            int i=schedule.size();
            for (; i<priorities.size(); i++) {
                Course newPCourse = null;
                //Select priority course
                for (String course : priorities.get(priorPurposes[i])) {
                    if (remainingCourses.contains(course)) {
                        newPCourse = courses.get(course);
                        break;
                    }
                }
                if (newPCourse == null && i == 0) break;
                else if (newPCourse == null) {
                    //Undo last selection
                    undoLastSelection(lengths, i, remainingCourses, removedCourses, schedule);
                    i -= 2;
                } else {
                    //Add to schedule
                    schedule.add(newPCourse.name);
                    moveCourse(lengths, i, remainingCourses, removedCourses, newPCourse.name);
                    //Remove conflicts
                    for (String otherCourse : remainingCourses) {
                        if (courses.get(otherCourse).conflicts(newPCourse)) {
                            moveCourse(lengths, i+1, remainingCourses, removedCourses, otherCourse);
                        }
                    }
                }
            }
            if (schedule.isEmpty()) break;

            //Produce all possible schedules with this round's priority courses
            boolean back = false;
            int maxLength = lengths[i] + remainingCourses.size();
            while (maxLength - lengths[i] >= minCourses - i) {
                //Fill up a schedule
                while (schedule.size() < maxCourses && remainingCourses.size() > 0) {
                    //Get course
                    Course newCourse = courses.get(remainingCourses.iterator().next());
                    //Add to schedule
                    moveCourse(lengths, schedule.size(), remainingCourses, removedCourses, newCourse.name);
                    schedule.add(newCourse.name);
                    //Remove conflicts if further courses can be added
                    if (schedule.size() != maxCourses) {
                        for (String otherCourse : remainingCourses) {
                            if (courses.get(otherCourse).conflicts(newCourse)) {
                                moveCourse(lengths, schedule.size(), remainingCourses, removedCourses, otherCourse);
                            }
                        }
                        back = false;
                    }
                }
                //Add schedule if it meets length requirements and isn't a subset of a produced schedule
                if (schedule.size() >= minCourses && !back) schedules.add(schedule.stream().toList());
                //Undo last selection
                undoLastSelection(lengths, schedule.size(), remainingCourses, removedCourses, schedule);
                back = true;
            }

            //Undo non-priority selections and one priority selection, if possible
            while (schedule.size() >= priorities.size() && schedule.size() > 0) {
                undoLastSelection(lengths, schedule.size(), remainingCourses, removedCourses, schedule);
            }
        }
        return schedules;
    }

    @Override
    public void saveNewSchedules() {
        Path schedulePath = Path.of("semesters/" + semesterName + "/" + semesterName + "schedules.txt");
        try {
            schedulePath.toFile().createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(schedulePath.toFile(), getExistingScheduleCount() != 0));
            int i = getExistingScheduleCount();
            //TODO: alter schedules.toString() to remove brackets
            if (getExistingScheduleCount() == 0 && schedules.size() > 0) {
                writer.append(schedules.get(i).toString());
                i++;
            }
            for (; i < schedules.size(); i++) {
                writer.append("\n").append(schedules.get(i).toString());
            }
            writer.close();
            numOldSchedule = schedules.size();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
