package sg.edu.np.atk_teacher.BaseClasses;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Lord One on 7/19/2016.
 */
public class GV {
    public static final int attend_code = 0,
                            late_code = 1,
                            absent_code = 2,
                            notyet_code = 3;
    public static final String [] status_name = {"Present", "Late", "Absent"};


    public static void setAuCodeInSP(Activity activity, String authorizationCode) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authorizationCode", "Bearer " + authorizationCode);
        editor.apply();
    }

}
