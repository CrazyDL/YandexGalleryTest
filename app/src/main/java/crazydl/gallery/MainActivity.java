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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TASK_FRAGMENT_TAG = "taskFragment";

    private PictureAdapter pictureAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        if(!Utils.getInstance().isOnline()){
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            pictureAdapter.loadCashedData();
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            pictureAdapter.updateData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.deleteInvalidCacheData();
    }
}
