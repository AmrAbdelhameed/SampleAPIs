package com.example.amr.sampleapptounderstandapis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<MainGridItem> mGridData;
    private MainGridViewAdapter mGridAdapter;
    private ProgressDialog dialog;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
        String choose = sharedPreferences.getString("choose", "home");

        setTitle(choose);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        lv = (ListView) findViewById(R.id.listView1);

        mGridData = new ArrayList<>();
        mGridAdapter = new MainGridViewAdapter(MainActivity.this, R.layout.grid_item_layout, mGridData);
        lv.setAdapter(mGridAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String title = mGridAdapter.getItem(position).getTitle();
                String imageurl = mGridAdapter.getItem(position).getImageURL();
                String published_date = mGridAdapter.getItem(position).getPublished_date();

                Bundle b = new Bundle();

                b.putString("title", title);
                b.putString("imageurl", imageurl);
                b.putString("published_date", published_date);

                intent.putExtras(b);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

        });

        //Start download
        new MoviesAsyncTask().execute();

    }

    public class MoviesAsyncTask extends AsyncTask<Void, Void, Boolean> {

        String movieJson;

        private final String LOG_TAG = MoviesAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }


        private boolean parseResult(String result) {
            if (result == null)
                return false;
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("results");
                MainGridItem item;
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    item = new MainGridItem();

                    JSONArray imgs = post.getJSONArray("multimedia");
                    JSONObject imgobj = imgs.getJSONObject(imgs.length() - 1);
                    String img = imgobj.optString("url");

                    item.setTitle(post.optString("title"));
                    item.setImageURL(img);
                    item.setPublished_date(post.getString("published_date"));

                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected Boolean doInBackground(Void... strings) {

            SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
            String choose = sharedPreferences.getString("choose", "home");

            String baseURL = "http://api.nytimes.com/svc/topstories/v2/" + choose + ".json?api_key=b8e44f592a524d3db24fcb3636f874e5";

            Uri built = Uri.parse(baseURL).buildUpon().build();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(built.toString());
                Log.v(LOG_TAG, "built uri " + built.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return false;
                }
                movieJson = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + movieJson);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            return parseResult(movieJson);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
            if (aBoolean) {
                SharedPreferences.Editor e = MainActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                e.putString("json", movieJson).commit();
                MainGridViewAdapter adapter = new MainGridViewAdapter(MainActivity.this, R.layout.grid_item_layout, mGridData);
                lv.setAdapter(adapter);
            } else {
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_LONG).show();
                SharedPreferences e = MainActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                movieJson = e.getString("json", null);
                if (parseResult(movieJson)) {
                    MainGridViewAdapter adapter = new MainGridViewAdapter(MainActivity.this, R.layout.grid_item_layout, mGridData);
                    lv.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_section, menu);
        return true;
    }

    public void ChangeStringforURL(String s) {

        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("choose", s);
        editor.commit();

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home1) {
            ChangeStringforURL("home");
            return true;
        }

        if (id == R.id.arts) {
            ChangeStringforURL("arts");
            return true;
        }

        if (id == R.id.automobiles) {
            ChangeStringforURL("automobiles");
            return true;
        }

        if (id == R.id.books) {
            ChangeStringforURL("books");
            return true;
        }

        if (id == R.id.business) {
            ChangeStringforURL("business");
            return true;
        }

        if (id == R.id.fashion) {
            ChangeStringforURL("fashion");
            return true;
        }

        if (id == R.id.food) {
            ChangeStringforURL("food");
            return true;
        }

        if (id == R.id.health) {
            ChangeStringforURL("health");
            return true;
        }

        if (id == R.id.insider) {
            ChangeStringforURL("insider");
            return true;
        }

        if (id == R.id.magazine) {
            ChangeStringforURL("magazine");
            return true;
        }

        if (id == R.id.movies) {
            ChangeStringforURL("movies");
            return true;
        }

        if (id == R.id.national) {
            ChangeStringforURL("national");
            return true;
        }

        if (id == R.id.nyregion) {
            ChangeStringforURL("nyregion");
            return true;
        }

        if (id == R.id.obituaries) {
            ChangeStringforURL("obituaries");
            return true;
        }

        if (id == R.id.opinion) {
            ChangeStringforURL("opinion");
            return true;
        }

        if (id == R.id.politics) {
            ChangeStringforURL("politics");
            return true;
        }

        if (id == R.id.realestate) {
            ChangeStringforURL("realestate");
            return true;
        }

        if (id == R.id.science) {
            ChangeStringforURL("science");
            return true;
        }

        if (id == R.id.sports) {
            ChangeStringforURL("sports");
            return true;
        }

        if (id == R.id.sundayreview) {
            ChangeStringforURL("sundayreview");
            return true;
        }

        if (id == R.id.technology) {
            ChangeStringforURL("technology");
            return true;
        }

        if (id == R.id.theater) {
            ChangeStringforURL("theater");
            return true;
        }

        if (id == R.id.tmagazine) {
            ChangeStringforURL("tmagazine");
            return true;
        }

        if (id == R.id.travel) {
            ChangeStringforURL("travel");
            return true;
        }

        if (id == R.id.upshot) {
            ChangeStringforURL("upshot");
            return true;
        }

        if (id == R.id.world) {
            ChangeStringforURL("world");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}