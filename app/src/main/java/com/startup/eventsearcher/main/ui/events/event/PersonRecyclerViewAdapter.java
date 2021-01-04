package com.startup.eventsearcher.main.ui.events.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;

import java.util.ArrayList;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Subscriber> subscribers;
    private Context context;
    private RecyclerView recyclerView;

    public PersonRecyclerViewAdapter(ArrayList<Subscriber> subscribers, Context context, RecyclerView recyclerView) {
        this.subscribers = subscribers;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public PersonRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item_list, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Toast.makeText(context, "itemPosition = " + itemPosition, Toast.LENGTH_SHORT).show();
            }
        });

        return new PersonRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.personLogin.setText(subscribers.get(position).getPerson().getLogin());
        holder.personArrivalTime.setText(subscribers.get(position).getExtraDate().getArrivalTime());
        holder.personComment.setText(subscribers.get(position).getExtraDate().getComment());
    }

    @Override
    public int getItemCount() {
        return subscribers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View View;
        public final TextView personLogin;
        public final TextView personArrivalTime;
        public final TextView personComment;

        public ViewHolder(View view) {
            super(view);
            View = view;
            personLogin = view.findViewById(R.id.person_login);
            personArrivalTime = view.findViewById(R.id.person_arrival_time);
            personComment = view.findViewById(R.id.person_comment);
        }
    }
}
