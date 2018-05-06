package crazydl.gallery;


import android.os.AsyncTask;

import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PictureDownloaderTask extends AsyncTask<Void, ArrayList<Picture>, Boolean> {
    private final int DOWNLOAD_LIMIT = 3;

    private Boolean result;
    private File cacheDir;
    private RestClient restClient;
    private PictureDao pictureDao;
    private DateFormat df;
    private PictureListParser pictureListParser;
    private TaskFragment.TaskCallback taskCallback;
    private List<Picture> pictureList;

    PictureDownloaderTask() {
        Utils utils = Utils.getInstance();
        pictureListParser = new PictureListParser();
        restClient = utils.getRestClient();
        pictureDao = utils.getAppDatabase().pictureDao();
        cacheDir = utils.getPictureCacheDir();
        df = new SimpleDateFormat("d MMM", Locale.US);
        pictureList = new ArrayList<>();
        result = null;
    }

    public void setTaskCallback(TaskFragment.TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
        if (taskCallback != null){
            if(!pictureList.isEmpty()){
                taskCallback.onProgress(new ArrayList<>(pictureList));
                pictureList.clear();
            }
            if(result != null){
                taskCallback.onPostExecute();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if(taskCallback != null){
            taskCallback.onPreExecute();
        }
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(ArrayList<Picture>... values) {
        taskCallback.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean res) {
        if (taskCallback != null){
            if(!pictureList.isEmpty()){
                taskCallback.onProgress(new ArrayList<>(pictureList));
                pictureList.clear();
            }
            taskCallback.onPostExecute();
        }
        result = res;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ArrayList<Resource> pictures = pictureListParser.updatePicturesData(Utils.PUBLIC_FOLDER_URL);
        pictureDao.nukeTable();

        if(pictures.isEmpty()){
            return true;
        }
        Collections.sort(pictures, (resource, t1) -> t1.getCreated().compareTo(resource.getCreated()));
        int itemCount = pictures.size();
        for (int from = 0, to; from < itemCount; ) {
            to = from + DOWNLOAD_LIMIT > itemCount ? itemCount : from + DOWNLOAD_LIMIT;
            for (int i = from; i < to; i++) {
                if (isCancelled()) {
                    return false;
                }
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
                pictureList.add(new Picture(cacheFile.getAbsolutePath(), res.getName(), df.format(res.getCreated())));
            }
            pictureDao.insert(pictureList);
            if (taskCallback != null){
                publishProgress(new ArrayList<>(pictureList));
                pictureList.clear();
            }
            from = to;
        }
        return true;
    }
}
