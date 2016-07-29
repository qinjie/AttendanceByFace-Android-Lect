package sg.edu.np.atk_teacher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.Items.Item_timetable;
import sg.edu.np.atk_teacher.ArrayAdapters.Timetable_Array_Adapter;
import sg.edu.np.atk_teacher.UtilityClasses.LoginClass;
import sg.edu.np.atk_teacher.UtilityClasses.TimetableClass;

public class TimeTableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CHANGEPASSWORD = 1;

    Timetable_Array_Adapter adapter;
    ListView listView;
    private int preLast;
    private Date preDate = new Date();
    private Spinner spinner;
    private ImageButton date_picker;
    private final String ALL_SUBJECT = "All Subjects";
    private String curr_subject = ALL_SUBJECT;
    private String curr_date;
    private Activity activity;
    final List<Item_timetable> timetable_list = new ArrayList<>();
    ArrayList<String> options = new ArrayList<String>();
    ArrayAdapter<String> options_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        options_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);

        initDate();
        activity = this;
        date_picker = (ImageButton) findViewById(R.id.datePickerButt);
        spinner = (Spinner) findViewById(R.id.spinner);

        ButterKnife.inject(this);

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
                                Toast.makeText(TimeTableActivity.this, "loading data...", Toast.LENGTH_SHORT).show();
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

                    Item_timetable item = adapter.getItem(position);

                    Intent intent = new Intent(TimeTableActivity.this, StudentListActivity.class);
                    intent.putExtra("lessonId", item.getLesson_id());
                    intent.putExtra("startHour", item.getStart_hour());
                    intent.putExtra("startMinute", item.getStart_minute());
                    intent.putExtra("recordedDate", item.getDate());

                    startActivity(intent);
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
        }else if (id == R.id.nav_change_password) {
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
                    }
                    catch (Exception e){}
                }
                else {
                    Toast.makeText(TimeTableActivity.this, "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void getTimetableList(boolean toReset) {
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

                        for(int i = 0; i < timetable.length(); i++) {
                            JSONObject data = timetable.getJSONObject(i);
                            int start_hour = Integer.valueOf(data.getString("start_time").substring(0, 2));
                            int start_minute = Integer.valueOf(data.getString("start_time").substring(3, 5));
                            int end_hour = Integer.valueOf(data.getString("end_time").substring(0, 2));
                            int end_minute = Integer.valueOf(data.getString("start_time").substring(3, 5));
                            Item_timetable item = new Item_timetable(data.getString("class_section"), data.getString("lesson_id"),
                                                                     start_hour, start_minute, end_hour, end_minute,
                                                                     data.getString("date"), data.getInt("presentStudent"), data.getInt("totalStudent"));
                            timetable_list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(TimeTableActivity.this, "Load Timetable error code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

//    List getHardCodeTimetableList() {
//        List<Item_timetable> timetables = new ArrayList<Item_timetable>();
//        //TODO: get data from server, according to curr_subject and curr_date
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 7, 15));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 8, 15));
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 9, 15));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 10, 20));
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 11, 15));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 12, 20));
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 13, 20));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 14, 15));
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 15, 20));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 16, 20));
//        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 1, 15));
//        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 2, 15));
//
//        return timetables;
//    }


}
