package sg.edu.np.atk_teacher.BaseClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.Activities.LoginActivity;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.RequestClasses.TrainFaceClass;

/**
 * Created by Lord One on 7/26/2016.
 */
public class GF {


    public static boolean alreadyLoggedIn (Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_lec_pref", 0);
        String auCode = pref.getString("authorizationCode", null);
        if (auCode != null && auCode != "{\"password\":[\"Incorrect username or password.\"]}") {
            return true;
        }
        return false;
    }

    public static String getSavedUsername (Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_lec_pref", 0);
        String username = pref.getString("username", "");
        return username;
    }

    public static void logoutAction(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_lec_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        StringClient client = ServiceGenerator.createService(StringClient.class);
        Call<ResponseBody> call = client.logout();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public static void setAuCodeInSP(Activity activity, String authorizationCode, String username) {
        GV.auCode = "Bearer " + authorizationCode;

        SharedPreferences pref = activity.getSharedPreferences("ATK_lec_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authorizationCode", "Bearer " + authorizationCode);
        editor.putString("username", username);
        editor.apply();
    }

    public static void allowTrainFace(final Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.setTitle("Enable Face Training");
        dialog.setContentView(R.layout.train_face_dialog);

        final EditText stu_id_text = (EditText) dialog.findViewById(R.id.dia_input_sid);
        Button cancel_btn = (Button) dialog.findViewById(R.id.dia_cancel);
        Button allow_btn = (Button) dialog.findViewById(R.id.dia_allow);
        Button disallow_btn = (Button) dialog.findViewById(R.id.dia_disallow);
        final TextView noti_tv = (TextView) dialog.findViewById(R.id.dia_noti);

        noti_tv.setText("");
        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sid = stu_id_text.getText().toString();
                if(sid.length() == 0)
                    return;

//                JsonObject up = new JsonObject();
//                up.addProperty("studentIdTung", sid);
                TrainFaceClass up = new TrainFaceClass(sid);

                StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
                Call<ResponseBody> call = client.allowTrainFace(up);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                noti_tv.setTextColor(Color.GREEN);
                                noti_tv.setText("Student with ID \"" + sid + "\" is allowed to re-train face for 10 minutes");
                                stu_id_text.setText("");

                            } else if (response.code() == 400) {
                                JSONObject data = new JSONObject(response.errorBody().string());
                                int errorCode = data.getInt("code");
                                noti_tv.setTextColor(Color.RED);
                                noti_tv.setText("student ID does not exist!"); //TODO: specify error

                            } else {
                                noti_tv.setTextColor(Color.RED);
                                noti_tv.setText("Unauthorization problem!"); //TODO: specify error
                            }
                        }
                        catch (Exception e) {
                            noti_tv.setTextColor(Color.RED);
                            noti_tv.setText("Action failed! Please contact developer team");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        noti_tv.setTextColor(Color.RED);
                        noti_tv.setText("Action failed! No internet access");
                        ErrorClass.showError(activity, ErrorClass.internet_err);
                    }
                });
            }
        });

        disallow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sid = stu_id_text.getText().toString();
                if(sid.length() == 0)
                    return;

                JsonObject up = new JsonObject();
                up.addProperty("studentId", sid);

                StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
                Call<ResponseBody> call = client.disallowTrainFace(up);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                noti_tv.setTextColor(Color.GREEN);
                                noti_tv.setText("Student with ID \"" + sid + "\" is disallowed from re-training face");
                                stu_id_text.setText("");

                            } else if (response.code() == 400) {
                                JSONObject data = new JSONObject(response.errorBody().string());
                                int errorCode = data.getInt("code");
                                noti_tv.setTextColor(Color.RED);
                                noti_tv.setText("student ID does not exist!"); //TODO: specify error

                            } else {
                                noti_tv.setTextColor(Color.RED);
                                noti_tv.setText("Unauthorization problem!"); //TODO: specify error
                            }
                        }
                        catch (Exception e) {
                            noti_tv.setTextColor(Color.RED);
                            noti_tv.setText("Action failed! Please contact developer team");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        noti_tv.setTextColor(Color.RED);
                        noti_tv.setText("Action failed! No internet access");
                        ErrorClass.showError(activity, ErrorClass.internet_err);
                    }
                });
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }

    public static String beautifyDate(String _date) {
        String result = _date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int year = cal.get(Calendar.YEAR);
            String month = GV.MONTH_NAME[cal.get(Calendar.MONTH)];
            int day = cal.get(Calendar.DAY_OF_MONTH);

            result = year + "-" + month + "-" + String.format("%02d", day);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
