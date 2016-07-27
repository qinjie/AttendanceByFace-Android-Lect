package sg.edu.np.atk_teacher.Items;

/**
 * Created by Lord One on 7/20/2016.
 */
public class Item_timetable {
    private String class_id;
    private String subject_name;
    private String lesson_id;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private String date;
    private int n_students_taken;
    private int n_students;


    public Item_timetable(String class_id, String subject_name, String lesson_id, int start_hour, int start_minute,
                     int end_hour, int end_minute, String date, int n_students_taken, int n_students) {
        this.class_id = class_id;
        this.subject_name = subject_name;
        this.lesson_id = lesson_id;
        this.start_hour = start_hour;
        this.start_minute = start_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.date = date;
        this.n_students_taken = n_students_taken;
        this.n_students = n_students;
    }

    public String getClass_id() {
        return class_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public int getStart_minute() {
        return start_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public String getDate() {
        return date;
    }

    public int getN_students_taken() {
        return n_students_taken;
    }

    public int getN_students() {
        return n_students;
    }

    public String getTimeAndDate() {
        return "8:00 - 10:00, 7/14/2016"; //TODO
    }

    public int compareTo(Item_timetable o) {
        if(this.getN_students() < o.getN_students())
            return -1;
        return 1;
    }
}
