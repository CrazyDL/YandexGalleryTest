package crazydl.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.yandex.disk.rest.json.Resource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final int PERMISSION_REQUEST_INTERNET_CODE = 0;

    private RecyclerView recyclerView;
    private DemoAdapter demoAdapter;
    private ImageDownloader imageDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        demoAdapter = new DemoAdapter(createDemoItems());
        recyclerView.setAdapter(demoAdapter);

        imageDownloader = new ImageDownloader(swipeRefreshLayout);
        RefreshItems();
    }

    @NonNull
    private ArrayList<DemoItem> createDemoItems() {
        ArrayList<DemoItem> demoItems = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (i <= 1) {
                demoItems.add(new DemoItem("10:00 AM", R.drawable.test1));
            } else if (i <= 5) {
                demoItems.add(new DemoItem("Yesterday", R.drawable.test2));
            } else if (i <= 6) {
                demoItems.add(new DemoItem("Oct. 23", R.drawable.test3));
            } else if (i <= 8) {
                demoItems.add(new DemoItem("Oct. 21", R.drawable.test4));
            } else if (i <= 11) {
                demoItems.add(new DemoItem("Oct. 20", R.drawable.test5));
            } else {
                demoItems.add(new DemoItem("Oct. 16", R.drawable.test6));
            }
        }
        return demoItems;
    }

    private boolean haveInternetPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestInternetPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
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
        imageDownloader.execute();
    }

}
