package crazydl.gallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FullPictureActivity extends AppCompatActivity {
    private ArrayList<Picture> items;
    private PictureDao pictureDao;
    private int currentItem;

    private ImageButton closeFullImage;
    private TextView imageName;
    private ImageView fullImage;

    @SuppressLint({"StaticFieldLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_picture_layout);
        closeFullImage = findViewById(R.id.closeFullImage);
        imageName = findViewById(R.id.name);
        fullImage = findViewById(R.id.fullImage);

        closeFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fullImage.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            public void onSwipeTop() {
                finish();
            }
            public void onSwipeBottom() {
                finish();
            }
            public void onSwipeRight() {
                if(currentItem > 0){
                    currentItem--;
                    UpdateViews();
                }
            }
            public void onSwipeLeft() {
                if(currentItem < items.size() - 1){
                    currentItem++;
                    UpdateViews();
                }
            }

        });

        getSupportActionBar().hide();

        final String filepath = getIntent().getStringExtra("filePath");

        pictureDao = App.getInstance().getAppDatabase().pictureDao();
        new AsyncTask<Void, Void, ArrayList<Picture>>(){

            @Override
            protected ArrayList<Picture> doInBackground(Void... voids) {
                return new ArrayList<>(pictureDao.getAll());
            }

            @Override
            protected void onPostExecute(ArrayList<Picture> pictures) {
                if (pictures.isEmpty()){
                    return;
                }
                items = pictures;
                for (int i = 0; i < items.size(); i++){
                    if(items.get(i).getFilePath().equals(filepath)){
                        currentItem = i;
                        break;
                    }
                }
                UpdateViews();
            }
        }.execute();
    }

    private void UpdateViews(){
        imageName.setText(items.get(currentItem).getName());
        Bitmap bitmap = BitmapFactory.decodeFile(items.get(currentItem).getFilePath());
        if (bitmap != null) {
            fullImage.setImageBitmap(bitmap);
        }
        else {
            fullImage.setImageResource(R.drawable.reversed_error);
        }
    }
}
