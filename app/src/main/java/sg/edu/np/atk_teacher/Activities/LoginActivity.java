package sg.edu.np.atk_teacher.Activities;

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
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.GF;
import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.RequestClasses.LoginClass;

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

//        login();
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

        ProgressDia.showDialog(this);

        if (!validate()) {
            onLoginFailed(ErrorClass.accInvalid_err);
            return;
        }

        _loginButton.setEnabled(false);

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

//        username = "abab";
//        password = "123456";

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
                        onLoginFailed(ErrorClass.accInvalid_err);
                    }
                    else {
                        onLoginFailed(ErrorClass.unauthorized_err);
                    }
                } catch (Exception e) {
                    onLoginFailed(ErrorClass.wrongFormat_err);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print("Error Login");
                onLoginFailed(ErrorClass.internet_err);
            }
        });

    }

    private void onLoginSuccess() {
        ProgressDia.dismissDialog();
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, TimeTableActivity.class);
        startActivity(intent);
        finish();
    }

    private void onLoginFailed(int errorCode) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(this, errorCode);
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 4 || username.length() > 255) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 255) {
            _passwordText.setError("between 6 and 255 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}

