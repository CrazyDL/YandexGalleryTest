package crazydl.gallery;

import android.app.Application;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

public class App extends Application {
    public static App instance;

    private RestClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
        restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
    }

    public static App getInstance(){
        return instance;
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
