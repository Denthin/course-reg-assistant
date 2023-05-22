public interface Course {
    /**
     * @return Course name and section
     */
    String getName();

    /**
     * @return Graduation requirement fulfilled by this course
     */
    String getPurpose();

    /**
     * @return When this course happens
     */
    int getTime();
}
