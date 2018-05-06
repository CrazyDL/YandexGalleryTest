package crazydl.gallery;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FullPictureActivity extends AppCompatActivity {
    private final String SAVED_CURRENT_POSITION = "savedCurrentPosition";
    private final int INVALID_POSITION = -1;
    private static ArrayList<Picture> items;
    private int currentPosition;

    private TextView imageName;
    private ImageView fullImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_picture_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageButton closeFullImage = findViewById(R.id.closeFullImage);
        imageName = findViewById(R.id.name);
        fullImage = findViewById(R.id.fullImage);

        closeFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fullImage.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
                finish();
            }

            public void onSwipeBottom() {
                finish();
            }

            public void onSwipeRight() {
                if (currentPosition > 0) {
                    currentPosition--;
                    updateViews();
                }
            }

            public void onSwipeLeft() {
                if (currentPosition < items.size() - 1) {
                    currentPosition++;
                    updateViews();
                }
            }

        });

        currentPosition = savedInstanceState == null ? INVALID_POSITION : savedInstanceState.getInt(SAVED_CURRENT_POSITION);
        new LoadDataFromDbTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void findCurrentPicture(ArrayList<Picture> pictures) {
        items = pictures;
        if (currentPosition == INVALID_POSITION) {
            String filepath = getIntent().getStringExtra("filePath");
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getFilePath().equals(filepath)) {
                    currentPosition = i;
                    break;
                }
            }
        }
        if (!items.isEmpty()) {
            updateViews();
        }
    }

    private void updateViews() {
        imageName.setText(items.get(currentPosition).getName());
        //fullImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(getApplicationContext())
                .load("file:///" + items.get(currentPosition).getFilePath())
                .fit()
                .centerInside()
                .placeholder(R.drawable.download_refresh)
                .error(R.drawable.error)
                .into(fullImage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_CURRENT_POSITION, currentPosition);
    }

    static class LoadDataFromDbTask extends AsyncTask<Void, Void, ArrayList<Picture>> {
        private WeakReference<FullPictureActivity> activityWeakReference;

        LoadDataFromDbTask(FullPictureActivity fullPictureActivity) {
            activityWeakReference = new WeakReference<>(fullPictureActivity);
        }

        @Override
        protected ArrayList<Picture> doInBackground(Void... voids) {
            return new ArrayList<>(Utils.getInstance().getAppDatabase().pictureDao().getAll());
        }

        @Override
        protected void onPostExecute(ArrayList<Picture> pictures) {
            activityWeakReference.get().findCurrentPicture(pictures);
        }
    }
}
