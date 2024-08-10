package com.kasunjay.miigras_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.kasunjay.miigras_app.Domain.NewsDomain;
import com.kasunjay.miigras_app.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    ArrayList<NewsDomain> newsList;
    Context context;

    public NewsAdapter(ArrayList<NewsDomain> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_news_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        holder.title.setText(newsList.get(position).getTitle());
        holder.subtitle.setText(newsList.get(position).getSubTitle());

        int imageId = holder.itemView.getResources().getIdentifier(newsList.get(position).getPicAddress(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(imageId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, subtitle;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            subtitle = itemView.findViewById(R.id.subTitleText);
            image = itemView.findViewById(R.id.news_image);
        }
    }
}
