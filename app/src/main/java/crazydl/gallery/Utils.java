package crazydl.gallery;

import android.Manifest;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

import java.io.File;

public class Utils extends Application {
    public static final int PERMISSION_REQUEST_INTERNET_CODE = 0;
    public static final int COLUMNS_COUNT = 2;
    public static final String PICTURE_DOWNLOAD_FOLDER = "Image";
    public static final String PUBLIC_FOLDER_URL = "https://yadi.sk/d/pz7-XL9k3UY724";

    public static Utils instance;
    private static RestClient restClient;

    private AppDatabase appDatabase;
    private PictureAdapter pictureAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "database").build();
        pictureAdapter = new PictureAdapter();
        pictureAdapter.loadCashedData();
    }

    public static Utils getInstance(){
        return instance;
    }

    public  RestClient getRestClient() {
        return restClient;
    }

    public  AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public PictureAdapter getPictureAdapter() {
        return pictureAdapter;
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

    public static void clearCashedData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                File cache = Utils.getInstance().getPictureCacheDir();
                for (File file : cache.listFiles()) {
                    file.delete();
                }
                return null;
            }
        };
    }

    public static void deleteInvalidCacheData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                PictureDao pictureDao = Utils.getInstance().getAppDatabase().pictureDao();
                File cache = Utils.getInstance().getPictureCacheDir();
                for (File file : cache.listFiles()) {
                    if (pictureDao.getByFilePath(file.getAbsolutePath()) == null) {
                        file.delete();
                    }
                }
                return null;
            }
        };
    }

    public boolean haveInternetPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;
    }

}
