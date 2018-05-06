package crazydl.gallery;

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
        restClient =  Utils.getInstance().getRestClient();
        resources = new ArrayList<>();
    }

    private void getPublicRes(String publicUrl, int offset){
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
        parsePublicRes(resource);
        ResourceList rl = resource.getResourceList();
        if (rl != null && rl.getLimit() + rl.getOffset() < rl.getTotal()){
            getPublicRes(publicUrl, rl.getLimit() + rl.getOffset());
        }
    }

    private void parsePublicRes(Resource resource){
        if(resource.getType().equals("dir")){
            for (Resource res : resource.getResourceList().getItems()) {
                if(res.getType().equals("dir") && res.getPublicUrl() != null) {
                    getPublicRes(res.getPublicUrl(),0);
                }
                else if (res.getType().equals("file") && res.getMediaType().equals("image")){
                    parsePublicRes(res);
                }
            }
        }
        else if (resource.getMediaType().equals("image")){
            resources.add(resource);
        }
    }

    public ArrayList<Resource> updatePicturesData(String publicKey){
        resources.clear();
        getPublicRes(publicKey, 0);
        return resources;
    }
}
