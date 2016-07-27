package sg.edu.np.atk_teacher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.Items.Item_timetable;
import sg.edu.np.atk_teacher.ArrayAdapters.Timetable_Array_Adapter;

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
    private String auCode;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        final List<Item_timetable> timetable_list = getTimetableList();

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
                                //TODO:complete this using request to server
                                List<Item_timetable> additional_list = getTimetableList();
                                timetable_list.addAll(additional_list);
                                adapter.notifyDataSetChanged();
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

                    startActivity(intent);
                }
            }
        });


        HashSet<String> unique_subjects = new HashSet<>();
        for(Item_timetable item : timetable_list) {
            unique_subjects.add(item.getSubject_name());
        }
        ArrayList<String> options = new ArrayList<String>();
        options.add(ALL_SUBJECT);
        for(String subject : unique_subjects)
            options.add(subject);
        final ArrayAdapter<String> options_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        options_adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(options_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                curr_subject = options_adapter.getItem(position);;

                timetable_list.clear();
                timetable_list.addAll(getTimetableList());
                adapter.notifyDataSetChanged();
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

        // this method will call on close dialog box
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            int year = selectedYear;
            int month = selectedMonth;
            int day = selectedDay;
            //TODO: above is time chosen

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

        if (id == R.id.nav_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivityForResult(intent, REQUEST_CHANGEPASSWORD);
        } else if (id == R.id.nav_log_out) {
            GF.logoutAction(TimeTableActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    List getTimetableList() {
        List<Item_timetable> timetables = new ArrayList<Item_timetable>();
        //TODO: get data from server, according to curr_subject and curr_date
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 7, 15));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 8, 15));
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 9, 15));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 10, 20));
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 11, 15));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 12, 20));
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 13, 20));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 14, 15));
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 15, 20));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 16, 20));
        timetables.add(new Item_timetable("NP0101", "Discrete Math", "abc", 8, 0, 10, 0, "7/14/2016", 1, 15));
        timetables.add(new Item_timetable("NP0101", "Literature", "abc", 8, 0, 10, 0, "7/13/2016", 2, 15));

        return timetables;
    }



}
