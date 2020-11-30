package com.startup.eventsearcher.main.ui.events;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.EventContent;

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
        holder.btnTag.setText(listTags.get(position));
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final Button btnTag;

        public ViewHolder(View view) {
            super(view);
            btnTag = (Button) view.findViewById(R.id.btn_tag);

            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((ColorDrawable) btnTag.getBackground()).getColor() == ContextCompat.getColor(view.getContext(), R.color.primaryLightColor)){
                        btnTag.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primaryDarkColor));
                    }else {
                        btnTag.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primaryLightColor));
                    }
                    Toast.makeText(view.getContext(), "ff", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
