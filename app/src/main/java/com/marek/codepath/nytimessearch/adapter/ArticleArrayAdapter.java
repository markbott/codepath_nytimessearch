package com.marek.codepath.nytimessearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marek.codepath.nytimessearch.model.Article;
import com.marek.codepath.nytimessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by marek on 3/18/2017.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        ImageView img = (ImageView)convertView.findViewById(R.id.ivImage);
        TextView tv = (TextView)convertView.findViewById(R.id.tvTitle);

        img.setImageResource(0);
        tv.setText(article.getHeadline());

        String thumbnail = article.getThumbnail();
        if(!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(img);
        }

        return convertView;
    }
}
