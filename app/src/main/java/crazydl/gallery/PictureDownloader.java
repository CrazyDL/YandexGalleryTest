package crazydl.gallery;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;

import java.io.IOException;

public class PictureDownloader {
    private final String TAG = "PictureDownloader";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";

    private RestClient restClient;


    public PictureDownloader() {
        restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
    }

    public void GetPublicRes(String publicKey){
        ResourcesArgs.Builder resBuilder = new ResourcesArgs.Builder()
                .setPublicKey(publicKey)
                .setSort(ResourcesArgs.Sort.created);
        try {
            Resource resource = restClient.listPublicResources(resBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServerIOException e) {
            e.printStackTrace();
        }
    }
}
