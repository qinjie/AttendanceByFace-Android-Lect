package sg.edu.np.atk_teacher.BaseClasses;

/**
 * Created by Lord One on 7/26/2016.
 */

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lord One on 7/26/2016.
 */
public class ErrorInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // before request
        Request request = chain.request();

        // execute request
        Response response = chain.proceed(request);


        // after request

        // inspect status codes of unsuccessful responses
        switch (response.code()){
            case 401:
                Log.e("TEST","Unauthorized error for: " +request.url());
                GF.logoutAction(GV.activity);
        }

        return response;
    }
}