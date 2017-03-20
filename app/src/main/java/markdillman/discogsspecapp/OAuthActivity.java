package markdillman.discogsspecapp;

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
import android.net.Uri;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.EditText;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.StringBuilder;
import java.util.HashMap;

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
 * Created by markdillman on 3/16/17.
 */

public class OAuthActivity extends AppCompatActivity implements AsyncResponse{

    private AsyncIdHandler idHandler;
    private AsyncProfileHandler profHandler;
    private static final String CALLBACK_URL = "http://convectionmeadows.com";
    private String consumerKey    = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String userAgent = "Crate-a-logue 1.0";
    private String requestUrl = "https://www.discogs.com/oauth/authorize";
    private OAuth10aService mService;
    private OAuth1RequestToken mToken;
    private OAuth1AccessToken mAccessToken;
    private String authUrl;
    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    private UsersDB mUsersDB;
    private SQLiteDatabase mSQLDB;
    static final String TAG = "OAuthActivity:";
    private HashMap<String,String> personaMap;
    private HashMap<String,String> profileMap;
    String responseBody;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        //async return stuff
        idHandler = new AsyncIdHandler(this);
        profHandler = new AsyncProfileHandler(this);
        idHandler.delegate = this;
        profHandler.delegate = this;


        //Database Stuff
        mUsersDB = new UsersDB(this);
        mSQLDB = mUsersDB.getWritableDatabase();

        //set user-agent
        System.setProperty("http.agent", userAgent);

