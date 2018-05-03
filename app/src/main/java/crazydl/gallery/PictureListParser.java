package crazydl.gallery;

import android.util.Log;

import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.IOException;
import java.util.ArrayList;

public class PictureListParser {
    private final String TAG = "PictureListParser";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";
    private final int DOWNLOAD_LIMIT = 20;

    private RestClient restClient;
    private ArrayList<Resource> resources;

    PictureListParser() {
        restClient =  App.getInstance().getRestClient();
        resources = new ArrayList<>();
    }

    private void GetPublicRes(String publicUrl, int offset){
        ResourcesArgs.Builder resBuilder = new ResourcesArgs.Builder()
                .setPublicKey(publicUrl)
                .setSort(ResourcesArgs.Sort.created)
                .setLimit(DOWNLOAD_LIMIT)
                .setOffset(offset);
        Resource resource;
        try {
            resource = restClient.listPublicResources(resBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ServerIOException e) {
            e.printStackTrace();
            return;
        }
        if (resource == null)
            return;
        ParsePublicRes(resource);
        ResourceList rl = resource.getResourceList();
        if (rl != null && rl.getLimit() + rl.getOffset() < rl.getTotal()){
            GetPublicRes(publicUrl, rl.getLimit() + rl.getOffset());
        }
    }

    private void ParsePublicRes(Resource resource){
        if(resource.getType().equals("dir")){
            for (Resource res : resource.getResourceList().getItems()) {
                if(res.getType().equals("dir") && res.getPublicUrl() != null) {
                    GetPublicRes(res.getPublicUrl(),0);
                }
                else if (res.getType().equals("file") && res.getMediaType().equals("image")){
                    ParsePublicRes(res);
                }
            }
        }
        else if (resource.getMediaType().equals("image")){
            resources.add(resource);
        }
    }

    public ArrayList<Resource> UpdatePicturesData(String publicKey){
        resources.clear();
        GetPublicRes(publicKey, 0);
        return resources;
    }
}
