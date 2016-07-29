package sg.edu.np.atk_teacher.UtilityClasses;

/**
 * Created by Champ on 29/07/2016.
 */
public class ModifyStatusClass {
    String student_id;
    String lesson_id;
    String recorded_date;
    int status;
    String recorded_time;

    public ModifyStatusClass(String student_id, String lesson_id, String recorded_date, int status, String recorded_time) {
        this.student_id = student_id;
        this.lesson_id = lesson_id;
        this.recorded_date = recorded_date;
        this.status = status;
        this.recorded_time = recorded_time;
    }
}
