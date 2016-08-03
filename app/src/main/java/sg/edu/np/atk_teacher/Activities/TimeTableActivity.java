package sg.edu.np.atk_teacher.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.Items.Item_timetable;
import sg.edu.np.atk_teacher.ArrayAdapters.Timetable_Array_Adapter;
import sg.edu.np.atk_teacher.R;

public class TimeTableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CHANGEPASSWORD = 1;

    protected FrameLayout frameLayout;

    Timetable_Array_Adapter adapter;
    ListView listView;
    private int preLast;
    private Date preDate = new Date();
    private Spinner spinner;
    private ImageButton date_picker;
    private final String ALL_SUBJECT = "All Subjects";
    private String curr_subject = ALL_SUBJECT;
    private String curr_date;
    private int chosenPosition;
    private Activity activity;
    final List<Item_timetable> timetable_list = new ArrayList<>();
    ArrayList<String> options = new ArrayList<String>();
    ArrayAdapter<String> options_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
//        getLayoutInflater().inflate(R.layout.activity_timetable, frameLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        options_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);

        initDate();
        activity = this;
        date_picker = (ImageButton) findViewById(R.id.datePickerButt);
        spinner = (Spinner) findViewById(R.id.spinner);

//        ButterKnife.inject(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getDropdownList();

        adapter = new Timetable_Array_Adapter(TimeTableActivity.this, R.layout.item_timetable_view, timetable_list);

        listView = (ListView) findViewById(android.R.id.list);

        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch(view.getId()) {
                    case android.R.id.list:

                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if(lastItem == totalItemCount) {
                            if(preLast!=lastItem) {
                                preLast = lastItem;
//                                Toast.makeText(TimeTableActivity.this, "loading data...", Toast.LENGTH_SHORT).show();
                                getTimetableList(false);
                            }
                        }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Date currDate = new Date();
                long sepTime = currDate.getTime() - preDate.getTime();
                if(sepTime > 1000) {
                    preDate = currDate;

                    chosenPosition = position;
                    Item_timetable item = adapter.getItem(position);

                    Intent intent = new Intent(TimeTableActivity.this, StudentListActivity.class);
                    intent.putExtra("lessonId", item.getLesson_id());
                    intent.putExtra("startHour", item.getStart_hour());
                    intent.putExtra("startMinute", item.getStart_minute());
                    intent.putExtra("endHour", item.getEnd_hour());
                    intent.putExtra("endMinute", item.getEnd_minute());
                    intent.putExtra("recordedDate", item.getDate());
                    intent.putExtra("classSection", item.getClass_section());
                    intent.putExtra("location", item.getLocation());

                    startActivityForResult(intent, 0);
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    curr_subject = options_adapter.getItem(position);
                    getTimetableList(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(activity, datePickerListener, year, month, day).show();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            if (view.isShown()) {

                int year = selectedYear;
                int month = selectedMonth + 1;
                int day = selectedDay;

                curr_date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                getTimetableList(true);
            }
        }
    };

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data) {
        if(request_code == 0 && result_code != 0) {
            Item_timetable tmp_timetable = timetable_list.get(chosenPosition);
            tmp_timetable.add_to_n_students_taken(result_code);
            timetable_list.set(chosenPosition, tmp_timetable);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_timetable_this_week) {
            Intent intent = new Intent(this, TimetableThisWeekActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_allow_train_face) {
            allowTrainFace();
        } else if (id == R.id.nav_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivityForResult(intent, REQUEST_CHANGEPASSWORD);
        } else if (id == R.id.nav_log_out) {
            GF.logoutAction(TimeTableActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void initDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

        curr_date = year + "-" + month + "-" + day;
    }

    void getDropdownList() {
        ProgressDia.showDialog(this);
        StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
        Call<ResponseBody> call = client.getLecturerSubjectList();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        options.clear();
                        String _data = response.body().string();
                        _data = _data.replace("[", "").replace("]", "").replace("\"", "");
                        List<String> data = new ArrayList<String>(Arrays.asList(_data.split(",")));
                        options.add(ALL_SUBJECT);
                        options.addAll(data);
                        options_adapter.setDropDownViewResource(R.layout.spinner_layout);
                        spinner.setAdapter(options_adapter);

                        onGetDropdownListSuccess();
                    }
                    catch (Exception e){
                        onGetDropdownListFailed(ErrorClass.wrongFormat_err);
                    }
                }
                else {
                    onGetDropdownListFailed(ErrorClass.unauthorized_err);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onGetDropdownListFailed(ErrorClass.internet_err);
            }
        });
    }

    void getTimetableList(final boolean toReset) {
        ProgressDia.showDialog(this);
        if(toReset)
            timetable_list.clear();

        StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);

        String classSection;
        if(curr_subject.compareTo(ALL_SUBJECT) == 0)
            classSection = "all";
        else
            classSection = curr_subject;
        Call<ResponseBody> call = client.getListClasses(curr_date, classSection);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        JSONObject body = new JSONObject(response.body().string());
                        JSONArray timetable = body.getJSONArray("timetable");
                        curr_date = body.getString("nextFromDate");

                        if(timetable.length() > 0) {
                            for (int i = 0; i < timetable.length(); i++) {
                                JSONObject data = timetable.getJSONObject(i);
                                int start_hour = Integer.valueOf(data.getString("start_time").substring(0, 2));
                                int start_minute = Integer.valueOf(data.getString("start_time").substring(3, 5));
                                int end_hour = Integer.valueOf(data.getString("end_time").substring(0, 2));
                                int end_minute = Integer.valueOf(data.getString("start_time").substring(3, 5));
                                Item_timetable item = new Item_timetable(data.getString("class_section"), data.getString("lesson_id"),
                                        start_hour, start_minute, end_hour, end_minute,
                                        data.getString("date"), data.getInt("presentStudent"), data.getInt("totalStudent"),
                                        data.getString("location"));
                                timetable_list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            if(toReset)
                                Toast.makeText(TimeTableActivity.this, "No data", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(TimeTableActivity.this, "No more data", Toast.LENGTH_LONG).show();
                        }
                        onGetStudentListSuccess();
                    }
                    catch (Exception e) {
                        onGetStudentListFailed(ErrorClass.wrongFormat_err);
                        e.printStackTrace();
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

    }

    void allowTrainFace() {

        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Allow train face");
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

                JsonObject up = new JsonObject();
                up.addProperty("student_id", sid);

                StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);
                Call<ResponseBody> call = client.allowTrainFace(up);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                noti_tv.setTextColor(Color.GREEN);
                                noti_tv.setText("Student with ID \"" + sid + "\" is allowed to re-train face in 10 minutes");
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
                        ErrorClass.showError(TimeTableActivity.this, ErrorClass.internet_err);
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
                up.addProperty("student_id", sid);

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
                        ErrorClass.showError(TimeTableActivity.this, ErrorClass.internet_err);
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

    void onGetDropdownListSuccess() {
        ProgressDia.dismissDialog();
    }
    void onGetDropdownListFailed(int err) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(activity, err);
    }
    void onGetStudentListSuccess() {
        ProgressDia.dismissDialog();
    }
    void onGetStudentListFailed(int err) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(activity, err);
    }

}
