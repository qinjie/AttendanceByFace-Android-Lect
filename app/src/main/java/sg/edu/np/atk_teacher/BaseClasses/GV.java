package sg.edu.np.atk_teacher.BaseClasses;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Lord One on 7/19/2016.
 */
public class GV {
    public static final int
            notyet_code = 0,
            attend_code = 1,
            late_code = 2,
            absent_code = 3; //need to be consistent with the local server

    public static final int max_late_time = 15; //need to be consistent with the local server

    public static final String [] status_name = {"Present", "Late", "Absent"};

    public static Activity activity;

    public static String auCode = null;

    public static String lecturerName = "Mr (Ms) Lecturer";

    public static final String MONTH_NAME[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static final String DAY_OF_WEEK[] = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

}
