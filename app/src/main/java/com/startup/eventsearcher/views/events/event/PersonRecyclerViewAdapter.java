package com.startup.eventsearcher.views.events.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.presenters.userData.StoragePhotoPresenter;
import com.startup.eventsearcher.utils.DateParser;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.views.profile.IStoragePhotoView;

import java.util.ArrayList;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Subscriber> subscribers = new ArrayList<>();

    public PersonRecyclerViewAdapter() {
    }

    public void setSubscribers(ArrayList<Subscriber> subscribers) {
        this.subscribers = subscribers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item_list, parent, false);

        return new PersonRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.loadPhotoFromStorage(subscribers.get(position).getUser().getUid());
        if (FirebaseAuthUserGetter.getUserFromFirebaseAuth().equals(subscribers.get(position).getUser())){
            holder.personCardView.setCardBackgroundColor(ContextCompat.getColor(holder.personView.getContext(), R.color.backgroundCurrentUser));
        }
        holder.personLogin.setText(subscribers.get(position).getUser().getLogin());
        holder.personArrivalTime.setText(DateParser.getDateFormatTime(subscribers.get(position).getExtraDate().getArrivalTime()));
        holder.personComment.setText(subscribers.get(position).getExtraDate().getComment());
    }

    @Override
    public int getItemCount() {
        return subscribers.size();
    }

    public void unSubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements IStoragePhotoView {
        public final View personView;
        public final TextView personLogin;
        public final TextView personArrivalTime;
        public final TextView personComment;
        public final CardView personCardView;
        public final ImageView personAvatar;

        public ViewHolder(View view) {
            super(view);
            personView = view;
            personLogin = view.findViewById(R.id.person_login);
            personArrivalTime = view.findViewById(R.id.person_arrival_time);
            personComment = view.findViewById(R.id.person_comment);
            personCardView = view.findViewById(R.id.person_card);
            personAvatar = view.findViewById(R.id.person_avatar);
        }

        private final StoragePhotoPresenter storagePhotoPresenter = new StoragePhotoPresenter(this);

        @Override
        public void onSavePhotoInStorage() {
            //
        }

        @Override
        public void onLoadPhotoFromStorage(Bitmap bitmap) {
            personAvatar.setImageBitmap(bitmap);
        }

        @Override
        public void onError(String message) {
            //
        }

        @Override
        public void showLoading(boolean show) {
            //
        }

        public void loadPhotoFromStorage(String uid) {
            storagePhotoPresenter.loadPhotoFromStorage(
                    uid,
                    BitmapFactory.decodeResource(itemView.getResources(), R.drawable.default_avatar));
        }
    }
}
