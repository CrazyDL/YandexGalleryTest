package crazydl.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.disk.rest.json.Resource;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {
    private final String TAG = "DemoAdapter";

    private static final int VISIBLE = 0;
    private static final int INVISIBLE = 1;
    private static final int HIDE = 2;

    private ArrayList<Resource> items;
    private ArrayList<Integer> itemsPositions;
    private ArrayList<Integer> itemsVisible;

    private File cacheDir;
    private DateFormat df;

    DemoAdapter(File cacheDir) {
        items = new ArrayList<>();
        itemsPositions = new ArrayList<>();
        itemsVisible = new ArrayList<>();

        df = new SimpleDateFormat("d MMM");

        this.cacheDir = new File(cacheDir, "Images");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            this.cacheDir = cacheDir;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(itemsPositions.get(position) != -1){
            Resource item = items.get(itemsPositions.get(position));
            holder.date.setVisibility(View.VISIBLE);
            switch (itemsVisible.get(itemsPositions.get(position))){
                case VISIBLE:
                    holder.date.setText(df.format(item.getCreated()));
                    break;
                case INVISIBLE:
                    holder.date.setText("");
                    break;
                case HIDE:
                    holder.date.setVisibility(View.GONE);
                    break;
            }
            holder.image.setVisibility(View.VISIBLE);
            File imageFile = new File(cacheDir, item.getMd5());
            if (imageFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                holder.image.setImageBitmap(bitmap);
            }
            else {
                holder.image.setImageResource(R.drawable.download_error);
            }
        }
        else {
            holder.date.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemsPositions.size();
    }

    public void UpdateData(List<Resource> pictures){
        /*for(File file: cacheDir.listFiles()){
            Log.d(TAG, file.getAbsolutePath());
        }
        Log.d(TAG, String.valueOf(items.size()) + "  " + String.valueOf(pictures.size()));*/
        items.addAll(pictures);
        UpdatePositions();
    }

    public void UpdatePositions(){
        Log.d(TAG, String.valueOf(itemsPositions.size()) + "  " + String.valueOf(itemsVisible.size()));

        int itemsSize = items.size();
        if(itemsSize == 0)
            return;
        itemsPositions.clear();
        itemsVisible.clear();
        String currentDate = df.format(items.get(0).getCreated());
        int diffDatePosition = 0;
        itemsPositions.add(0);
        itemsVisible.add(VISIBLE);
        for (int i = 1; i < itemsSize; i++){
            Resource item = items.get(i);
            if(df.format(item.getCreated()).equals(currentDate)){
                if(i - diffDatePosition > 1)
                    itemsVisible.add(HIDE);
                else
                    itemsVisible.add(INVISIBLE);
            }
            else {
                itemsVisible.add(VISIBLE);
                currentDate = df.format(item.getCreated());
                diffDatePosition = i;
                if (itemsPositions.size() % 2 != 0) {
                    itemsPositions.add(-1);
                }
            }
            itemsPositions.add(i);
        }
        Log.d(TAG, "sss"+ String.valueOf(itemsPositions.size()) + "  " + String.valueOf(itemsVisible.size()));

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            date = itemView.findViewById(R.id.date);
            image  = itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}
