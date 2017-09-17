package com.example.android.moviehelpline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.R.attr.author;
import static com.example.android.moviehelpline.R.id.ib_trailer1;
import static com.example.android.moviehelpline.R.id.ib_trailer2;
import static com.example.android.moviehelpline.R.id.ib_trailer3;
import static com.example.android.moviehelpline.R.id.text2;
import static com.example.android.moviehelpline.R.id.tv_trailerName1;
import static com.example.android.moviehelpline.R.id.tv_trailerName2;
import static com.example.android.moviehelpline.R.id.tv_trailerName3;

/**
 * Created by chandrakala on 8/21/2016.
 */

public class ReviewActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_page);

        Intent intent = getIntent();
        int length= intent.getIntExtra("arrayLength",1);
        String[] author = intent.getStringArrayExtra("author");
        String[] review = intent.getStringArrayExtra("review");
        String movieName = intent.getStringExtra("moviename");

        TextView tv_name=(TextView)findViewById(R.id.tv_reviewMovieName);
        TextView tv_auth=(TextView)findViewById(R.id.tv_reviewAuthor);
        TextView tv_rev=(TextView)findViewById(R.id.tv_review);
        TextView tv_auth2=(TextView)findViewById(R.id.tv_rev_author_2);
        TextView tv_rev2=(TextView)findViewById(R.id.tv_rev_content_2);
        TextView tv_auth3=(TextView)findViewById(R.id.tv_rev_author_3);
        TextView tv_rev3=(TextView)findViewById(R.id.tv_rev_content_3);
        TextView tv_revNo2=(TextView)findViewById(R.id.tv_rev_2);//no need for 1 as the activity wont be called if no review
        TextView tv_revNo3=(TextView)findViewById(R.id.tv_rev_3);

        tv_name.setText(movieName);
        int flag = 0;
        if(length > 0){

            tv_auth.setText(author[0]);
            tv_rev.setText(review[0]);
            flag =1;

        }if(length > 1){
            tv_auth2.setText(author[1]);
            tv_rev2.setText(review[1]);
            flag = 2;

        }if(length > 2){
            tv_auth3.setText(author[2]);
            tv_rev3.setText(review[2]);
            flag = 3;
        }

        if (flag < 3) {
            tv_auth3.setVisibility(View.GONE);
            tv_rev3.setVisibility(View.GONE);
            tv_revNo3.setVisibility(View.GONE);
        }
        if(flag < 2){
            tv_auth2.setVisibility(View.GONE);
            tv_rev2.setVisibility(View.GONE);
            tv_revNo2.setVisibility(View.GONE);
        }
    }
}
