package com.example.android.moviehelpline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by chandrakala on 8/19/2016.
 */

public class MovieDetails extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_page);
        Intent intent = getIntent();
        String JsonString = intent.getStringExtra("JsonObject");
        try {
            JSONObject jsonObj = new JSONObject(JsonString);
            updateDetails(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String movieName;

    private void updateDetails(JSONObject jsonObj) throws JSONException, IOException {

        ImageView poster = (ImageView)findViewById(R.id.iv_moviePoster);
        TextView tv_release = (TextView)findViewById(R.id.tv_ReleaseDate);
        TextView tv_movieName = (TextView)findViewById(R.id.tv_movieTitle);
        TextView tv_rating = (TextView)findViewById(R.id.tv_rating);
        TextView tv_synopsis = (TextView)findViewById(R.id.tv_synopsis);
        String moviePoster = jsonObj.getString("poster_path");
        poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(MovieDetails.this).load("http://image.tmdb.org/t/p/w185"+moviePoster).into(poster);
        tv_movieName.setText(jsonObj.getString("original_title"));
        movieName = jsonObj.getString("original_title");
        tv_release.setText(jsonObj.getString("release_date"));
        String rating = jsonObj.getString("vote_average");
        tv_rating.setText(rating+"/10");
        tv_synopsis.setText(jsonObj.getString("overview"));

        String movieId = jsonObj.getString("id");

        movieTrailer movieTask = new movieTrailer();
        movieTask.execute(movieId);


    }

    private void trailerResponse(final Results output) {
        ImageButton ib_trailer1 = (ImageButton)findViewById(R.id.ib_trailer1);
        TextView tv_trailerName1 = (TextView)findViewById(R.id.tv_trailerName1);
        ImageButton ib_trailer2 = (ImageButton)findViewById(R.id.ib_trailer2);
        TextView tv_trailerName2 = (TextView)findViewById(R.id.tv_trailerName2);
        ImageButton ib_trailer3= (ImageButton)findViewById(R.id.ib_trailer3);
        TextView tv_trailerName3 = (TextView)findViewById(R.id.tv_trailerName3);
        final String[] keys = output.arr1;
        String[] names = output.arr2;
        int flag = 0;
        if(keys.length > 0){

            tv_trailerName1.setText(names[0]);
            ib_trailer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+ keys[0])));

                }
            });
            flag =1;

        }if(keys.length > 1){
            tv_trailerName2.setText(names[1]);
            ib_trailer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+ keys[1])));

                }
            });
            flag = 2;

        }if(keys.length > 2){
            tv_trailerName3.setText(names[2]);
            ib_trailer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+ keys[2])));

                }
            });
            flag = 3;
        }

        if (flag < 3) {
            ib_trailer3.setVisibility(View.GONE);
            tv_trailerName3.setVisibility(View.GONE);
        }
        if(flag < 2){
            ib_trailer2.setVisibility(View.GONE);
            tv_trailerName2.setVisibility(View.GONE);

        }if(flag < 1){
            ib_trailer1.setVisibility(View.GONE);
            tv_trailerName1.setVisibility(View.GONE);

        }

    }


    private class movieTrailer extends AsyncTask<String,Void, Results>{

        private ProgressDialog Dialog = new ProgressDialog(MovieDetails.this);

        @Override
        protected void onPreExecute()
        {
            Dialog.setMessage("Loading, Please Wait...");
            Dialog.show();
        }

        @Override
        protected Results doInBackground(String... strings) {

            final String baseUrl = "https://api.themoviedb.org/3/movie/";
            final String urlVid = "videos";
            final String api_key = "796afcf249eff66d25973c19d4be2546";

            final Uri builturi = Uri.parse(baseUrl).buildUpon()
                    .appendPath(strings[0])
                    .appendPath(urlVid)
                    .appendQueryParameter("api_key",api_key)
                    .build();
            final String urlRev = "reviews";

            final Uri builturiRev = Uri.parse(baseUrl).buildUpon()
                    .appendPath(strings[0])
                    .appendPath(urlRev)
                    .appendQueryParameter("api_key",api_key)
                    .build();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerJsonStr = null;
            String reviewJsonStr = null;
            URL url;
            InputStream inputStream=null;
            StringBuffer buffer=null;

            try {
                url = new URL(builturi.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJsonStr = buffer.toString();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            try {
                url = new URL(builturiRev.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
                reviewSet(reviewJsonStr);

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }
            try {
            return getMovieTrailerFromJson(trailerJsonStr);

        } catch (JSONException e) {
            Log.e("FetchMovieTask2", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
        @Override
        protected void onPostExecute(Results keys){

            trailerResponse(keys);
            Dialog.dismiss();   //for loading icon
        }
    }

    private void reviewSet(String reviewJsonStr) throws JSONException {

        JSONObject JSONString = new JSONObject(reviewJsonStr);
        final JSONArray ReviewArray = JSONString.getJSONArray("results");
        JSONObject reviewObj;
        TextView revButton = (TextView) findViewById(R.id.tv_reviewButton);

        if(ReviewArray.length()==0){
            revButton.setVisibility(View.GONE);
        }else {
        final String author[]=new String[ReviewArray.length()];
        final String review[]=new String[ReviewArray.length()];
            for(int i=0;i<ReviewArray.length();i++) {

                reviewObj = ReviewArray.getJSONObject(i);
                author[i] = reviewObj.getString("author");
                review[i] = reviewObj.getString("content");
            }
            revButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MovieDetails.this, ReviewActivity.class)
                            .putExtra("moviename",movieName)
                            .putExtra("author", author)
                            .putExtra("review", review)
                            .putExtra("arrayLength",ReviewArray.length());
                    startActivity(intent);
                }
            });
        }
    }


    private Results getMovieTrailerFromJson(String trailerJsonStr) throws JSONException {

        JSONObject JSONString = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = JSONString.getJSONArray("results");
        String[] result1 = new String[trailerArray.length()];
        String[] result2 = new String[trailerArray.length()];

        JSONObject movie;
        String trailerKey;
        String trailerName;

        for(int i=0;i<trailerArray.length();i++){
            movie = trailerArray.getJSONObject(i);
            trailerKey= movie.getString("key");
            trailerName= movie.getString("name");
            result1[i] = trailerKey;
            result2[i] = trailerName;
        }
        return new Results(result1,result2);
    }

    public class Results{
        private String[] arr1=null;
        private String[] arr2=null;
        public Results(String[] arr1, String[] arr2){
            this.arr1 = arr1;
            this.arr2 = arr2;
        }
    }
}
