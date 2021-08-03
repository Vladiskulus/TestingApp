package com.vn.iambulance.testingapp.activity;

import static com.vn.iambulance.testingapp.Constant.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vn.iambulance.testingapp.adapter.UsersAdapter;
import com.vn.iambulance.testingapp.databinding.ActivityMainBinding;
import com.vn.iambulance.testingapp.db.*;
import com.vn.iambulance.testingapp.model.*;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.*;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.*;

public class MainActivity extends AppCompatActivity {

    UsersAdapter adapter;
    GitHubApi api = ServiceGenerator.getRequestApi();
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    DBService dbService = new DBService();
    Realm realm;
    MyBroadcastReceiver receiver;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        receiver = new MyBroadcastReceiver();
        realm = Realm.getDefaultInstance();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                });
        initRecyclerView();
        displayLocalData();
        fetchData();
    }

    private void fetchData() {
        List<UserInfo> fetchedUsers = new ArrayList<>();
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .flatMap((Function<UserModel, ObservableSource<UserInfo>>) user -> getRepositoriesObservable(user.getUserInfo()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        fetchedUsers.add(userInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        displayFetchedData();
                    }

                    @Override
                    public void onComplete() {
                        persistFetchedData(userResponseToUser(fetchedUsers));
                        displayFetchedData();
                    }
                });
    }

    private ObservableSource<UserInfo> getRepositoriesObservable(final UserInfo userInfo) {
        return api.getRepositories(userInfo.getLogin())
                .map(userRepositories -> {
                    RealmList<UserRepository> repositories = new RealmList<>();
                    repositories.addAll(userRepositories);
                    userInfo.setRepositories(repositories);
                    return userInfo;
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<UserModel> getUsersObservable() {

        return api.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<List<UserInfo>, ObservableSource<UserModel>>) userInfos -> Observable.fromIterable(userResponseToUser(userInfos))
                        .subscribeOn(Schedulers.io()));

    }

    private ArrayList<UserModel> userResponseToUser(List<UserInfo> userInfos) {
        ArrayList<UserModel> userModels = new ArrayList<>();
        for (UserInfo user : userInfos) {
            userModels.add(new UserModel(user.getId(), user));
        }
        return userModels;
    }

    private void persistFetchedData(List<UserModel> userModels) {
        realm.beginTransaction();
        if (!realm.isEmpty()) {
            for (UserModel userModel : userModels) {
                UserModel currentUserModel = realm.where(UserModel.class).equalTo("id", userModel.getId()).findFirst();
                assert currentUserModel != null;
                userModel.setChangesCount(currentUserModel.getChangesCount());
            }
        }

        else {
            for (UserModel userModel : userModels) {
                userModel.setChangesCount(0);
            }
        }
        realm.copyToRealmOrUpdate(userModels);
        realm.commitTransaction();
        realm.close();
    }

    private void initRecyclerView() {
        adapter = new UsersAdapter(this, new ArrayList<>());
        binding.usersRecyclerview.setHasFixedSize(true);
        binding.usersRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRecyclerview.setAdapter(adapter);

    }

    private void displayLocalData() {
        List<UserModel> userModels = Realm.getDefaultInstance().copyFromRealm(dbService.getAll(UserModel.class));
        if (!userModels.isEmpty()) {
            adapter.setUsers(userModels);
            binding.progressCircular.setVisibility(View.GONE);
        }
    }

    private void displayFetchedData() {
        List<UserModel> userModels = Realm.getDefaultInstance().copyFromRealm(dbService.getAll(UserModel.class));
        if (!userModels.isEmpty()) {
            adapter.setUsers(userModels);
        }
        binding.progressCircular.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NAME);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
        unregisterReceiver(receiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            Integer userId = extras.getInt(USER_ID_MESSAGE);
            Integer changesCount = extras.getInt(CHANGES_COUNT_MESSAGE);
            updateUser(userId, changesCount);
            displayLocalData();
        }
    }

    public void updateUser(Integer userId, Integer changesCount) {
        Realm realm = Realm.getDefaultInstance();
        UserModel currentUserModel;
        try {
            RealmResults<UserModel> userModels = realm.where(UserModel.class).equalTo("id", userId).findAll();
            currentUserModel = userModels.get(0);
            realm.beginTransaction();
            currentUserModel.setChangesCount(changesCount);
            realm.commitTransaction();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }
}