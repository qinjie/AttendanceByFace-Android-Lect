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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.Item_student;
import sg.edu.np.atk_teacher.BaseClasses.Student_List_Array_Adapter;

public class StudentListActivity extends ListActivity {

    TextView title;
    Student_List_Array_Adapter adapter;
    String lesson_id;
    int start_hour;
    int start_minute;
    Date preDate = new Date();

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list_layout);

        lesson_id = getIntent().getStringExtra("lessonId");
        start_hour = getIntent().getIntExtra("startHour", 0);
        start_minute = getIntent().getIntExtra("startMinute", 0);

        title = (TextView) findViewById(R.id.description_browser);

        List<Item_student> stu_list = getStudentList();

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
            String student_name = student.getName();

            showChangeStatusDialog(student_name);
        }

    }

    void showChangeStatusDialog(final String student_name) {
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
                int selected_id = radio_group.getCheckedRadioButtonId();

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

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
