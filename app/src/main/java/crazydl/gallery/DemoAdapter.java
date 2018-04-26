package crazydl.gallery;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {
    private final ArrayList<DemoItem> items;
    private ArrayList<Integer> itemsPositions;

    DemoAdapter(ArrayList<DemoItem> items) {
        this.items = new ArrayList<>(items);
        itemsPositions = new ArrayList<>();
        int itemsSize = items.size();
        if(itemsSize == 0)
            return;
        String currentDate = items.get(0).getDate();
        int diffDatePosition = 0;
        itemsPositions.add(0);
        for (int i = 1; i < itemsSize; i++){
            DemoItem item = items.get(i);
            if(item.getDate().equals(currentDate)){
                if(i - diffDatePosition > 1)
                    item.setDateStatus(DemoItem.HIDE);
                else
                    item.setDateStatus(DemoItem.INVISIBLE);
            }
            else {
                currentDate = item.getDate();
                diffDatePosition = i;
                if (itemsPositions.size() % 2 != 0) {
                    itemsPositions.add(-1);
                }
            }
            itemsPositions.add(i);
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
            DemoItem item = items.get(itemsPositions.get(position));
            holder.date.setVisibility(View.VISIBLE);
            switch (item.getDateStatus()){
                case DemoItem.VISIBLE:
                    holder.date.setText(item.getDate());
                    break;
                case DemoItem.INVISIBLE:
                    holder.date.setText("");
                    break;
                case DemoItem.HIDE:
                    holder.date.setVisibility(View.GONE);
                    break;
            }
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageResource(item.getImageId());
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        ImageView image;

        public ViewHolder(View itemView) {
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
