package sg.edu.np.atk_teacher.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.RequestClasses.SignupClass;

/**
 * Created by Lord One on 7/21/2016.
 */
public class SignupActivity extends AppCompatActivity {
    @InjectView(R.id.input_username)    EditText _usernameText;
    @InjectView(R.id.input_email)       EditText _emailText;
    @InjectView(R.id.input_password)    EditText _passwordText;
    @InjectView(R.id.input_confirmpass) EditText _confirmedPasswordText;
    @InjectView(R.id.btn_signup)        Button _signupButton;
    @InjectView(R.id.link_login)        TextView _loginLink;

    String username;
    String email;
    String password;
    String confirmedPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }

    public void signup() {

        ProgressDia.showDialog(this);
        username = _usernameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        confirmedPassword = _confirmedPasswordText.getText().toString();

        if (!validate()) {
            onSignupFailed(ErrorClass.accInvalid_err);
            return;
        }

        _signupButton.setEnabled(false);

        SignupClass user = new SignupClass(username, email, password);
        signupAction(user);

    }

    public void onSignupSuccess() {
        setResult(RESULT_OK, null);
        ProgressDia.dismissDialog();

        Toast.makeText(getBaseContext(), "Signed up successfully! Please check your Email to verify your account!", Toast.LENGTH_LONG).show();

        onBackPressed();

    }


    public void onSignupFailed(int errorCode) {
        ProgressDia.dismissDialog();
        ErrorClass.showError(this, errorCode);
        _signupButton.setEnabled(true);

    }


    public void signupAction(SignupClass user) {

        StringClient client = ServiceGenerator.createService(StringClient.class);

        Call<ResponseBody> call = client.signup(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    int messageCode = response.code();
                    if(messageCode == 200){
                        onSignupSuccess();
                    }
                    else {
                        JSONObject body = new JSONObject(response.errorBody().string());
                        int errorCode = body.getInt("code");
                        onSignupFailed(ErrorClass.accInvalid_err);
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onSignupFailed(ErrorClass.internet_err);
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if (username.isEmpty() || username.length() < 4 || username.length() > 255) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmedPassword.compareTo(password) != 0) {
            _confirmedPasswordText.setError("These passwords don't match. Try again?");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
