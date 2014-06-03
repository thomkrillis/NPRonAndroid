package com.thehackerati.bobbyandroid;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get stories JSON
    private String url = "http://api.npr.org/query";

    static ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    // Queries for first 20 Science stories
    static {
        params.add(new BasicNameValuePair("apiKey", "")); //Insert your NPR API key here
        params.add(new BasicNameValuePair("numResults", "20"));
        params.add(new BasicNameValuePair("format", "json"));
        params.add(new BasicNameValuePair("id", "1007"));
        params.add(new BasicNameValuePair("requiredAssets", "text"));
    }

    // JSON Node names
    private static final String TAG_LIST = "list";
    private static final String TAG_STORIES = "story";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TEASER = "teaser";
    private static final String TAG_DATE = "storyDate";
    private static final String TAG_TEXT = "$text";

    // stories JSONArray
    JSONObject slist = null;
    JSONArray stories = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> storyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storyList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String date = ((TextView) view.findViewById(R.id.date))
                        .getText().toString();
                String description = ((TextView) view.findViewById(R.id.teaser))
                        .getText().toString();

                // Starting single story activity
                Intent in = new Intent(getApplicationContext(),
                        SingleStoryActivity.class);
                in.putExtra(TAG_TITLE, name);
                in.putExtra(TAG_DATE, date);
                in.putExtra(TAG_TEASER, description);
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetStories().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetStories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, params);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    slist = jsonObj.getJSONObject(TAG_LIST);
                    stories = slist.getJSONArray(TAG_STORIES);

                    // looping through All Stories
                    for (int i = 0; i < stories.length(); i++) {
                        JSONObject c = stories.getJSONObject(i);

                        String id = c.getString(TAG_ID);

                        // Title node is JSON Object
                        JSONObject title = c.getJSONObject(TAG_TITLE);
                        String title_text = title.getString(TAG_TEXT);

                        // Teaser node is JSON Object
                        JSONObject teaser = c.getJSONObject(TAG_TEASER);
                        String teaser_text = teaser.getString(TAG_TEXT);

                        // Date node is JSON Object
                        JSONObject date = c.getJSONObject(TAG_DATE);
                        String date_text = date.getString(TAG_TEXT);

                        // tmp hashmap for single story
                        HashMap<String, String> story = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        story.put(TAG_ID, id);
                        story.put(TAG_TITLE, title_text);
                        story.put(TAG_TEASER, teaser_text);
                        story.put(TAG_DATE, date_text);

                        // adding story to story list
                        storyList.add(story);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, storyList,
                    R.layout.list_item, new String[] { TAG_TITLE, TAG_TEASER,
                    TAG_DATE }, new int[] { R.id.title,
                    R.id.date, R.id.teaser });

            setListAdapter(adapter);
        }

    }

}