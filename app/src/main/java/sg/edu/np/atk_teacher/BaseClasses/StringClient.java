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
import sg.edu.np.atk_teacher.UtilityClasses.ModifyStatusClass;

/**
 * Created by Lord One on 7/26/2016.
 */
public interface StringClient {



    @GET("user/logout")
    Call<ResponseBody> logout();

    @GET("timetable/current-semester")
    Call<ResponseBody> getListClasses(@Query("fromDate") String fromDate, @Query("classSection") String classSection);

    @GET("lesson/list-class-section-for-lecturer")
    Call<ResponseBody> getLecturerSubjectList();

    @POST("user/lecturer-login")
    Call<ResponseBody> login(@Body LoginClass up);

    @POST("attendance/modify-status")
    Call<ResponseBody> modify_status(@Body ModifyStatusClass up);
}
