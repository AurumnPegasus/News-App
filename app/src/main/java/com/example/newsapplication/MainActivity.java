package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ListIterator;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String oldUrl = "Initialize";
    private String oldData = "empty";
    private ListView listNews;
    private String feedUrl = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms";
    public static final String STATE_URL = "feedUrl";
    private TextView selection;
    private String textForSelection = "Top Stories";
    public static final String LISTVIEW_STATE = "LISTVIEW_STATE";
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listNews = findViewById(R.id.xmlListView);
        selection = findViewById(R.id.tvTellTitle);
        selection.setText("");
        if(savedInstanceState != null)
        {
            feedUrl = savedInstanceState.getString(STATE_URL);
            textForSelection = gettextForSelection();
        }
        downloadFeed(feedUrl);
    }

    private void downloadFeed(String feedUrl)
    {
        DownloadData downloadData = new DownloadData();
        Log.d(TAG, "downloadFeed: the link is " + feedUrl);
        downloadData.execute(feedUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ki, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        mListState = listNews.onSaveInstanceState();
        outState.putParcelable(LISTVIEW_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListState = savedInstanceState.getParcelable(LISTVIEW_STATE);
    }

    private String gettextForSelection()
    {
        String URLData = feedUrl;
        if("https://timesofindia.indiatimes.com/rssfeedstopstories.cms".equalsIgnoreCase(URLData))
            return "Top Stories";
        else if("https://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms".equalsIgnoreCase(URLData))
            return "National News";
        else if("https://timesofindia.indiatimes.com/rssfeeds/296589292.cms".equalsIgnoreCase(URLData))
            return "International News";
        else if("https://timesofindia.indiatimes.com/rssfeeds/1898055.cms".equalsIgnoreCase(URLData))
            return "Business News";
        else if("https://timesofindia.indiatimes.com/rssfeeds/1081479906.cms".equalsIgnoreCase(URLData))
            return "Entertainment Section";
        else if("https://timesofindia.indiatimes.com/rssfeeds/4719148.cms".equalsIgnoreCase(URLData))
            return "Sports News";
        else if("https://timesofindia.indiatimes.com/rssfeeds/-2128672765.cms".equalsIgnoreCase(URLData))
            return "Science News";
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.mnuTopStories:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms";
                break;

            case R.id.mnuIndia:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms";
                break;

            case R.id.mnuWorld:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/296589292.cms";
                break;

            case R.id.mnuBusiness:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/1898055.cms";
                break;

            case R.id.mnuEntertainment:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/1081479906.cms";
                break;

            case R.id.mnuSports:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/4719148.cms";
                break;

            case R.id.mnuScience:
                feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/-2128672765.cms";
                break;

            case R.id.mnuRefresh:
                feedUrl = oldUrl;
                oldUrl = "empty";
                oldData = "empty";
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        textForSelection = gettextForSelection();
        downloadFeed(feedUrl);
        return true;
    }

    public class DownloadData extends AsyncTask<String, Void, String>
    {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute:  parameter is " + s);
            if("error".equalsIgnoreCase(s))
                s = oldData;
            else
                oldData = s;
            ParseNews parseNews = new ParseNews();
            parseNews.parse(s);
            FeedAdapter feedAdapter  = new FeedAdapter(MainActivity.this, R.layout.display_blueprint, parseNews.getNews());
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            listNews.setAdapter(feedAdapter);
            selection.setText(textForSelection);
            if(mListState != null)
            {
                listNews.onRestoreInstanceState(mListState);
                mListState = null;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with" + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null)
                Log.e(TAG, "doInBackground: error in downloading");
            return rssFeed;
        }

        private String downloadXML(String UrlPath)
        {
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url = new URL(UrlPath);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                if(oldUrl.equalsIgnoreCase(UrlPath))
                {
                    Log.d(TAG, "downloadXML: SKIP MAX");
                    return "error";
                }
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: response was" + response);
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                int charsRead;
                char[] inputBuffer = new char[500];

                while(true)
                {
                    charsRead = bufferedReader.read(inputBuffer);
                    if(charsRead<0)
                        break;
                    if(charsRead>0)
                    {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }

                bufferedReader.close();
                oldUrl = UrlPath;
                return xmlResult.toString();
            }
            catch (MalformedURLException e)
            {
                Log.d(TAG, "downloadXML: MalformedURlException " + e.getMessage());
            }
            catch(IOException e)
            {
                Log.d(TAG, "downloadXML: Input Output excpetion " + e.getMessage());
            }
            catch(SecurityException e)
            {
                Log.d(TAG, "downloadXML: Some random security exception " + e.getMessage());
            }
            return null;
        }
    }

}
