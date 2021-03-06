package com.example.franc.newsapp.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.franc.newsapp.MainActivity;
import com.example.franc.newsapp.Models.Articles;
import com.example.franc.newsapp.NewsDetailActivity;
import com.example.franc.newsapp.R;
import com.example.franc.newsapp.Utils.OnItemOneClickListener;
import com.example.franc.newsapp.Utils.Utils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<Articles> articles;

    public NewsAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, null);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {
        Articles model = articles.get(position);

        RequestOptions options = new RequestOptions();
        options.placeholder(Utils.getRandomDrawbleColor());
        options.error(Utils.getRandomDrawbleColor());
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.txtTime.setText(" \u2022 " + Utils.DateToTimeFormat(model.getPublishedAt()));
        holder.txtTitle.setText(model.getTitle());
        holder.txtTitle.setText(model.getTitle());
        holder.txtAuthor.setText(model.getAuthor());
        holder.txtDescription.setText(model.getDescription());
        holder.txtPublishedAt.setText(Utils.DateFormat(model.getPublishedAt()));
        holder.txtSource.setText(model.getSource().getName());

        holder.setListener(new OnItemOneClickListener() {
            @Override
            public void onClick(View v, int position) {
                ImageView imageView = v.findViewById(R.id.img);
                Intent intent = new Intent(context, NewsDetailActivity.class);
                Articles article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());

                Pair<View, String> pair = Pair.create((View) imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context,pair);

                context.startActivity(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.startActivity(intent, optionsCompat.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTitle, txtDescription, txtAuthor, txtPublishedAt, txtSource, txtTime;
        public ImageView imageView;
        public ProgressBar progressBar;
        OnItemOneClickListener listener;

        public void setListener(OnItemOneClickListener listener) {
            this.listener = listener;
        }

        public NewsViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            txtAuthor = itemView.findViewById(R.id.author);
            txtPublishedAt = itemView.findViewById(R.id.publishedAt);
            txtSource = itemView.findViewById(R.id.source);
            txtTime = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageView = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
