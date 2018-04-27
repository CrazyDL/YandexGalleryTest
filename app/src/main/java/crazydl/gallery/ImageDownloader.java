package crazydl.gallery;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.json.Resource;

import java.util.List;

public class ImageDownloader extends AsyncTask<Void, List<Resource>, Void> {
    private final String TAG = "ImageDownloader";
    private final String GET_INFO_PUBLIC_RES = "https://cloud-api.yandex.net/v1/disk/public/resources";
    private final int DOWNLOAD_LIMIT = 5;

    private RestClient restClient;

    @SuppressLint("StaticFieldLeak")
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureListParser pictureListParser;

    public ImageDownloader(SwipeRefreshLayout sRL) {
        swipeRefreshLayout = sRL;
        pictureListParser = new PictureListParser();
        restClient = Utils.getRestClientInstance();
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
        return null;
    }
}
