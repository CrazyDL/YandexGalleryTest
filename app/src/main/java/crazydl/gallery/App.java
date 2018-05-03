package crazydl.gallery;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

import java.io.File;

public class App extends Application {
    private final String PICTURE_DOWNLOAD_FOLDER = "Image";
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

    public File getPictureCacheDir() {
        File cacheDir = new File(getCacheDir(), PICTURE_DOWNLOAD_FOLDER);
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            cacheDir = getCacheDir();
        }
        return cacheDir;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else{
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    public  void ClearCashedData(){
        File cache = getPictureCacheDir();
        for(File file: cache.listFiles()){
            file.delete();
        }
    }
}
