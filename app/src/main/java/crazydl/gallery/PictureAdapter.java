package crazydl.gallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private final String TAG = "PictureAdapter";

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
                    .load("file:///" + item.getFileName())
                    .centerCrop()
                    .placeholder(R.drawable.download_refresh)
                    .error(R.drawable.download_error)
                    .into(holder.image);
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

    public void AddData(List<Picture> pictures){
        int from = items.isEmpty() ? 0 :  items.size() - 1;
        items.addAll(pictures);
        UpdatePositions(from);
    }

    public void ClearData(){
        int size = itemsPositions.size();
        items.clear();
        itemsPositions.clear();
        itemsVisible.clear();
        notifyItemRangeRemoved(0, size);
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
