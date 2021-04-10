package com.startup.eventsearcher.main.ui.events.event;

import android.content.Context;
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
import com.startup.eventsearcher.authentication.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.utils.DateParser;

import java.util.ArrayList;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Subscriber> subscribers;
    private final Context context;
    private final RecyclerView recyclerView;

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

        view.setOnClickListener(view1 -> {
            int itemPosition = recyclerView.getChildLayoutPosition(view1);
            Toast.makeText(context, "itemPosition = " + itemPosition, Toast.LENGTH_SHORT).show();
        });

        return new PersonRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (FirebaseAuthUserGetter.getUserFromFirebaseAuth().equals(subscribers.get(position).getUser())){
            holder.personCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.backgroundCurrentUser));
        }
        holder.personLogin.setText(subscribers.get(position).getUser().getLogin());
        holder.personArrivalTime.setText(DateParser.getDateFormatTime(subscribers.get(position).getExtraDate().getArrivalTime()));
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
        public final CardView personCardView;

        public ViewHolder(View view) {
            super(view);
            View = view;
            personLogin = view.findViewById(R.id.person_login);
            personArrivalTime = view.findViewById(R.id.person_arrival_time);
            personComment = view.findViewById(R.id.person_comment);
            personCardView = view.findViewById(R.id.person_card);
        }
    }
}