        //OAuth Stuff
        Log.d(TAG,"Prebuild:\n");
        mService = new ServiceBuilder()
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .userAgent(userAgent)
                .callback(CALLBACK_URL)
                .signatureType(com.github.scribejava.core.model.SignatureType.QueryString)
                .debug()
                .build(DiscogsApi.instance());
        Log.d(TAG,"Postbuild:\n");

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        ((ViewGroup)mWebView.getParent()).removeView(mWebView);
        setContentView(mWebView);
        Log.d(TAG,"Postload\n");
        asynchAuthorize();

    }

    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG,"onPageStarted Reached!\n");
            if ((url != null) && (url.startsWith(CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                Log.d(TAG,"Conditional statement reached!\n");
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);
                final String verifier = uri.getQueryParameter("oauth_verifier");
                Log.d(TAG,"_____________VERIFIER__________");
                Log.d(TAG,verifier);
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                            Log.d(TAG,"__________GET ACCESS VERIFIER_________");
                        try {
                            return mService.getAccessToken(mToken, verifier);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mAccessToken = mService.getAccessToken(mToken, verifier);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return mAccessToken;
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        //enter token information into SQLite database
                        Log.d(TAG,"-----OAUTH TOKEN-----");
                        Log.d(TAG,accessToken.getParameter("oauth_token"));
                        Log.d(TAG,"-----OAUTH SECRET-----");
                        Log.d(TAG,accessToken.getParameter("oauth_token_secret"));
                        getProfile(accessToken);
                        //idHandler.execute((OAuth1AccessToken)accessToken,mService);
                        //Log.d(TAG,"!!!!!!!!RESULT!!!!!!!!!!");
                        //Log.d(TAG,responseBody);
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    private void getProfile(final Token accessToken){
        (new AsyncTask<Void,Void,Response>(){
            @Override
            protected Response doInBackground(Void... params) {
                final OAuthRequest personaRequest = new OAuthRequest(Verb.GET,"https://api.discogs.com/oauth/identity?",mService);
                personaRequest.addOAuthParameter("oauth_token_secret",accessToken.getParameter("oauth_token_secret"));
                personaRequest.addHeader("User-Agent","Discogeronomy/1.0");
                mService.signRequest((OAuth1AccessToken)accessToken, personaRequest);
                final Response response = personaRequest.send();
                return response;
            }

            @Override
            protected void onPostExecute(Response response){
                String body = null;
                try {
                    body = response.getBody();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //CHECK THAT UNIQUE USERNAME IS NOT ALREADY IN THE DATABASE ------ TO DO!!!!!
                pullInfo(body,(OAuth1AccessToken)accessToken);
                //LOAD USER IN THE DATABASE
            }
        } ).execute();
    }

    private void pullInfo(final String body, final OAuth1AccessToken accessToken){
        //construct url
        //DEBUG STUFF
        Log.d(TAG,"PROFILE REQUEST RESPONSE");
        Log.d(TAG,body);
        System.out.println(body);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,String>> typeRef =
                new TypeReference<HashMap<String,String>>(){};
        try {
            personaMap = mapper.readValue(body,typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,personaMap.get("resource_url"));
        (new AsyncTask<Void,Void,Response>(){
            @Override
            protected Response doInBackground(Void...params){

                final OAuthRequest profileRequest = new OAuthRequest(Verb.GET,personaMap.get("resource_url"),mService);
                profileRequest.addOAuthParameter("oauth_token_secret",accessToken.getParameter("oauth_token_secret"));
                profileRequest.addHeader("User-Agent","Discogeronomy/1.0");
                mService.signRequest((OAuth1AccessToken)accessToken, profileRequest);
                final Response response = profileRequest.send();
                return response;
            }
            @Override
            protected void onPostExecute(Response response){
                try {
                    responseBody = response.getBody();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String,String>> typeRef =
                        new TypeReference<HashMap<String,String>>(){};
                try {
                    profileMap = mapper.readValue(responseBody,typeRef);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,responseBody);
                Log.d(TAG,profileMap.get("email"));
                if (mSQLDB != null){
                    ContentValues vals = new ContentValues();
                    vals.put(DBContract.UserTable.COLUMN_NAME_USER_NAME, profileMap.get("username"));
                    vals.put(DBContract.UserTable.COLUMN_NAME_PROPER_NAME, profileMap.get("name"));
                    vals.put(DBContract.UserTable.COLUMN_NAME_ID,profileMap.get("id"));
                    vals.put(DBContract.UserTable.COLUMN_NAME_TOKEN,accessToken.getToken());
                    vals.put(DBContract.UserTable.COLUMN_NAME_TOKEN_SECRET,accessToken.getTokenSecret());
                    vals.put(DBContract.UserTable.COLUMN_NAME_URL,profileMap.get("resource_url"));
                    vals.put(DBContract.UserTable.COLUMN_NAME_LOGGED_IN,"1");
                    vals.put(DBContract.UserTable.COLUMN_NAME_COLLECTION_URL,profileMap.get("collection_folders_url"));
                    mSQLDB.insert(DBContract.UserTable.TABLE_NAME,null,vals);
                    Intent intent = new Intent(OAuthActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        }).execute();
    }

    private void asynchAuthorize(){
        (new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG,"Pre: getRequestToken()\n");
                try {
                    mToken = mService.getRequestToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,mToken.getToken());
                Log.d(TAG,"Post: getRequestToken()\n");

                return authUrl = mService.getAuthorizationUrl(mToken);
            }

            @Override
            protected void onPostExecute(String url){
                Log.d(TAG,"Pre: loadUrl()\n");
                //Build URL
                StringBuilder authQueries = new StringBuilder();
                authQueries.append(authUrl);
                authQueries.append("?oauth_token=");
                authQueries.append(mToken.getToken());
                Log.d(TAG,"FINAL URL QUERY: ");
                Log.d(TAG,authQueries.toString());
                mWebView.loadUrl(authQueries.toString());
                Log.d(TAG,"Post: loadUrl()\n");
            }
        } ).execute();
    }

    @Override
    public void processFinish(String output){
        responseBody = output;
    }

    private String urlFix(String url){
        StringBuilder fixer = new StringBuilder();
        fixer.append(url);
        fixer.append("?");
        return fixer.toString();
    }
}
