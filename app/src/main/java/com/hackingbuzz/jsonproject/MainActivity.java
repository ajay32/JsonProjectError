package com.hackingbuzz.jsonproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hackingbuzz.jsonproject.models.MovieModel;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HttpURLConnection connection;
    URL url;
    BufferedReader reader;
    Button btn;
    TextView tv;
    private StringBuffer buffer;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listViewMovies);


        //    new JsonTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt");

        //    new JsonTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt");



    }


    public class JsonTask extends AsyncTask<String, String, List<MovieModel>> {

        @Override
        protected List<MovieModel> doInBackground(String... params) {


            try {
                url = new URL(params [0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                }

                String finalJSON= buffer.toString();

                JSONObject parentObj = new JSONObject(finalJSON);
                JSONArray parentArry = parentObj.getJSONArray("movies");

               List<MovieModel> movieModelList = new ArrayList<>();

                for(int i=0 ; i<parentArry.length(); i++) {
                    JSONObject finalObj = parentArry.getJSONObject(i);
                    MovieModel mv = new MovieModel();
                    mv.setMovie(finalObj.getString("movie"));
                    mv.setYear(finalObj.getInt("year"));
                    mv.setRating((float) finalObj.getDouble("rating"));
                    mv.setDirector(finalObj.getString("director"));
                    mv.setDuration(finalObj.getString("duration"));
                    mv.setTagline(finalObj.getString("tagline"));
                    mv.setImage(finalObj.getString("image"));
                    mv.setStory(finalObj.getString("story"));

                    List<MovieModel.Cast> castList = new ArrayList<>();

                    for(int j=0 ; j<finalObj.getJSONArray("cast").length(); j++) {
                        JSONObject castJSONObject = finalObj.getJSONArray("Cast").getJSONObject(j);

                        MovieModel.Cast cast = new MovieModel.Cast();
                        cast.setName(castJSONObject.getString("name"));
                        castList.add(cast);

                    }
                    mv.setCastList(castList);
                    movieModelList.add(mv);


                }

                return movieModelList;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);
            //TODO set the data to the List

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            new JsonTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}