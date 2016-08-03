package sg.edu.np.atk_teacher.Activities;

/**
 * Created by Lord One on 7/19/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.Items.Item_student;
import sg.edu.np.atk_teacher.ArrayAdapters.Student_List_Array_Adapter;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.RequestClasses.ModifyStatusClass;

public class StudentListActivity extends TimeTableActivity {

    TextView title;
    ListView listView;
    Student_List_Array_Adapter adapter;
    String lesson_id;
    int start_hour;
    int start_minute;
    int end_hour;
    int end_minute;
    String recorded_date;
    String class_section;
    String location;
    Date preDate = new Date();
    int old_status;
    List<Item_student> stu_list;

    Activity activity = this;
    int result_value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.student_list_layout);
        getLayoutInflater().inflate(R.layout.student_list_layout, frameLayout);

        listView = (ListView) findViewById(R.id.list_stu);

        lesson_id = getIntent().getStringExtra("lessonId");
        start_hour = getIntent().getIntExtra("startHour", 0);
        start_minute = getIntent().getIntExtra("startMinute", 0);
        end_hour = getIntent().getIntExtra("endHour", 0);
        end_minute = getIntent().getIntExtra("endMinute", 0);
        recorded_date = getIntent().getStringExtra("recordedDate");
        class_section = getIntent().getStringExtra("classSection");
        location = getIntent().getStringExtra("location");

        title = (TextView) findViewById(R.id.description_browser);
        title.setText(class_section + "\n" +
                      String.format("%02d", start_hour) + ":" + String.format("%02d", start_minute) + " - " + String.format("%02d", end_hour) + ":" + String.format("%02d", end_minute) + "\n" +
                      recorded_date + "\n" +
                      location);

        stu_list = new ArrayList<>();
        adapter = new Student_List_Array_Adapter(StudentListActivity.this, R.layout.student_view, stu_list);

        ProgressDia.showDialog(this);
        getStudentList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Date currDate = new Date();
                long sepTime = currDate.getTime() - preDate.getTime();
                if(sepTime > 1000) {
                    preDate = currDate;

                    Item_student student = adapter.getItem(position);
                    old_status = student.getCurrent_status();
                    showChangeStatusDialog(student);
                }

            }
        });
    }

    void showChangeStatusDialog(final Item_student student) {
        final String student_name = student.getName();
        final Dialog changeStatusDialog = new Dialog(StudentListActivity.this);
        changeStatusDialog.setTitle("Change Status");
        changeStatusDialog.setContentView(R.layout.change_status_dialog);
        changeStatusDialog.setCanceledOnTouchOutside(false);

        TextView student_name_tv = (TextView) changeStatusDialog.findViewById(R.id.student_name_of_change_stt);
        TextView student_id_tv = (TextView) changeStatusDialog.findViewById(R.id.student_id_of_change_stt);
        TextView class_section_tv = (TextView) changeStatusDialog.findViewById(R.id.class_section_of_change_stt);
        TextView subject_time_tv = (TextView) changeStatusDialog.findViewById(R.id.timeanddate_of_change_stt);
        TextView location_tv = (TextView) changeStatusDialog.findViewById(R.id.location_of_change_stt);

        student_name_tv.setText(student.getName());
        student_id_tv.setText("ID: " + student.getId());
        class_section_tv.setText(class_section);
        subject_time_tv.setText(String.format("%02d", start_hour) + ":" + String.format("%02d", start_minute) + ", " + recorded_date);
        location_tv.setText(location);

        final TimePicker timePicker = (TimePicker) changeStatusDialog.findViewById(R.id.timePicker);
        final TextView reminder = (TextView) changeStatusDialog.findViewById(R.id.reminder);
        final RadioGroup radio_group = (RadioGroup) changeStatusDialog.findViewById(R.id.radio_status);
        RadioButton radio_present = (RadioButton) changeStatusDialog.findViewById(R.id.radioPresent);
        RadioButton radio_late = (RadioButton) changeStatusDialog.findViewById(R.id.radioLate);
        RadioButton radio_absent = (RadioButton) changeStatusDialog.findViewById(R.id.radioAbsent);
        final Button change_button = (Button) changeStatusDialog.findViewById(R.id.change_button);
        final Button cancel_button = (Button) changeStatusDialog.findViewById(R.id.cancel_butt_dialog);

        change_button.setEnabled(false);

        timePicker.setIs24HourView(true);
        if(Build.VERSION.SDK_INT < 23) {
            timePicker.setCurrentHour(start_hour);
            timePicker.setCurrentMinute(start_minute + 1);
        }
        else {
            timePicker.setHour(start_hour);
            timePicker.setMinute(start_minute + 1);
        }
        timePicker.setEnabled(false);

        reminder.setVisibility(View.INVISIBLE);

        radio_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setEnabled(false);
                reminder.setVisibility(View.INVISIBLE);
                change_button.setEnabled(true);
            }
        });
        radio_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setEnabled(true);
                reminder.setVisibility(View.VISIBLE);
                change_button.setEnabled(true);
            }
        });
        radio_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setEnabled(false);
                reminder.setVisibility(View.INVISIBLE);
                change_button.setEnabled(true);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatusDialog.cancel();
            }
        });

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int selected_id = radio_group.getCheckedRadioButtonId();
                final JSONObject status_list = new JSONObject();
                try {
                    status_list.put(String.valueOf(R.id.radioPresent), GV.attend_code);
                    status_list.put(String.valueOf(R.id.radioLate), GV.late_code);
                    status_list.put(String.valueOf(R.id.radioAbsent), GV.absent_code);
                }
                catch (Exception e) {}

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                int hour_picked = timePicker.getCurrentHour();
                int minute_picked = timePicker.getCurrentMinute();

                if(selected_id != R.id.radioLate || IsValidTime(start_hour, start_minute, hour_picked, minute_picked)) {
                    builder.setTitle("Confirmation");

                    if(selected_id == R.id.radioPresent) {
                        builder.setMessage("Are you sure you want to change " + student_name + "'s status to Present?");
                    }
                    else if(selected_id == R.id.radioLate) {
                        if(Build.VERSION.SDK_INT < 23)
                            builder.setMessage("Are you sure you want to change " + student_name + "'s status to Late (at " +
                                    String.format("%02d", timePicker.getCurrentHour()) + ":" + String.format("%02d", timePicker.getCurrentMinute()) + ")?");
                        else builder.setMessage("Are you sure you want to change " + student_name + "'s status to Late (at " +
                                String.format("%02d", timePicker.getHour()) + ":" + String.format("%02d", timePicker.getMinute()) + ")?");
                    }
                    else if(selected_id == R.id.radioAbsent) {
                        builder.setMessage("Are you sure you want to change " + student_name + "'s status to Absent?");
                    }

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String recorded_time = "";
                                if(selected_id == R.id.radioLate) {
                                    if(Build.VERSION.SDK_INT < 23)
                                        recorded_time = String.format("%02d", timePicker.getCurrentHour()) + ":" + String.format("%02d", timePicker.getCurrentMinute());
                                    else
                                        recorded_time = String.format("%02d", timePicker.getHour()) + ":" + String.format("%02d", timePicker.getMinute());
                                }
                                dialog.dismiss();
                                changeStatusDialog.dismiss();
                                ProgressDia.showDialog(activity);
                                modifyStatusFunction(student.getId(), status_list.getInt(String.valueOf(selected_id)), recorded_time);

                            }
                            catch (Exception e){}
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    try {
                        alertDialog.show();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else {
                    builder.setTitle("Time invalid");
                    builder.setMessage("Please choose a reasonable time for status Late");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    try{
                        alertDialog.show();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        changeStatusDialog.show();
    }

    void modifyStatusFunction (final String student_id, final int status, String recorded_time) {
        ModifyStatusClass up = new ModifyStatusClass(student_id, lesson_id, recorded_date, status, recorded_time);

        StringClient client = ServiceGenerator.createService(StringClient.class);
        Call<ResponseBody> call = client.modify_status(up);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.code() == 200) {
                        // thong bao ok cho user
                        new AlertDialog.Builder(activity)
                                .setTitle("Change notification")
                                .setMessage("Status changed successfully!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        // thay doi mau sac tren UI: 2 cho~
                        for (int i = 0; i < stu_list.size(); i++) {
                            Item_student tmp_stu = stu_list.get(i);
                            String tmp_id = tmp_stu.getId();
                            if (tmp_id.compareTo(student_id) == 0) {
                                result_value += tmp_stu.modifyHistory(old_status, status);
                                tmp_stu.setCurrent_status(status);

                                stu_list.set(i, tmp_stu);
                                adapter.notifyDataSetChanged();

                                setResult(result_value);
                                break;
                            }
                        }
                        onChangeStatusSuccess();
                    }
                    else {
                        onChangeStatusFailed(ErrorClass.unauthorized_err);
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                    }

                }
                catch (Exception e) {
                    onGetStudentListFailed(ErrorClass.wrongFormat_err);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onChangeStatusFailed(ErrorClass.internet_err);
            }
        });
    }

    boolean IsValidTime(int start_hour, int start_minute, int chosen_hour, int chosen_minute) {
        int diff = (chosen_hour - start_hour) * 60 + (chosen_minute - start_minute);
        return (diff > 0 && diff <= GV.max_late_time);
    }

    void getStudentList() {

        StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
        Call<ResponseBody> call = client.getStudentList(lesson_id, recorded_date);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        JSONArray body = new JSONArray(response.body().string());
                        for(int i = 0; i < body.length(); i++) {
                            JSONObject item = body.getJSONObject(i);
                            Item_student stu = new Item_student(item.getString("student_name"), item.getString("student_id"),
                                                                item.getInt("status"), item.getInt("countPresent"),
                                                                item.getInt("countLate"), item.getInt("countAbsent"));
                            stu_list.add(stu);
                        }
                        adapter.notifyDataSetChanged();
                        onGetStudentListSuccess();
                    }
                    catch (Exception e) {
                        onGetStudentListFailed(ErrorClass.wrongFormat_err);
                    }
                }
                else {
                    onGetStudentListFailed(ErrorClass.unauthorized_err);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onGetStudentListFailed(ErrorClass.internet_err);
            }
        });

        adapter = new Student_List_Array_Adapter(StudentListActivity.this, R.layout.student_view, stu_list);
        listView.setAdapter(adapter);
    }

    void onGetStudentListSuccess() {
        ProgressDia.dismissDialog();
    }

    void onGetStudentListFailed(int err) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(activity, err);
    }

    void onChangeStatusSuccess() {
        ProgressDia.dismissDialog();
    }

    void onChangeStatusFailed(int err) {
        ProgressDia.dismissDialog();
    }

}
