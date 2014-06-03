package com.thehackerati.bobbyandroid;

/**
 * Created by Lake on 5/30/2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleStoryActivity extends Activity {

    // JSON node keys
    private static final String TAG_TITLE = "title";
    private static final String TAG_DATE = "date";
    private static final String TAG_TEASER = "teaser";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_story);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String title = in.getStringExtra(TAG_TITLE);
        String date = in.getStringExtra(TAG_DATE);
        String teaser = in.getStringExtra(TAG_TEASER);

        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.title_label);
        TextView lblEmail = (TextView) findViewById(R.id.date_label);
        TextView lblMobile = (TextView) findViewById(R.id.teaser_label);

        lblName.setText(title);
        lblEmail.setText(date);
        lblMobile.setText(teaser);
    }
}
