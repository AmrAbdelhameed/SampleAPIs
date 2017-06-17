package com.example.amr.sampleapptounderstandapis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.title_);
        TextView date = (TextView) findViewById(R.id.date_);
        ImageView img = (ImageView) findViewById(R.id.image_);

        Intent in = getIntent();
        Bundle b = in.getExtras();
        String a = b.getString("ab");
        String aa = b.getString("abab");
        String aaa = b.getString("ababab");

        title.setText(a);
        setTitle(a);
        date.setText(aa);
        Picasso.with(DetailActivity.this).load(aaa).into(img);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

