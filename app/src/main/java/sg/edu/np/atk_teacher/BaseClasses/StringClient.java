package sg.edu.np.atk_teacher.BaseClasses;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sg.edu.np.atk_teacher.UtilityClasses.LoginClass;

/**
 * Created by Lord One on 7/26/2016.
 */
public interface StringClient {



    @GET("user/logout")
    Call<ResponseBody> logout();

    @POST("user/login")
    Call<ResponseBody> login(@Body LoginClass up);
}
