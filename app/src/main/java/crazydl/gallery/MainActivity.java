package crazydl.gallery;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DemoAdapter demoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        demoAdapter = new DemoAdapter(createDemoItems());
        recyclerView.setAdapter(demoAdapter);


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
}
