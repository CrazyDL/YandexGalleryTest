package crazydl.gallery.data;

import android.content.Context;

import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import crazydl.gallery.Utils;


public class YandexDiskApiMapper {
    private final int NO_DOWNLOAD_LIMIT = -1;
    private static final String PICTURE_DOWNLOAD_FOLDER = "Image";
    private int DOWNLOAD_LIMIT = 20;

    private RestClient mRestClient;
    private File mCacheDir;
    private Context mContext;

    public YandexDiskApiMapper() {
        mRestClient = Utils.getInstance().getRestClient();
        mContext = Utils.getInstance().getApplicationContext();
        mCacheDir = getPictureCacheDir();
    }

    private File getPictureCacheDir() {
        File cacheDir = new File(mContext.getCacheDir(), PICTURE_DOWNLOAD_FOLDER);
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            cacheDir = mContext.getCacheDir();
        }
        return cacheDir;
    }

    private void getPublicRes(String publicUrl, int offset, ArrayList<Resource> resources) {
        ResourcesArgs.Builder resBuilder = new ResourcesArgs.Builder()
                .setPublicKey(publicUrl)
                .setSort(ResourcesArgs.Sort.created)
                .setOffset(offset);
        if (DOWNLOAD_LIMIT != NO_DOWNLOAD_LIMIT) {
            resBuilder.setLimit(DOWNLOAD_LIMIT);
        }
        Resource resource = null;
        try {
            resource = mRestClient.listPublicResources(resBuilder.build());
        } catch (IOException | ServerIOException e) {
            e.printStackTrace();
        }
        if (resource != null) {
            parsePictureFromPublicRes(resource, resources);
            ResourceList rl = resource.getResourceList();
            if (rl != null && rl.getLimit() + rl.getOffset() < rl.getTotal()) {
                getPublicRes(publicUrl, rl.getLimit() + rl.getOffset(), resources);
            }
        }
    }

    private void parsePictureFromPublicRes(Resource resource, ArrayList<Resource> resources) {
        if (resource.getType().equals("dir")) {
            for (Resource res : resource.getResourceList().getItems()) {
                if (res.getType().equals("dir") && res.getPublicUrl() != null) {
                    getPublicRes(res.getPublicUrl(), 0, resources);
                } else if (res.getType().equals("file") && res.getMediaType().equals("image")) {
                    resources.add(res);
                }
            }
        } else if (resource.getMediaType().equals("image")) {
            resources.add(resource);
        }
    }

    public ArrayList<Resource> getPictureList(String publicKey) {
        ArrayList<Resource> resources = new ArrayList<>();
        getPublicRes(publicKey, 0, resources);
        Collections.sort(resources, (r1, r2) -> r2.getCreated().compareTo(r1.getCreated()));
        return resources;
    }

    public String downloadPictureInCache(Resource resource) {
        File cacheFile = new File(mCacheDir, resource.getMd5());
        if (!cacheFile.exists()) {
            try {
                mRestClient.downloadPublicResource(resource.getPublicKey(), resource.getPath().getPath(), cacheFile, null);
            } catch (IOException | ServerException e) {
                e.printStackTrace();
                return null;
            }
        }
        return cacheFile.getAbsolutePath();
    }
}
