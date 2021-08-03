package com.vn.iambulance.testingapp.db;

import com.vn.iambulance.testingapp.Constant;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String BASE_URL = "https://api.github.com/";

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Auth(Constant.CLIENT_ID, Constant.CLIENT_SECRET))
            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static GitHubApi api = retrofit.create(GitHubApi.class);

    public static GitHubApi getRequestApi() {
        return api;
    }
}