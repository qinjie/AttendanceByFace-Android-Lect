package sg.edu.np.atk_teacher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lord One on 7/21/2016.
 */
public class ChangePasswordActivity extends AppCompatActivity {
    @InjectView(R.id.input_old_password)    EditText _currentpasswordText;
    @InjectView(R.id.input_password)        EditText _passwordText;
    @InjectView(R.id.input_confirmpass)     EditText _confirmedPasswordText;
    @InjectView(R.id.btn_changePass)        Button _changePassButton;

    String currentPassword;
    String password;
    String confirmedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.inject(this);

        _changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });
    }

    public void changePass() {

        if (!validate()) {
            onChangePasswordFailed(-3);
            return;
        }

        _changePassButton.setEnabled(false);

        currentPassword = _currentpasswordText.getText().toString();
        password = _passwordText.getText().toString();
        confirmedPassword = _confirmedPasswordText.getText().toString();

        changePasswordAction();

    }


    public void onChangePasswordSuccess() {
        setResult(RESULT_OK, null);
//        Notification.showMessage(ChangePasswordActivity.this, 7);
        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_LONG).show();
        onBackPressed();
//        Intent intent = new Intent(ChangePasswordActivity.this, LogInActivity.class);
//        startActivity(intent);

    }

    public void onChangePasswordFailed(int err) {

        _currentpasswordText.setText("");
        _passwordText.setText("");
        _confirmedPasswordText.setText("");

        _changePassButton.setEnabled(true);

    }

    public boolean validate() {
        boolean valid = true;

        String currentPassword = _currentpasswordText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        if (currentPassword.isEmpty() || currentPassword.length() < 4 || currentPassword.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password.compareTo(currentPassword) == 0) {
            _passwordText.setError("new password must be different with current password");
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

    void changePasswordAction() {
//        SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
//        String auCode = pref.getString("authorizationCode", null);
//
//        JsonObject toUp = new JsonObject();
//        toUp.addProperty("oldPassword", currentPassword);
//        toUp.addProperty("newPassword", password);
//
//        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
//        Call<ResponseBody> call = client.changePassword(toUp);
//
//        call.enqueue (new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    int messageCode = response.code();
//                    if (messageCode == 200) {
//                        onChangePasswordSuccess();
//                    } else if (messageCode == 400) {
//                        JSONObject data = new JSONObject(response.errorBody().string());
//                        int errorCode = data.getInt("code");
//                        if(errorCode == 1)
//                            onChangePasswordFailed(1);
//                        else if(errorCode == 8)
//                            onChangePasswordFailed(8);
//
//                    } else {
//                        onChangePasswordFailed(-1);
//                    }
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                    onChangePasswordFailed(2);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                onChangePasswordFailed(-2);
//            }
//        });

    }

}
