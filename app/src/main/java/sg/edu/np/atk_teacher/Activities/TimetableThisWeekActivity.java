package sg.edu.np.atk_teacher.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;

/**
 * Created by Champ on 29/07/2016.
 */
public class TimetableThisWeekActivity extends NavActivity {

    LinearLayout lay2, lay3, lay4, lay5, lay6, lay7, lay8;
    ArrayList<LinearLayout> tv_list;
    View v;
    JSONObject tv_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_timetable_this_week, frameLayout);

        getTimetableThisWeek();
    }

    void getTimetableThisWeek() {
        ProgressDia.showDialog(this);
        StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
        Call<ResponseBody> call = client.getTimetableThisWeek();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {

                        initTextView();

                        JSONArray body = new JSONArray(response.body().string());
                        for(int i = 0; i < body.length(); i++) {
                            JSONObject specific_lesson = body.getJSONObject(i);
                            String weekday = specific_lesson.getString("weekday");
                            int index = tv_map.getInt(weekday);

//                            String description = specific_lesson.getString("class_section") + '\n' +
//                                                 specific_lesson.getString("start_time") + " - " + specific_lesson.getString("end_time") + '\n' +
//                                                 specific_lesson.getString("location");
//
//                            TextView tv = new TextView(TimetableThisWeekActivity.this);
//                            tv.setText(description);
//                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//                            tv_list.get(index).addView(tv);
//                            tv_list.get(index).addView(getSeparateLine());

                            String class_time = specific_lesson.getString("start_time") + " - " + specific_lesson.getString("end_time");
                            String description = specific_lesson.getString("class_section") + '\n' +
                                                 specific_lesson.getString("location");

                            LinearLayout new_class = new LinearLayout(TimetableThisWeekActivity.this);
                            new_class.setOrientation(LinearLayout.HORIZONTAL);
                            new_class.setGravity(Gravity.CENTER_VERTICAL);

                            TextView tv_time = new TextView(TimetableThisWeekActivity.this);
                            tv_time.setText(class_time);
                            tv_time.setTextColor(Color.BLUE);
                            tv_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                            TextView tv_des = new TextView(TimetableThisWeekActivity.this);
                            tv_des.setText(description);
                            tv_des.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            tv_des.setPadding(15, 0, 0, 0);

                            new_class.addView(tv_time);
                            new_class.addView(tv_des);

                            tv_list.get(index).addView(new_class);
                            tv_list.get(index).addView(getSeparateLine());
                        }
                        onGetTimetableSuccess();
                    } else {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        onGetTimetableFailed(ErrorClass.unauthorized_err);
                    }
                }
                catch (Exception e) {
                    onGetTimetableFailed(ErrorClass.wrongFormat_err);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onGetTimetableFailed(ErrorClass.internet_err);
            }
        });
    }

    void initTextView() {
        lay2 = (LinearLayout) findViewById(R.id.lay_mon);
        lay3 = (LinearLayout) findViewById(R.id.lay_tue);
        lay4 = (LinearLayout) findViewById(R.id.lay_wed);
        lay5 = (LinearLayout) findViewById(R.id.lay_thu);
        lay6 = (LinearLayout) findViewById(R.id.lay_fri);
        lay7 = (LinearLayout) findViewById(R.id.lay_sat);
        lay8 = (LinearLayout) findViewById(R.id.lay_sun);

        tv_list = new ArrayList<LinearLayout>();
        tv_list.add(lay2);
        tv_list.add(lay3);
        tv_list.add(lay4);
        tv_list.add(lay5);
        tv_list.add(lay6);
        tv_list.add(lay7);
        tv_list.add(lay8);

        tv_map = new JSONObject();
        try {
            tv_map.put("MON", 0);
            tv_map.put("TUES", 1);
            tv_map.put("WED", 2);
            tv_map.put("THUR", 3);
            tv_map.put("FRI", 4);
            tv_map.put("SAT", 5);
            tv_map.put("SUN", 6);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    View getSeparateLine() {
        v = new View(TimetableThisWeekActivity.this);
        v.setBackgroundColor(Color.BLACK);
        v.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.getLayoutParams().height = 1;
        return v;
    }

    void onGetTimetableSuccess() {
        ProgressDia.dismissDialog();
    }
    void onGetTimetableFailed(int err) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(this, err);
    }
}
