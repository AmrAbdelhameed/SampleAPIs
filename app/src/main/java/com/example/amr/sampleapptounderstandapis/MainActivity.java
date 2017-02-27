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
                String idd = mGridAdapter.getItem(position).getIDD();
                String nam = mGridAdapter.getItem(position).getName();
                String numb = mGridAdapter.getItem(position).getNumber();
                String age = mGridAdapter.getItem(position).getAge();

                Bundle b = new Bundle();

                b.putString("ab", idd);
                b.putString("abab", nam);
                b.putString("ababab",numb);
                b.putString("abababab",age);

                intent.putExtras(b);
                startActivity(intent);
            }

        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {

                Toast.makeText(MainActivity.this, mGridAdapter.getItem(index).getName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        //Start download
        new MoviesAsyncTask().execute();

    }

    public class MoviesAsyncTask extends AsyncTask<Void, Void, Boolean> {

        String appKey = "";
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
                    item.setIDD(post.optString("id"));
                    item.setName(post.getString("firstname"));
                    item.setNumber(post.getString("lastname"));
                    item.setAge(post.getString("age"));

                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected Boolean doInBackground(Void... strings) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            final String baseURL = "http://192.168.1.107/phpinandroid/showStudents.php";
            final String api_key = "api_key";

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int a = item.getItemId();
        if (a == R.id.item1) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}