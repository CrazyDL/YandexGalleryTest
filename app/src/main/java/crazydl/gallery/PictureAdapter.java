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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private static final int VISIBLE = 0;
    private static final int INVISIBLE = 1;
    private static final int HIDE = 2;

    private ArrayList<Picture> items;
    private ArrayList<Integer> itemsPositions;
    private ArrayList<Integer> itemsVisible;

    private int diffDatePosition = 0;

    PictureAdapter() {
        items = new ArrayList<>();
        itemsPositions = new ArrayList<>();
        itemsVisible = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_element, parent, false);
        ViewHolder h = new ViewHolder(view);
        view.setOnClickListener(it -> {
                int position = h.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    Intent intent = new Intent(view.getContext(), FullPictureActivity.class);
                    intent.putExtra("filePath", items.get(position).getFilePath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });
        return h;
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
            Picasso.with(holder.image.getContext())
                    .load("file:///" + item.getFilePath())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.download_refresh)
                    .error(R.drawable.error)
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

    public void addData(List<Picture> pictures){
        if (pictures == null || pictures.isEmpty()){
            return;
        }
        int from = items.isEmpty() ? 0 :  items.size() - 1;
        items.addAll(pictures);
        updatePositions(from);
    }

    private void updatePositions(int from){
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
    public void loadCashedData(){
        new AsyncTask<Void, Void, ArrayList<Picture>>(){
            @Override
            protected void onPreExecute() {
                clearData();
            }

            @Override
            protected void onPostExecute(ArrayList<Picture> pictures) {
                items = pictures;
                updatePositions(0);
            }

            @Override
            protected ArrayList<Picture> doInBackground(Void... voids) {
                return new ArrayList<>(Utils.getInstance().getAppDatabase().pictureDao().getAll());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearData(){
        int size = itemsPositions.size();
        items.clear();
        itemsPositions.clear();
        itemsVisible.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView date;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            image  = itemView.findViewById(R.id.image);
        }
    }
}
