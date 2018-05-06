package crazydl.gallery;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;


import java.util.ArrayList;

public class TaskFragment extends Fragment {
    private TaskCallback taskCallback;
    private PictureDownloaderTask pictureDownloaderTask;

    public interface TaskCallback{
        void onPreExecute();
        void onProgress(ArrayList<Picture> pictures);
        void onPostExecute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        taskCallback = (TaskCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        taskCallback = null;
        if(pictureDownloaderTask != null){
            pictureDownloaderTask.setTaskCallback(null);
        }
    }

    public void continueTask(){
        if(pictureDownloaderTask != null){
            pictureDownloaderTask.setTaskCallback(taskCallback);
        }
    }

    public void executeTask(){
        pictureDownloaderTask = new PictureDownloaderTask();
        pictureDownloaderTask.setTaskCallback(taskCallback);
        pictureDownloaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isWorking(){
        return pictureDownloaderTask != null;
    }

    public void finishTask(){
        if (pictureDownloaderTask != null){
            pictureDownloaderTask.cancel(true);
        }
        pictureDownloaderTask = null;
    }
}
