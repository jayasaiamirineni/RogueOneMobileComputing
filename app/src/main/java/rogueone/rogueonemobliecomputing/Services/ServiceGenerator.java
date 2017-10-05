package rogueone.rogueonemobliecomputing.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rogueone.rogueonemobliecomputing.BuildConfig;

/**
 * Created by jayas on 5/09/2017.
 */

public class ServiceGenerator {
    private static final String BASE_URL = "http://rogueonemobilecomputing.azurewebsites.net/";
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static <S> S createService(Class<S> serviceClass,Context context) {
        return createService(serviceClass, null,context);
    }
    public static <S> S createService(Class<S> serviceClass,final String authToken,Context context){
        if(!TextUtils.isEmpty(authToken)||authToken!=null){

            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if(!httpClient.interceptors().contains(interceptor)){
                httpClient.addInterceptor(interceptor);
            }
            SharedPreferences preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID,context.MODE_PRIVATE);
            String refreshToken = preferences.getString("refresh_token",null);
            httpClient.authenticator(new RefreshTokenAuthenticator(httpClient,refreshToken,context));
        }

        if(!httpClient.interceptors().contains(logging)){

            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }
}