package sg.edu.np.atk_teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.UtilityClasses.LoginClass;

/**
 * Created by Lord One on 7/21/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_FORGOT_PASSWORD = 2;

    @InjectView(R.id.input_username)                EditText _usernameText;
    @InjectView(R.id.input_password)                EditText _passwordText;
    @InjectView(R.id.btn_login)                     Button _loginButton;
    @InjectView(R.id.link_forgotPass)               TextView _forgotPassLink;
    @InjectView(R.id.link_signup)                   TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);

        GV.activity = LoginActivity.this;

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loginButton.setEnabled(false);
                login();
            }
        });

        _forgotPassLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivityForResult(intent, REQUEST_FORGOT_PASSWORD);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void login() {

        //TODO: validate before login
//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }

        _loginButton.setEnabled(false);

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        username = "abab";
        password = "123456";

        StringClient client = ServiceGenerator.createService(StringClient.class);

        LoginClass up = new LoginClass(username, password);

        Call<ResponseBody> call = client.login(up);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();

                    if (messageCode == 200) {
                        JSONObject data = new JSONObject(response.body().string());
                        String authorizationCode = data.getString("token");
                        GF.setAuCodeInSP(LoginActivity.this, authorizationCode);
                        onLoginSuccess();
                    }
                    else if(messageCode == 400) {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        onLoginFailed(0);
//                        Notification.showLoginNoti(activity, errorCode);
                    }
                    else {
//                        ErrorClass.showError(LogInActivity.this, 1);
                    }
                } catch (Exception e) {
                    onLoginFailed(0);
                    e.printStackTrace();
//                    ErrorClass.showError(LogInActivity.this, 2);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print("Error Login");
                onLoginFailed(1);
//                ErrorClass.showError(LogInActivity.this, 3);
            }
        });

    }

    private void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, TimeTableActivity.class);
        startActivity(intent);
        finish();
    }

    private void onLoginFailed(int errorCode) {
        if(errorCode == 0)
            Toast.makeText(getBaseContext(), "Login failed, code != 200", Toast.LENGTH_LONG).show();
        if(errorCode == 1)
            Toast.makeText(getBaseContext(), "Login failed, no internet", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 4 || username.length() > 255) {
            _usernameText.setError("enter a valid username address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 255) {
            _passwordText.setError("at least 6 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}

