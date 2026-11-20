package com.example.projectaethera.network;



import androidx.annotation.NonNull;



import com.example.projectaethera.BuildConfig;
import com.example.projectaethera.config.AppConfig;



import java.io.IOException;

import java.util.concurrent.TimeUnit;



import okhttp3.Interceptor;

import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.Response;

import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;



/**

 * Builds and holds a single Retrofit instance for the whole app.

 * Adds the API key header and timeout settings before creating ApiService.

 */

public class RetrofitClient {



    /** Cached Retrofit instance so we only build it once. */

    private static Retrofit retrofit = null;



    /**

     * Returns the shared ApiService used to call the backend.

     * Creates Retrofit on first call with Gson, timeouts, and API key header.

     *

     * @return the ApiService interface ready for network calls

     */

    public static ApiService getClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()

                .connectTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)

                .readTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)

                .writeTimeout(AppConfig.API_TIME_OUT, TimeUnit.SECONDS)



                .addInterceptor(new Interceptor() {

                    @NonNull

                    @Override

                    public Response intercept(@NonNull Chain chain) throws IOException {



                        Request original = chain.request();





                        Request request = original.newBuilder()

                                .header("x-api-key", BuildConfig.API_KEY)



                                .method(original.method(), original.body())





                                .build();

                        return chain.proceed(request);

                    }

                })

                .build();





        if (retrofit == null) {

            retrofit = new Retrofit.Builder()

                    .baseUrl(AppConfig.getBaseUrl())

                    .addConverterFactory(GsonConverterFactory.create())

                    .client(okHttpClient)

                    .build();

        }

        return retrofit.create(ApiService.class);

    }

}

