package sg.edu.np.atk_teacher.BaseClasses;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONObject;

import sg.edu.np.atk_teacher.AuxiliaryClasses.ManageWifi;

/**
 * Created by Champ on 02/08/2016.
 */
public class ErrorClass {
    public static int unauthorized_err = 0,
                      internet_err = 1,
                      incorrectInfo_err = 2,
                      wrongFormat_err = 3,
                      accInvalid_err = 4,
                      newPassInvalid_err = 5,
                      incorrectPass_err = 6,
                      unsuccessAttemp_err = 7,
                      noEmail_err = 8;
    public static void showError(Activity activity, int code) {
        String errorDescription = "";
        if(code == unauthorized_err)
            errorDescription = "User unauthorized";
        if(code == internet_err)
            errorDescription = "No internet access";
        if(code == incorrectInfo_err)
            errorDescription = "Incorrect information";
        if(code == wrongFormat_err)
            errorDescription = "Data received in wrong format! Please contact the developers";
        if(code == accInvalid_err)
            errorDescription = "Account is invalid";
        if(code == newPassInvalid_err)
            errorDescription = "Your new password is invalid";
        if(code == incorrectPass_err)
            errorDescription = "Password incorrect";
        if(code == unsuccessAttemp_err)
            errorDescription = "Unsuccessful attempt";
        if(code == noEmail_err)
            errorDescription = "No user with this email address";

        Toast.makeText(activity, errorDescription, Toast.LENGTH_LONG).show();

        if(code == internet_err)
            ManageWifi.askToTurnOnWifi(activity);

    }
}
