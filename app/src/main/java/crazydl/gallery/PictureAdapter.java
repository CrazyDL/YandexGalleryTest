package crazydl.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private final String TAG = "PictureAdapter";
    private final int IMAGE_PATH_TAG = 0;

    private static final int VISIBLE = 0;
    private static final int INVISIBLE = 1;
    private static final int HIDE = 2;

    private ArrayList<Picture> items;
    private ArrayList<Integer> itemsPositions;
    private ArrayList<Integer> itemsVisible;

    private PictureDao pictureDao;

    private int diffDatePosition = 0;

    PictureAdapter() {
        items = new ArrayList<>();
        itemsPositions = new ArrayList<>();
        itemsVisible = new ArrayList<>();

        AppDatabase db = App.getInstance().getAppDatabase();
        pictureDao = db.pictureDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(itemsPositions.get(position) != -1){
            Picture item = items.get(itemsPositions.get(position));
            holder.date.setVisibility(View.VISIBLE);
            switch (itemsVisible.get(itemsPositions.get(position))){
                case VISIBLE:
                    holder.date.setText(item.getDate());
                    break;
                case INVISIBLE:
                    holder.date.setText("");
                    break;
                case HIDE:
                    holder.date.setVisibility(View.GONE);
                    break;
            }
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.image.getContext())
                    .load("file:///" + item.getFilePath())
                    .centerCrop()
                    .placeholder(R.drawable.download_refresh)
                    .error(R.drawable.error)
                    .into(holder.image);
            holder.path.setText(item.getFilePath());
        }
        else {
            holder.path.setText("empty");
            holder.date.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemsPositions.size();
    }

    public void AddData(List<Picture> pictures){
        int from = items.isEmpty() ? 0 :  items.size() - 1;
        items.addAll(pictures);
        UpdatePositions(from);
    }

    private void UpdatePositions(int from){
        int itemsSize = items.size();
        if(itemsSize == 0)
            return;
        int startPosition = itemsPositions.size();
        String currentDate = items.get(from).getDate();
        if(from == 0){
            diffDatePosition = 0;
            itemsPositions.add(0);
            itemsVisible.add(VISIBLE);
        }
        for (int i = from + 1; i < itemsSize; i++){
            Picture item = items.get(i);
            if(item.getDate().equals(currentDate)){
                if(i - diffDatePosition > 1)
                    itemsVisible.add(HIDE);
                else
                    itemsVisible.add(INVISIBLE);
            }
            else {
                itemsVisible.add(VISIBLE);
                currentDate = item.getDate();
                diffDatePosition = i;
                if (itemsPositions.size() % 2 != 0) {
                    itemsPositions.add(-1);
                }
            }
            itemsPositions.add(i);
        }
        notifyItemRangeInserted(startPosition, itemsPositions.size());
    }

    @SuppressLint("StaticFieldLeak")
    public void LoadCashedData(){
        new AsyncTask<Void, Void, ArrayList<Picture>>(){
            @Override
            protected void onPreExecute() {
                ClearData();
            }

            @Override
            protected void onPostExecute(ArrayList<Picture> pictures) {
                items = pictures;
                UpdatePositions(0);
            }

            @Override
            protected ArrayList<Picture> doInBackground(Void... voids) {
                return new ArrayList<>(pictureDao.getAll());
            }
        }.execute();
    }

    public void ClearData(){
        int size = itemsPositions.size();
        items.clear();
        itemsPositions.clear();
        itemsVisible.clear();
        notifyItemRangeRemoved(0, size);
    }

    @SuppressLint("StaticFieldLeak")
    public void DeleteInvalidCache(){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                File cache = App.getInstance().getPictureCacheDir();
                for(File file: cache.listFiles()){
                    if(pictureDao.getByFilePath(file.getAbsolutePath()) == null){
                        file.delete();
                    }
                }
                return null;
            }
        }.execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        TextView path;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            date = itemView.findViewById(R.id.date);
            image  = itemView.findViewById(R.id.image);
            path  = itemView.findViewById(R.id.path);
        }

        @Override
        public void onClick(View view) {
            if(path.getText().equals("empty")){
                return;
            }
            Intent intent = new Intent(view.getContext(), FullPictureActivity.class);
            intent.putExtra("filePath", path.getText());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }
    }
}
