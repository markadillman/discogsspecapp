package markdillman.discogsspecapp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.net.Uri;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.RatingBar.*;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.annotation.*;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

/**
 * Created by markdillman on 3/19/17.
 */

public class AlbumActivity extends AppCompatActivity {
    private Button mButton;
    private UsersDB mUsersDB;
    private SQLiteDatabase mSQLDB;
    private String token;
    private String tokenSecret;
    private String url;
    private int id;
    private int localId;
    private static final String CALLBACK_URL = "http://convectionmeadows.com";
    private String consumerKey = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String userAgent = "Crate-a-logue 1.0";
    private String requestUrl = "https://www.discogs.com/oauth/authorize";
    private String username;
    private OAuth10aService mService;
    private OAuth1AccessToken mAccessToken;
    private final String TAG = "AlbumActivity";
    private String instanceUrl;
    private int folder_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        //unpack bundle
        Bundle args = getIntent().getExtras();
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.equals("token_secret")) {
                    tokenSecret = args.getString(key);
                } else if (key.equals("token")) {
                    token = args.getString(key);
                } else if (key.equals("url")) {
                    url = args.getString(key);
                } else if (key.equals("username")){
                    username = args.getString(key);
                } else if (key.equals("folder_id")){
                    folder_id = args.getInt(key);
                }
            }
        }
        //write username into TextView
        TextView userField = (TextView)findViewById(R.id.username_release);
        userField.setText(username);
        //Oauth query stuff
        System.setProperty("http.agent", userAgent);
        mService = new ServiceBuilder()
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .userAgent(userAgent)
                .callback(CALLBACK_URL)
                .signatureType(com.github.scribejava.core.model.SignatureType.QueryString)
                .debug()
                .build(DiscogsApi.instance());
        //construct token
        mAccessToken = new OAuth1AccessToken(token, tokenSecret);
        Log.d(TAG, mAccessToken.toString());
        getReleases(mService, mAccessToken);

        //ADD RELEASE BUTTON BINDING AND ACTIVITY CALL GO HERE
        Button addButton = (Button)findViewById(R.id.add_release);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //get bundle together
                Bundle addArgs = new Bundle();
                addArgs.putString("url",url);
                addArgs.putString("username",username);
                addArgs.putString("token",mAccessToken.getToken());
                addArgs.putString("token_secret",mAccessToken.getTokenSecret());
                addArgs.putInt("folder_id",folder_id);
                Intent intent = new Intent(AlbumActivity.this,AddActivity.class);
                intent.putExtras(addArgs);
                startActivity(intent);
            }
        });

    }

    private void getReleases(final OAuth10aService service, final OAuth1AccessToken token) {
        (new AsyncTask<Void, Void, Response>() {
            private String responseBody;

            @Override
            protected Response doInBackground(Void... params) {
                Log.d(TAG, "URL: " + url);
                final OAuthRequest folderRequest = new OAuthRequest(Verb.GET, url, service);
                folderRequest.addOAuthParameter("oauth_token_secret", token.getTokenSecret());
                folderRequest.addHeader("User-Agent", "Discogeronomy/1.0");
                service.signRequest(token, folderRequest);
                final Response response = folderRequest.send();
                return response;
            }

            @Override
            protected void onPostExecute(Response response) {
                ReleaseList releaseArray = null;
                try {
                    responseBody = response.getBody();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, responseBody);
                //use an adapter to convert response to clickable fields
                ObjectMapper mapper = new ObjectMapper();
                try {
                    releaseArray = mapper.readValue(responseBody, ReleaseList.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "FIRST RELEASE Title: " + releaseArray.getReleases().get(0)
                        .getBasic_information().getTitle());
                Log.d(TAG, "Releases: " + Integer.toString(releaseArray.getReleases().size()));
                //inflate layout with data fields
            LinearLayout releaseList = (LinearLayout)findViewById(R.id.release_list);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i < releaseArray.getReleases().size();i++){
                View menuLayout = inflater.inflate(R.layout.release_panel,releaseList,true);
            }
            for (int i = 0 ; i < releaseArray.getReleases().size();i++){
                Log.d(TAG,Integer.toString(i));
                final int iter = i;
                Log.d(TAG,"Number of children: " + Integer.toString(releaseList.getChildCount()));
                LinearLayout release = (LinearLayout)releaseList.getChildAt(i);
                LinearLayout intermediate = (LinearLayout)release.getChildAt(0);
                intermediate = (LinearLayout)intermediate.getChildAt(1);
                //Only lists top artist for now, can iterate through array with carriage returns
                String releaseTitle = releaseArray.getReleases().get(i).getBasic_information().getTitle();
                String artistName = releaseArray.getReleases().get(i).getBasic_information().getArtists()
                        .get(0).getName();
                String formatName = releaseArray.getReleases().get(i).getBasic_information().getArtists()
                        .get(0).getName();
                int year = releaseArray.getReleases().get(i)
                        .getBasic_information().getYear();
                //set text fields
                TextView target = (TextView)intermediate.getChildAt(0);
                if (releaseTitle.isEmpty()){target.setText("N/A");}
                else target.setText(releaseTitle);
                target = (TextView)intermediate.getChildAt(1);
                if (artistName.isEmpty()){target.setText("N/A");}
                else target.setText(artistName);
                target = (TextView)intermediate.getChildAt(2);
                if (formatName.isEmpty()){target.setText("N/A");}
                else target.setText(formatName);
                target = (TextView)intermediate.getChildAt(3);
                target.setText(Integer.toString(year));
                //ratings bar
                RatingBar ratingBar = (RatingBar)intermediate.getChildAt(4);
                ratingBar.setRating((float)releaseArray.getReleases().get(i).getRating());
                //set click listener for next view
                final ReleaseList ra = releaseArray;
                //HERE YOU WILL SET THE BUTTON CLICK LISTENERS
                intermediate = (LinearLayout)release.getChildAt(1);
                Button targetButton = (Button)intermediate.getChildAt(0);
                final RatingBar rbar = ratingBar;
                final ReleaseList rl = releaseArray;
                targetButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //get rating from bar
                        int rating = (int)rbar.getRating();
                        Log.d(TAG,"Value is " + rating);
                        //build url
                        StringBuilder editUrl = new StringBuilder();
                        editUrl.append(url);
                        editUrl.append("/"+rl.getReleases().get(iter).getId());
                        editUrl.append("/instances");
                        editUrl.append("/"+rl.getReleases().get(iter).getInstance_id());
                        Log.d(TAG,"Edit URL: " + editUrl.toString());
                        instanceUrl = editUrl.toString();
                        //start forming post
                        OAuthRequest editRequest = new OAuthRequest(Verb.POST,editUrl.toString(),mService);
                        editRequest.addHeader("User-Agent","Discogeronomy/1.0");
                        editRequest.addHeader("Content-Type","application/json");
                        StringBuilder jsonBody = new StringBuilder();
                        jsonBody.append("{\"username\":");
                        jsonBody.append(encloseQuotes(username)+",");
                        jsonBody.append("\"release_id\":");
                        jsonBody.append(Integer.toString(rl.getReleases().get(iter).getId())+",");
                        jsonBody.append("\"instance_id\":");
                        jsonBody.append(Integer.toString(rl.getReleases().get(iter).getInstance_id())+",");
                        jsonBody.append("\"rating\":");
                        jsonBody.append(Integer.toString(rating)+"}");
                        Log.d(TAG,"JSON:   " + jsonBody.toString());
                        editRequest.addPayload(jsonBody.toString());
                        mService.signRequest(mAccessToken,editRequest);
                        final OAuthRequest er = editRequest;
                        (new AsyncTask<Void,Void,Response>() {
                            @Override
                            protected Response doInBackground(Void... params) {
                                final Response resp = er.send();
                                return resp;
                            }
                            @Override
                            protected void onPostExecute(Response response){
                                //reload screen
                                try {
                                    Log.d(TAG,response.getBody());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                startActivity(getIntent());
                            }
                        }).execute();

                    }
                });
                targetButton = (Button)intermediate.getChildAt(1);
                targetButton.setOnClickListener(new View.OnClickListener(){
                   @Override
                    public void onClick(View view){
                       StringBuilder editUrl = new StringBuilder();
                       editUrl.append(url);
                       editUrl.append("/"+rl.getReleases().get(iter).getId());
                       editUrl.append("/instances");
                       editUrl.append("/"+rl.getReleases().get(iter).getInstance_id());
                       OAuthRequest deleteRequest = new OAuthRequest(Verb.DELETE,editUrl.toString(),mService);
                       deleteRequest.addHeader("User-Agent","Discogeronomy/1.0");
                       deleteRequest.addHeader("Content-Type","application/json");
                       //construct json body
                       StringBuilder jsonBody = new StringBuilder();
                       jsonBody.append("{\"username\":");
                       jsonBody.append(encloseQuotes(username)+",");
                       jsonBody.append("\"folder_id\":");
                       jsonBody.append(Integer.toString(folder_id)+",");
                       jsonBody.append("\"release_id\":");
                       jsonBody.append(Integer.toString(rl.getReleases().get(iter).getId())+",");
                       jsonBody.append("\"instance_id\":");
                       jsonBody.append(Integer.toString(rl.getReleases().get(iter).getInstance_id())+"}");
                       Log.d(TAG,jsonBody.toString());
                       mService.signRequest(mAccessToken,deleteRequest);
                       final OAuthRequest dr = deleteRequest;
                       (new AsyncTask<Void,Void,Response>() {
                           @Override
                           protected Response doInBackground(Void... params) {
                               Response delrep = dr.send();
                               return delrep;
                           }
                           @Override
                           protected void onPostExecute(Response response){
                               //reload screen
                               try {
                                   Log.d(TAG,response.getBody());
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                               finish();
                               startActivity(getIntent());
                           }
                       }).execute();
                   }
                });

            }
            }
        }).execute();
    }

    private String encloseQuotes(String orig){
        StringBuilder encloser = new StringBuilder();
        encloser.append("\"");
        encloser.append(orig);
        encloser.append("\"");
        return encloser.toString();
    }
}
