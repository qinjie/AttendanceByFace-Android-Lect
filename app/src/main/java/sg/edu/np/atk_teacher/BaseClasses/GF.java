package sg.edu.np.atk_teacher.BaseClasses;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import okhttp3.ResponseBody;
import retrofit2.Call;
import sg.edu.np.atk_teacher.LoginActivity;

/**
 * Created by Lord One on 7/26/2016.
 */
public class GF {


    public static boolean alreadyLoggedIn (Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);
        if (auCode != null && auCode != "{\"password\":[\"Incorrect username or password.\"]}"){
            return true;
        }
        return false;
    }

    public static void logoutAction(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        StringClient client = ServiceGenerator.createService(StringClient.class);
        Call<ResponseBody> call = client.logout();

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

}
