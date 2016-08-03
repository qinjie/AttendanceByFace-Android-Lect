package sg.edu.np.atk_teacher.BaseClasses;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sg.edu.np.atk_teacher.RequestClasses.LoginClass;
import sg.edu.np.atk_teacher.RequestClasses.ModifyStatusClass;
import sg.edu.np.atk_teacher.RequestClasses.ResetPassClass;
import sg.edu.np.atk_teacher.RequestClasses.SignupClass;

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

    @GET("timetable/list-student-for-lesson")
    Call<ResponseBody> getStudentList(@Query("lessonId") String lessonId, @Query("date") String date);

    @GET("timetable/current-week")
    Call<ResponseBody> getTimetableThisWeek();

    @POST("user/lecturer-login")
    Call<ResponseBody> login(@Body LoginClass up);

    @POST("attendance/modify-status")
    Call<ResponseBody> modify_status(@Body ModifyStatusClass up);

    @POST("user/change-password")
    Call<ResponseBody> changePassword(@Body JsonObject up);

    @POST("user/signup-lecturer")
    Call<ResponseBody> signup(@Body SignupClass up);

    @POST("user/reset-password")
    Call<ResponseBody> postResetPassword(@Body ResetPassClass up);

    @POST("user/allow-train-face")
    Call<ResponseBody> allowTrainFace(@Body JsonObject up);

    @POST("user/disallow-train-face")
    Call<ResponseBody> disallowTrainFace(@Body JsonObject up);

}
