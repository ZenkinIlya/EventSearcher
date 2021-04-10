package com.startup.eventsearcher.views.events.adapters;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.views.events.filter.FilterHandler;
import com.startup.eventsearcher.utils.animation.ResizeAnimation;

import java.util.ArrayList;
import java.util.List;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.ViewHolder> {

    private final List<String> listTags;
    private TagRecyclerViewListener tagRecyclerViewListener;
    private final ArrayList<String> arrayListActiveCategory;

    public TagRecyclerViewAdapter(List<String> items, TagRecyclerViewListener tagRecyclerViewListener) {
        this.listTags = items;
        this.tagRecyclerViewListener = tagRecyclerViewListener;
        arrayListActiveCategory = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_item_list, parent, false);
        return new TagRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tagText.setText(listTags.get(position));

        holder.tagCard.setOnClickListener(view -> {
            int startColor = ContextCompat.getColor(view.getContext(), R.color.white);
            int endColor = ContextCompat.getColor(view.getContext(), R.color.primaryDarkColor);
            int duration = 300;
            if (holder.tagCard.getCardBackgroundColor().getDefaultColor() == startColor) {
                ResizeAnimation resizeAnimation = new ResizeAnimation(holder.tagCard, 32, 0);
                resizeAnimation.setDuration(duration);
                holder.tagCard.startAnimation(resizeAnimation);
                startColorAnimation(startColor, endColor, duration, holder.tagCard);
                holder.tagText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
                animateSearchImage(holder.imageView, duration);

                arrayListActiveCategory.add(holder.tagText.getText().toString().toLowerCase());
                filterEventList();

            } else if (holder.tagCard.getCardBackgroundColor().getDefaultColor() == endColor){
                animateSearchImage(holder.imageView, duration);
                ResizeAnimation resizeAnimation = new ResizeAnimation(holder.tagCard, -32, 0);
                resizeAnimation.setDuration(duration);
                holder.tagCard.startAnimation(resizeAnimation);
                startColorAnimation(endColor, startColor, duration, holder.tagCard);
                holder.tagText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.greyText));

                arrayListActiveCategory.remove(holder.tagText.getText().toString().toLowerCase());
                filterEventList();
            }
        });
    }

    private void filterEventList() {
        FilterHandler.setArrayListCategory(arrayListActiveCategory);
        tagRecyclerViewListener.onFilter();
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    private void animateSearchImage(final ImageView imageView, int duration){
        final float alpha;
        if (imageView.getVisibility() == View.INVISIBLE){
            alpha = 1f;
            ViewCompat.animate(imageView).withStartAction(() -> imageView.setVisibility(View.VISIBLE))
                    .alpha(alpha)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(duration)
                    .start();
        }else {
            alpha = 0f;
            ViewCompat.animate(imageView).withEndAction(() -> imageView.setVisibility(View.INVISIBLE))
                    .alpha(alpha)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(duration)
                    .start();
        }
    }

    private void startColorAnimation(int startColor, int endColor, int duration, final CardView tagCard) {
        ValueAnimator anim;
        anim = ValueAnimator.ofArgb(startColor, endColor)
                .setDuration(duration);
        anim.addUpdateListener(valueAnimator -> {
            if (tagCard != null) {
                tagCard.setCardBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tagText;
        public final CardView tagCard;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            tagText = view.findViewById(R.id.tagCardText);
            tagCard = view.findViewById(R.id.tagCard);
            imageView = view.findViewById(R.id.tagImageSearch);
        }
    }
}
