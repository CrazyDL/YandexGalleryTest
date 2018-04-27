package crazydl.gallery;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

public class Utils {
    private static RestClient restClient;

    public static RestClient getRestClientInstance() {
        if (restClient == null){
            restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
        }
        return restClient;
    }
}
