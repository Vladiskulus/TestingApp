package com.vn.iambulance.testingapp.db;

import io.realm.*;

public class DBService {

    private Realm realm = Realm.getDefaultInstance();

    public <T extends RealmObject> RealmResults<T> getAll(Class<T> tClass) {
        return realm.where(tClass).findAllAsync();
    }

    public <T extends RealmObject> T getById(Integer id, Class<T> tClass) {
        return realm.where(tClass).equalTo("id", id).findFirst();
    }
}