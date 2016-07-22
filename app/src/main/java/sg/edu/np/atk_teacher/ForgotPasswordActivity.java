package sg.edu.np.atk_teacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.InjectView;

/**
 * Created by Lord One on 7/21/2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    @InjectView(R.id.input_email)           EditText _emailText;
    Button resetButton;
    @InjectView(R.id.noti_reset_password)   TextView noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetButton = (Button) findViewById(R.id.button_forget_pass) ;
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewPass();
            }
        });

    }


    public void requestNewPass() {

        if (!validate()) {
            onResetFailed();
            return;
        }

        resetButton.setEnabled(false);

        String email = _emailText.getText().toString();

        // Interact with local server
        //==========================

        //TODO
        onResetSuccess();
        //--------------------------

    }



    public void onResetSuccess() {
        //TODO
        noti.setVisibility(View.VISIBLE);
//        Preferences.dismissLoading();
//        setResult(RESULT_OK, null);
//
//        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
//        startActivity(intent);

    }

    public void onResetFailed() {
        //TODO
        noti.setVisibility(View.INVISIBLE);
//        Preferences.dismissLoading();
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
//
        resetButton.setEnabled(true);

    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
