package sg.edu.np.atk_teacher.AuxiliaryClasses;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Champ on 01/08/2016.
 */
public class ProgressDia {
    static Activity activity;
    static ProgressDialog dialog = null;

    public static void showDialog(Activity _activity) {

        dismissDialog();

        activity = _activity;
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("processing...");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public static void dismissDialog() {
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}
