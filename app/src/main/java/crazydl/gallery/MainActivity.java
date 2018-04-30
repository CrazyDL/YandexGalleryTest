package crazydl.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final int PERMISSION_REQUEST_INTERNET_CODE = 0;

    private RecyclerView recyclerView;
    private DemoAdapter demoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        demoAdapter = new DemoAdapter(getCacheDir());
        recyclerView.setAdapter(demoAdapter);

        //RefreshItems();
    }

    private boolean haveInternetPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestInternetPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                PERMISSION_REQUEST_INTERNET_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_INTERNET_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    RefreshItems();
                }
        }
    }

    @Override
    public void onRefresh() {
        if (haveInternetPermission()) {
            RefreshItems();
        } else {
            requestInternetPermission();
        }
    }

    private void RefreshItems(){
        new PictureDownloader(swipeRefreshLayout, demoAdapter, getCacheDir()).execute();
    }
}
