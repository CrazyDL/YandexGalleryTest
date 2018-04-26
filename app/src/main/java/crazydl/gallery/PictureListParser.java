package crazydl.gallery;

import android.content.Context;
import android.util.Log;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictureListParser {
    private final String TAG = "PictureDownloader";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";
    private final int DOWNLOAD_LIMIT = 20;

    private RestClient restClient;

    private List<PictureItem> pictureItems;

    public PictureListParser() {
        restClient = new RestClient(new Credentials("YandexGalleryTestUser", null));
        pictureItems = new ArrayList<>();
    }

    public void GetPublicRes(String publicUrl, int offset){
        ResourcesArgs.Builder resBuilder = new ResourcesArgs.Builder()
                .setPublicKey(publicUrl)
                .setSort(ResourcesArgs.Sort.created)
                .setLimit(DOWNLOAD_LIMIT)
                .setOffset(offset);
        Resource resource;
        try {
            resource = restClient.listPublicResources(resBuilder.build());
        } catch (IOException e) {
            Log.d(TAG, "IOException");
            e.printStackTrace();
            return;
        } catch (ServerIOException e) {
            Log.d(TAG, "ServerIOException");
            e.printStackTrace();
            return;
        }
        ParsePublicRes(resource);
        ResourceList rl = resource.getResourceList();
        if (rl != null && rl.getLimit() + rl.getOffset() < rl.getTotal()){
            GetPublicRes(publicUrl, rl.getLimit() + rl.getOffset());
        }
    }

    private void ParsePublicRes(Resource resource){
        if (resource == null)
            return;
        if(resource.getType().equals("dir")){
            for (Resource res : resource.getResourceList().getItems()) {
                if(res.getType().equals("dir") && res.getPublicUrl() != null) {
                    GetPublicRes(res.getPublicUrl(), 0);
                }
                else if (res.getType().equals("file") && res.getMediaType().equals("image")){
                    ParsePublicRes(res);
                }
            }
        }
        else if (resource.getMediaType().equals("image")){
            //pictureItems.add(new PictureItem(res.getName(), res.getCreated(), res.getPreview(), res.getPublicKey(), res.getPath()));
        }
    }

    public void UpdatePicturesData(String publicKey){
        GetPublicRes(publicKey, 0);
    }
}