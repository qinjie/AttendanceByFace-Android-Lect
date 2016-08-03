package sg.edu.np.atk_teacher.RequestClasses;

/**
 * Created by Lord One on 7/26/2016.
 */
public class LoginClass {
    String username = "NULL";
    String password = "NULL";
    String device_hash;
    public LoginClass(String _username, String _password) {
        username = _username;
        password = _password;
        device_hash = "0";
    }
}

