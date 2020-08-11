package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
public class ArticleFragment extends Fragment implements Serializable{

    public ArticleFragment() {
        // Required empty public constructor
    }

    public static final ArticleFragment newInstance(Article article){
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("article", article);
        articleFragment.setArguments(bdl);
        return articleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final Article article;

        if (savedInstanceState == null) {
            article = (Article) getArguments().getSerializable("article");
        }
        else {
            article = (Article) savedInstanceState.getSerializable("article");
        }

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        //locate
        TextView title = (TextView) view.findViewById(R.id.headline);
        TextView date= (TextView) view.findViewById(R.id.date);
        TextView author = (TextView) view.findViewById(R.id.author);
        final ImageButton image = (ImageButton) view.findViewById(R.id.imageButton);
        TextView content = (TextView) view.findViewById(R.id.text);
        TextView index= (TextView) view.findViewById(R.id.index);

        //set Content
        title.setText(article.getTitle());
        date.setText(article.getPublishedAt().split("T")[0]);
        author.setText(article.getAuthor());
        content.setText(article.getDescription());
        index.setText(""+article.getIndex()+" of "+article.getTotal());

        if (article.getUrlToImage() != null){
            Picasso picasso = new Picasso.Builder(view.getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl =
                            article.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(image);
                }
            }).build();

            picasso.load(article.getUrlToImage()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(image);
        } else {
            Picasso.get().load(article.getUrlToImage())
                    .error(R.drawable.brokenimage).
                    placeholder(R.drawable.missing).into(image);
        }

        final Intent i = new Intent((Intent.ACTION_VIEW));
        i.setData(Uri.parse(article.getUrl()));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("article", getArguments().getSerializable("article"));
    }


}

