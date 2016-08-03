package sg.edu.np.atk_teacher.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.RequestClasses.ResetPassClass;

/**
 * Created by Lord One on 7/21/2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    EditText _emailTextView;
    TextView noti;
    Button resetButton;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        noti = (TextView) findViewById(R.id.noti_reset_password);
        _emailTextView = (EditText) findViewById(R.id.input_email_to_reset);
        resetButton = (Button) findViewById(R.id.button_forget_pass) ;

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewPass();
            }
        });

    }


    public void requestNewPass() {

        ProgressDia.showDialog(this);

        email = _emailTextView.getText().toString();

        if (!validate()) {
            return;
        }

        resetButton.setEnabled(false);

        StringClient client = ServiceGenerator.createService(StringClient.class);

        ResetPassClass up = new ResetPassClass(email);

        Call<ResponseBody> call = client.postResetPassword(up);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        onResetSuccess();
                    } else {
                        JSONObject body = new JSONObject(response.errorBody().string());
                        int errorCode = body.getInt("code");
                        onResetFailed(ErrorClass.noEmail_err);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onResetFailed(ErrorClass.internet_err);
            }
        });

    }



    public void onResetSuccess() {
        ProgressDia.dismissDialog();
        noti.setText("A message has been sent to your Email Adresss! Please check the email and follow the instructions to reclaim your password!");
        noti.setTextColor(Color.GREEN);
        noti.setVisibility(View.VISIBLE);

    }

    public void onResetFailed(int errorCode) {
        ProgressDia.dismissDialog();
        if(errorCode == ErrorClass.noEmail_err) {
            noti.setText("No user with given email");
            noti.setTextColor(Color.RED);
            noti.setVisibility(View.VISIBLE);
        }
        if(errorCode == ErrorClass.internet_err)
            Toast.makeText(ForgotPasswordActivity.this, "Connect to server failed! Please check your internet connection", Toast.LENGTH_LONG).show();
//        Preferences.dismissLoading();
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
//
        resetButton.setEnabled(true);

    }

    public boolean validate() {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailTextView.setError("enter a valid email address");
            valid = false;
        } else {
            _emailTextView.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
