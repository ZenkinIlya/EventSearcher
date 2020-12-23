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
import com.startup.eventsearcher.main.ui.events.model.ExtraDate;
import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.util.List;
import java.util.Map;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private final List<Map.Entry<Person, ExtraDate>> personExtraDateList;
    private Context context;
    private RecyclerView recyclerView;

    public PersonRecyclerViewAdapter(List<Map.Entry<Person, ExtraDate>> personExtraDateList, Context context, RecyclerView recyclerView) {
        this.personExtraDateList = personExtraDateList;
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
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        });

        return new PersonRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.personLogin.setText(personExtraDateList.get(position).getKey().getLogin());
        holder.personArrivalTime.setText(personExtraDateList.get(position).getValue().getArrivalTime());
        holder.personComment.setText(personExtraDateList.get(position).getValue().getComment());
    }

    @Override
    public int getItemCount() {
        return personExtraDateList.size();
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
