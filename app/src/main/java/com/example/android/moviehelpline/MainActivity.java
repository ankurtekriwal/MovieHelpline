package com.example.android.moviehelpline;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

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
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {

    JSONArray moviesArray = new JSONArray();
    public interface AsyncResponse {
        void processFinish(String[] output);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateMovie();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail,menu);
        return true;
    }

    public void updateMovie(){
        final GridView gridview = (GridView) findViewById(R.id.gridView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
        FetchMovieTask movieTask = new FetchMovieTask(new AsyncResponse() {
            @Override
            public void processFinish(String[] output) {
                gridview.setAdapter(new ImageAdapter((MainActivity.this),output));
            }
        });
        movieTask.execute(sort_by);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            JSONObject movie2 = new JSONObject();
            public void onItemClick (AdapterView < ? > parent, View v,
                                     int position, long id){
                try {
                    movie2 = moviesArray.getJSONObject(position);
                    Intent intent = new Intent(MainActivity.this,MovieDetails.class)
                            .putExtra("JsonObject",movie2.toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_setting){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public String[] getMovieDataFromJson(String movieJsonStr) throws JSONException{

        JSONObject JSONString = new JSONObject(movieJsonStr);
        moviesArray = JSONString.getJSONArray("results");
        String[] result = new String[moviesArray.length()];
        JSONObject movie;
        String moviePath;

        for(int i=0;i<moviesArray.length();i++){
            movie = moviesArray.getJSONObject(i);
            moviePath= movie.getString("poster_path");
            result[i] = moviePath;
        }
        return result;
    }
    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public class ImageAdapter extends BaseAdapter {
        private String[] list;
        private Activity mContext;

        public ImageAdapter(MainActivity mainActivity,String[] list) {
            this.mContext=mainActivity;
            this.list=list;
        }

        public int getCount() {
            return list.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(700,520));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(0, 0, 0, 3);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(MainActivity.this).load("http://image.tmdb.org/t/p/w185"+list[position]).into(imageView);
            return imageView;
        }
    }

        public class FetchMovieTask extends AsyncTask<String,Void,String[]> { //last string in param denote the return type

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute()
            {
                Dialog.setMessage("Loading, Please Wait...");
                Dialog.show();
            }

        public AsyncResponse delegate = null;

        public FetchMovieTask(AsyncResponse delegate){
            this.delegate = delegate;
        }
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                    delegate.processFinish(result);
            }
            Dialog.dismiss();
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
            }

            String sort_by = params[0];

            HttpsURLConnection con = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                URL url;
                if(sort_by.equals("Top Rated")){
                    url = new URL("https://api.themoviedb.org/3/movie/top_rated?api_key=796afcf249eff66d25973c19d4be2546");
                }else {
                    url = new URL("https://api.themoviedb.org/3/movie/popular?api_key=796afcf249eff66d25973c19d4be2546");
                }
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                // Read the input stream into a String
                InputStream inputStream = con.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.

                }

                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


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
                movieJsonStr = buffer.toString();
                Log.v("FetchMovieTask", "Forecast JSON String:" + movieJsonStr);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
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
                    return getMovieDataFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e("FetchMovieTask2", e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

    }
}
