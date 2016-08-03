package sg.edu.np.atk_teacher.Activities;

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
import sg.edu.np.atk_teacher.AuxiliaryClasses.ProgressDia;
import sg.edu.np.atk_teacher.BaseClasses.ErrorClass;
import sg.edu.np.atk_teacher.BaseClasses.ServiceGenerator;
import sg.edu.np.atk_teacher.BaseClasses.StringClient;
import sg.edu.np.atk_teacher.R;

/**
 * Created by Lord One on 7/21/2016.
 */
public class ChangePasswordActivity extends TimeTableActivity {
    @InjectView(R.id.input_old_password_cpd)    EditText _currentpasswordText;
    @InjectView(R.id.input_password_cpd)        EditText _newPasswordText;
    @InjectView(R.id.input_confirmpass_cpd)     EditText _confirmedPasswordText;
    @InjectView(R.id.btn_changePass)            Button _changePassButton;

    String currentPassword;
    String newPassword;
    String confirmedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_change_password);
        getLayoutInflater().inflate(R.layout.activity_change_password, frameLayout);
        ButterKnife.inject(this);

        _changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });
    }

    public void changePass() {
        ProgressDia.showDialog(this);

        currentPassword = _currentpasswordText.getText().toString();
        newPassword = _newPasswordText.getText().toString();
        confirmedPassword = _confirmedPasswordText.getText().toString();

        if (!validate()) {
            onChangePasswordFailed(ErrorClass.accInvalid_err);
            return;
        }

        _changePassButton.setEnabled(false);

        changePasswordAction();

    }


    public void onChangePasswordSuccess() {
        ProgressDia.dismissDialog();
        setResult(RESULT_OK, null);
        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_LONG).show();
        onBackPressed();

    }

    public void onChangePasswordFailed(int err) {
        ProgressDia.dismissDialog();

        _currentpasswordText.setText("");
        _newPasswordText.setText("");
        _confirmedPasswordText.setText("");
        ErrorClass.showError(this, err);

        _changePassButton.setEnabled(true);

    }

    public boolean validate() {
        boolean valid = true;

        if (currentPassword.isEmpty() || currentPassword.length() < 6 || currentPassword.length() > 255) {
            _currentpasswordText.setError("between 6 and 255 alphanumeric characters");
            valid = false;
        } else {
            _currentpasswordText.setError(null);
        }

        if (newPassword.isEmpty() || newPassword.length() < 6 || newPassword.length() > 255) {
            _newPasswordText.setError("between 6 and 255 alphanumeric characters");
            valid = false;
        } else {
            _newPasswordText.setError(null);
        }

        if (newPassword.compareTo(currentPassword) == 0) {
            _newPasswordText.setError("new password must be different with current password");
            valid = false;
        } else {
            _newPasswordText.setError(null);
        }

        if (confirmedPassword.compareTo(newPassword) != 0) {
            _confirmedPasswordText.setError("These passwords don't match. Try again?");
            valid = false;
        } else {
            _newPasswordText.setError(null);
        }

        return valid;
    }

    void changePasswordAction() {
        SharedPreferences pref = this.getSharedPreferences("ATK_lec_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        JsonObject up = new JsonObject();
        up.addProperty("oldPassword", currentPassword);
        up.addProperty("newPassword", newPassword);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.changePassword(up);

        call.enqueue (new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();
                    if (messageCode == 200) {
                        onChangePasswordSuccess();
                    } else if (messageCode == 400) {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        if(errorCode == 1)
                            onChangePasswordFailed(ErrorClass.incorrectPass_err);
                        else if(errorCode == 8)
                            onChangePasswordFailed(ErrorClass.newPassInvalid_err);

                    } else {
                        onChangePasswordFailed(ErrorClass.unsuccessAttemp_err);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    onChangePasswordFailed(ErrorClass.wrongFormat_err);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onChangePasswordFailed(ErrorClass.internet_err);
            }
        });

    }

}
