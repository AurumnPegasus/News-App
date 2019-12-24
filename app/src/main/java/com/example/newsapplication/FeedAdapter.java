package com.example.newsapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> news;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> news) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.news = news;
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentNews = news.get(position);
        viewHolder.tvTitle.setText(currentNews.getTitle());
        viewHolder.tvDescription.setText(currentNews.getDescription());
        viewHolder.tvLink.setText(currentNews.getLinkToStory());
        return convertView;
    }

    private class ViewHolder
    {
        final TextView tvTitle,tvDescription, tvLink;
        ViewHolder(View v)
        {
            this.tvTitle = v.findViewById(R.id.tvTitle);
            this.tvDescription = v.findViewById(R.id.tvDescription);
            this.tvLink = v.findViewById(R.id.tvLink);
        }
    }

}
