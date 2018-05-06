package crazydl.gallery;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, TaskFragment.TaskCallback {
    private static final String TASK_FRAGMENT_TAG = "taskFragment";

    private PictureAdapter pictureAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TaskFragment taskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Utils.COLUMNS_COUNT));
        pictureAdapter = Utils.getInstance().getPictureAdapter();
        recyclerView.setAdapter(pictureAdapter);

        FragmentManager fm = getFragmentManager();
        taskFragment = (TaskFragment) fm.findFragmentByTag(TASK_FRAGMENT_TAG);

        if(taskFragment == null){
            taskFragment = new TaskFragment();
            fm.beginTransaction().add(taskFragment, TASK_FRAGMENT_TAG).commit();
        }
        if(taskFragment.isWorking()){
            swipeRefreshLayout.setRefreshing(true);
            taskFragment.continueTask();
        }
    }

    private void requestInternetPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET},
                Utils.PERMISSION_REQUEST_INTERNET_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Utils.PERMISSION_REQUEST_INTERNET_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    refreshItems();
                }
        }
    }

    @Override
    public void onRefresh() {
        if (Utils.getInstance().haveInternetPermission()) {
            refreshItems();
        } else {
            requestInternetPermission();
        }
    }

    private void refreshItems(){
        if(Utils.getInstance().isOnline()){
            taskFragment.executeTask();
        }
        else{
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            pictureAdapter.loadCashedData();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPreExecute() {
        swipeRefreshLayout.setRefreshing(true);
        pictureAdapter.clearData();
    }

    @Override
    public void onProgress(ArrayList<Picture> pictures) {
        pictureAdapter.addData(pictures);
    }

    @Override
    public void onPostExecute() {
        swipeRefreshLayout.setRefreshing(false);
        taskFragment.finishTask();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.deleteInvalidCacheData();
    }
}
