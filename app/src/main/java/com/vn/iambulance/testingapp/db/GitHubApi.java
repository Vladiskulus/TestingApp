package com.vn.iambulance.testingapp.db;

import com.vn.iambulance.testingapp.model.*;
import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.*;

public interface GitHubApi {

    @GET("users")
    Observable<List<UserInfo>> getAllUsers();

    @GET("users/{login}/repos")
    Observable<List<UserRepository>> getRepositories(@Path("login") String login);
}