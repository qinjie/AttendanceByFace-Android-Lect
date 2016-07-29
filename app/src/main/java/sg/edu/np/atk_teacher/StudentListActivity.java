package sg.edu.np.atk_teacher;

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
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.Items.Item_student;
import sg.edu.np.atk_teacher.ArrayAdapters.Student_List_Array_Adapter;
import sg.edu.np.atk_teacher.UtilityClasses.ModifyStatusClass;

public class StudentListActivity extends ListActivity {

    TextView title;
    Student_List_Array_Adapter adapter;
    String lesson_id;
    int start_hour;
    int start_minute;
    String recorded_date;
    Date preDate = new Date();
    int old_status;
    List<Item_student> stu_list;

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list_layout);

        lesson_id = getIntent().getStringExtra("lessonId");
        start_hour = getIntent().getIntExtra("startHour", 0);
        start_minute = getIntent().getIntExtra("startMinute", 0);
        recorded_date = getIntent().getStringExtra("recordedDate");

        title = (TextView) findViewById(R.id.description_browser);

        stu_list = getStudentList();

        adapter = new Student_List_Array_Adapter(StudentListActivity.this, R.layout.student_view, stu_list);
        this.setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Date currDate = new Date();
        long sepTime = currDate.getTime() - preDate.getTime();
        if(sepTime > 1000) {
            preDate = currDate;

            Item_student student = adapter.getItem(position);
            old_status = student.getCurrent_status();
            showChangeStatusDialog(student);
        }

    }

    void showChangeStatusDialog(final Item_student student) {
        final String student_name = student.getName();
        final Dialog dialog = new Dialog(StudentListActivity.this);
        dialog.setTitle("Change Status");
        dialog.setContentView(R.layout.change_status_dialog);
        dialog.setCanceledOnTouchOutside(false);

        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        final TextView reminder = (TextView) dialog.findViewById(R.id.reminder);
        final RadioGroup radio_group = (RadioGroup) dialog.findViewById(R.id.radio_status);
        RadioButton radio_present = (RadioButton) dialog.findViewById(R.id.radioPresent);
        RadioButton radio_late = (RadioButton) dialog.findViewById(R.id.radioLate);
        RadioButton radio_absent = (RadioButton) dialog.findViewById(R.id.radioAbsent);
        final Button change_button = (Button) dialog.findViewById(R.id.change_button);
        final Button cancel_button = (Button) dialog.findViewById(R.id.cancel_butt_dialog);

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
                dialog.cancel();
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
                            modifyStatusFunction(student.getId(), status_list.getInt(String.valueOf(selected_id)), recorded_time);
                            dialog.dismiss();
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
        });

        dialog.show();
    }

    void modifyStatusFunction (final String student_id, final int status, String recorded_time) {
        ModifyStatusClass up = new ModifyStatusClass(student_id, lesson_id, recorded_date, status, recorded_time);

        StringClient client = ServiceGenerator.createService(StringClient.class);
        Call<ResponseBody> call = client.modify_status(up);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
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
                    for(int i = 0; i < stu_list.size(); i++) {
                        Item_student tmp_stu = stu_list.get(i);
                        if(tmp_stu.getId().compareTo(student_id) == 0) {
                            tmp_stu.modifyHistory(old_status, -1);
                            tmp_stu.modifyHistory(status, 1);
                            tmp_stu.setCurrent_status(status);

                            stu_list.set(i, tmp_stu);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }
                }
                catch (Exception e) {
                    Toast.makeText(StudentListActivity.this, "action failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(StudentListActivity.this, "action failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    List getStudentList() {
        List<Item_student> stu_list = new ArrayList<Item_student>();
        stu_list.add(new Item_student("Nguyen Huu Thanh Canh", "NP01001", GV.absent_code, 12, 3, 5));
        stu_list.add(new Item_student("Nguyen Van Tu Thien", "SE03003", GV.attend_code, 1, 10, 9));
        stu_list.add(new Item_student("Do Van Duc", "NP03003", GV.late_code, 11, 1, 8));
        stu_list.add(new Item_student("Nguyen Huu Thanh Canh", "NP01004", GV.notyet_code, 12, 3, 5));
        stu_list.add(new Item_student("Nguyen Van Tu Thien", "NP02005", GV.attend_code, 1, 10, 9));
        stu_list.add(new Item_student("Do Van Duc", "NP03006", GV.late_code, 11, 1, 8));
        stu_list.add(new Item_student("Nguyen Huu Thanh Canh", "NP01007", GV.absent_code, 12, 3, 5));
        stu_list.add(new Item_student("Nguyen Van Tu Thien", "NP02008", GV.attend_code, 1, 10, 9));
        stu_list.add(new Item_student("Do Van Duc", "NP03009", GV.late_code, 11, 1, 8));
        return stu_list;
    }

}
