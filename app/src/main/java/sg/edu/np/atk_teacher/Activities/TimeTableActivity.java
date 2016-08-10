package sg.edu.np.atk_teacher.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import sg.edu.np.atk_teacher.Items.Item_timetable_seperator;
import sg.edu.np.atk_teacher.R;

public class TimeTableActivity extends NavActivity {

    Timetable_Array_Adapter adapter;
    ListView listView;
    private int preLast;
    private Date preDate = new Date();
    private ImageButton spinner;
    private ImageButton date_picker;
    private final String ALL_SUBJECT = "Subjects";
    private String curr_subject = ALL_SUBJECT;
    private String curr_date;
    private String past_date;
    private String request_date;
    private TextView date_tv;
    private int chosenPosition;
    private TextView curr_subject_tv;
    private Activity activity;
    final List timetable_list = new ArrayList<>();
    ArrayList<String> options = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.content_timetable, frameLayout);

        initDate();
        activity = this;
        date_picker = (ImageButton) findViewById(R.id.datePickerButt);
        spinner = (ImageButton) findViewById(R.id.spinner);
        curr_subject_tv = (TextView) findViewById(R.id.subject_chosen_tv);
        date_tv = (TextView) findViewById(R.id.date_tv);

        setLecturerName();

        adapter = new Timetable_Array_Adapter(TimeTableActivity.this, R.layout.item_timetable_view, R.layout.timetable_separator, timetable_list);

        getDropdownList();
        getTimetableList(true);

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
                if(sepTime > 1000 && adapter.getItemViewType(position) == Timetable_Array_Adapter.TYPE_ITEM) {
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


        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(options == null || options.size() == 0)
                    return;

                CharSequence[] cs = options.toArray(new CharSequence[options.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableActivity.this);
                builder.setTitle("Select subject");
                builder.setIcon(R.drawable.dark_green_round_with_stroke);

                builder.setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), options.get(item), Toast.LENGTH_SHORT).show();
                        curr_subject = options.get(item);
                        curr_subject_tv.setText(curr_subject);
                        getTimetableList(true);
                    }
                });

                final AlertDialog alert = builder.create();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.show();
                    }
                });

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
            Item_timetable tmp_timetable = (Item_timetable) timetable_list.get(chosenPosition);
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
        if(toReset) {
            timetable_list.clear();
            date_tv.setText(GF.beautifyDate(curr_date));
            request_date = curr_date;
            adapter.clearSpIdx();
        }
        else
            request_date = past_date;

        StringClient client = ServiceGenerator.createService(StringClient.class, GV.auCode);

        String classSection;
        if(curr_subject.compareTo(ALL_SUBJECT) == 0)
            classSection = "all";
        else
            classSection = curr_subject;
        Call<ResponseBody> call = client.getListClasses(request_date, classSection);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        JSONObject body = new JSONObject(response.body().string());
                        JSONArray timetable = body.getJSONArray("timetable");
                        JSONObject [] data = new JSONObject[timetable.length()];
                        past_date = body.getString("nextFromDate");

                        if(timetable.length() > 0) {
                            for (int i = 0; i < timetable.length(); i++) {
                                data[i] = timetable.getJSONObject(i);
                                if(i == 0 || isOnDifferentDate(data[i - 1], data[i])) {
                                    Item_timetable_seperator item = new Item_timetable_seperator(data[i]);
                                    adapter.addSeparatorIdx(timetable_list.size());
                                    timetable_list.add(item);
                                }

                                Item_timetable item = new Item_timetable(data[i]);
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

    public void dateClick(View v) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(activity, datePickerListener, year, month, day).show();
    }

    public boolean isOnDifferentDate(JSONObject d1, JSONObject d2) {
        boolean result = false;
        try{
            String date1 = d1.getString("date");
            String date2 = d2.getString("date");
            if(date1.compareTo(date2) != 0)
                result = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
