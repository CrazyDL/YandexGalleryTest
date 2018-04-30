package crazydl.gallery;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

public class App extends Application {
    public static App instance;

    private RestClient restClient;
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "database").build();
    }

    public static App getInstance(){
        return instance;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
