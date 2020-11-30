package com.startup.eventsearcher.main.ui.events;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;

import java.util.List;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.ViewHolder> {

    private final List<String> listTags;

    public TagRecyclerViewAdapter(List<String> items) {
        listTags = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_item_list, parent, false);
        return new TagRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tagText.setText(listTags.get(position));
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tagText;
        public final CardView tagCard;

        public ViewHolder(View view) {
            super(view);
            tagText = view.findViewById(R.id.tagCardText);
            tagCard = view.findViewById(R.id.tagCard);

            tagCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tagCard.getCardBackgroundColor().getDefaultColor() == ContextCompat.getColor(view.getContext(), R.color.primaryLightColor)){
                        tagCard.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primaryDarkColor));
                    }else {
                        tagCard.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primaryLightColor));
                    }
                    Toast.makeText(view.getContext(), "press tag", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
