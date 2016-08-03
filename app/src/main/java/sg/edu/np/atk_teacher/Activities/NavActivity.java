package sg.edu.np.atk_teacher.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CHANGEPASSWORD = 1;
    protected FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_current_classes) {
            Intent intent = new Intent(this, TimeTableActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_timetable_this_week) {
            Intent intent = new Intent(this, TimetableThisWeekActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_allow_train_face) {
            allowTrainFace();
        } else if (id == R.id.nav_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivityForResult(intent, REQUEST_CHANGEPASSWORD);
        } else if (id == R.id.nav_log_out) {
            GF.logoutAction(NavActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        ErrorClass.showError(NavActivity.this, ErrorClass.internet_err);
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
                        ErrorClass.showError(NavActivity.this, ErrorClass.internet_err);
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
}
