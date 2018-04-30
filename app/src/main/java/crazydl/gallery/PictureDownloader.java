package crazydl.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PictureDownloader extends AsyncTask<Void, List<Picture>, Void> {
    private final String TAG = "PictureDownloader";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";
    private final int DOWNLOAD_LIMIT = 10;


    private File cacheDir;
    private RestClient restClient;
    private DemoAdapter demoAdapter;
    private PictureDao pictureDao;
    private DateFormat df;
    @SuppressLint("StaticFieldLeak")
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureListParser pictureListParser;

    public PictureDownloader(SwipeRefreshLayout swipeRefreshLayout, DemoAdapter demoAdapter, File cDir) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.demoAdapter = demoAdapter;
        cacheDir = new File(cDir, "Images");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            cacheDir = cDir;
        }
        pictureListParser = new PictureListParser();
        restClient = App.getInstance().getRestClient();
        AppDatabase db = App.getInstance().getAppDatabase();
        pictureDao = db.pictureDao();
        df = new SimpleDateFormat("d MMM", Locale.US);
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
    protected final void onProgressUpdate(List<Picture>... values) {
        demoAdapter.AddData(values[0]);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<Resource> pictures = pictureListParser.UpdatePicturesData("https://yadi.sk/d/pz7-XL9k3UY724");
        if(pictures.isEmpty()){
            return null;
        }
        Collections.sort(pictures, new Comparator<Resource>() {
            @Override
            public int compare(Resource resource, Resource t1) {
                return resource.getCreated().compareTo(t1.getCreated());
            }
        });
        int itemCount = pictures.size() / 3;
        List<Picture> pictureList = new ArrayList<>();
        demoAdapter.ClearData();
        for (int from = 0, to = 0; from < itemCount; ) {
            to = from + DOWNLOAD_LIMIT > itemCount ? itemCount : from + DOWNLOAD_LIMIT;
            pictureList.clear();
            for (int i = from; i < to; i++) {
                Resource res = pictures.get(i);
                File cacheFile = new File(cacheDir, res.getMd5());
                if (!cacheFile.exists()) {
                    try {
                        restClient.downloadPublicResource(res.getPublicKey(),
                                res.getPath().getPath(), cacheFile, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ServerException e) {
                        e.printStackTrace();
                    }
                }
                pictureList.add(new Picture(cacheFile.getAbsolutePath(), df.format(res.getCreated())));
            }
            pictureDao.insert(pictureList);
            publishProgress(new ArrayList<>(pictureList));
            from = to;
        }
        return null;
    }
}
