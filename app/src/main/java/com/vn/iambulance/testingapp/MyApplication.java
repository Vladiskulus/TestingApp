package com.vn.iambulance.testingapp;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private void initRealm(){
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
    }

    @Override
    public void onCreate() {
        initRealm();
        super.onCreate();
    }
}