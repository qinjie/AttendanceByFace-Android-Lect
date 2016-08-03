package sg.edu.np.atk_teacher.RequestClasses;

/**
 * Created by Champ on 30/07/2016.
 */
public class SignupClass {
    String username;
    String email;
    String password;
    int role;

    public SignupClass(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = 30;
    }
}
