package sg.edu.np.atk_teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.atk_teacher.R;
import sg.edu.np.atk_teacher.TimeTableActivity;

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
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        // Interact with local server
        //==========================
        //TODO
//        SignupClass user = new SignupClass(username, password, email, studentId, this);
//        signupAction(user);

        //--------------------------

    }

    public void onSignupSuccess() {
        setResult(RESULT_OK, null);

        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, TimeTableActivity.class);
        startActivity(intent);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);

    }


//    public void signupAction(SignupClass user) {
//
//        String returnMessage = "";
//
//        StringClient client = ServiceGenerator.createService(StringClient.class);
//
//        Call<ResponseBody> call = client.signup(user);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//
//                    int messageCode = response.code();
//                    if(messageCode == 200){
//                        onSignupSuccess();
//                    }
//                    else{
//                        // handle when cannot signup
//                        onSignupFailed();
//                        Notification.showMessage(SignUpActivity.this, 5);
//                        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                        startActivity(intent);
//                    }
//
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                    ErrorClass.showError(SignUpActivity.this, 27);
//                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ErrorClass.showError(SignUpActivity.this, 28);
//            }
//        });
//    }

    private boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();


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
