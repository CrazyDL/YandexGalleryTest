package crazydl.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.util.ResourcePath;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageDownloader extends AsyncTask<Void, List<Resource>, Void> {
    private final String TAG = "ImageDownloader";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";
    private final int DOWNLOAD_LIMIT = 5;


    private File casheDir;
    private RestClient restClient;


    //private Context context;
    @SuppressLint("StaticFieldLeak")
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureListParser pictureListParser;

    public ImageDownloader(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        //this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        pictureListParser = new PictureListParser();
        restClient = Utils.getRestClientInstance();
        casheDir = new File(context.getCacheDir(), "Images");
        if (!casheDir.exists() && !casheDir.mkdir()) {
            casheDir = context.getCacheDir();
        }
        Log.d(TAG, casheDir.getAbsolutePath());
    }

    @Override
    protected void onPreExecute() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected final void onProgressUpdate(List<Resource>... values) {
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<Resource> pictures = pictureListParser.UpdatePicturesData("https://yadi.sk/d/pz7-XL9k3UY724");
        Collections.sort(pictures, new Comparator<Resource>() {
            @Override
            public int compare(Resource resource, Resource t1) {
                return resource.getCreated().compareTo(t1.getCreated());
            }
        });
        int itemCount = pictures.size() / 10;
        for (int i = 0; i < itemCount; ) {
            for (int j = 0; j < DOWNLOAD_LIMIT; j++, i++) {
                File cashFile = new File(casheDir, pictures.get(i).getMd5());
                if (!cashFile.exists()) {
                    try {
                        restClient.downloadPublicResource(pictures.get(i).getPublicKey(),
                                pictures.get(i).getPath().getPath(), cashFile, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ServerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
