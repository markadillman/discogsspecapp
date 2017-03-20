package markdillman.discogsspecapp;

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
import android.net.Uri;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.EditText;


import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuffer;
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
 * Created by markdillman on 3/19/17.
 */

public class AddActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    private int folder_id;
    private String username;
    private String targurl;
    private final String WEBVIEW_URL = "https://www.discogs.com/search";
    private final String TAG = "AddActivity";
    private OAuth1AccessToken mAccessToken;
    private String token;
    private String token_secret;
    private OAuth10aService mService;
    private String userAgent = "Crate-a-logue 1.0";
    private static final String CALLBACK_URL = "http://convectionmeadows.com";
    private String consumerKey = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String return_url;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        //setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.equals("url")) {
                    targurl = args.getString(key);
                } else if (key.equals("username")) {
                    username = args.getString(key);
                } else if (key.equals("folder_id")) {
                    folder_id = args.getInt(key);
                } else if (key.equals("token")){
                    token = args.getString(key);
                } else if (key.equals("token_secret")){
                    token_secret=args.getString(key);
                } else if (key.equals("return_url")){
                    return_url=args.getString(key);
                }
            }
        }

        //set up web view
        mWebView = (WebView)findViewById(R.id.webber);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(WEBVIEW_URL);

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
        mAccessToken = new OAuth1AccessToken(token, token_secret);
        Log.d(TAG, mAccessToken.toString());
    }

    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG,"onPageStarted Reached!\n");
            Log.d(TAG,"URL: " + url);
            Pattern p =  Pattern.compile("https://www.discogs.com/.*/release/.*");
            Matcher m = p.matcher(url);
            //if url matches regex, then pull release id and pass it to onPostExecute
            if ((url != null) && m.matches()) { // Override webview when user came back to CALLBACK_URL
                Log.d(TAG,"MATCH!!!!!");
                //extract release
                StringBuffer extractor = new StringBuffer(url);
                int targIndex = extractor.indexOf("/release/");
                Log.d(TAG,"Target index: " + Integer.toString(targIndex));
                //index+8 is final slash
                extractor.delete(0,targIndex+9);
                Log.d(TAG,"RELEASE: " + extractor.toString());
                //remove anything following the next slash or ? if there is anything
                targIndex = extractor.indexOf("/");
                if (targIndex != -1){
                    extractor.delete(targIndex,extractor.length()-1);
                } else Log.d(TAG,"NO CLIP");
                targIndex = extractor.indexOf("?");
                if (targIndex != -1){
                    extractor.delete(targIndex,extractor.length()-1);
                }else Log.d(TAG,"NO CLIP");
                Log.d(TAG,"RELEASE: " + extractor.toString());
                //start crafting async request
                StringBuilder fullurl = new StringBuilder();
                fullurl.append(targurl + "/");
                fullurl.append(extractor.toString());
                Log.d(TAG,"FULL URL: " + fullurl.toString());
                OAuthRequest addreq = new OAuthRequest(Verb.POST,fullurl.toString(),mService);
                addreq.addHeader("Content-Type","application/json");
                //craft json body
                StringBuilder body = new StringBuilder();
                body.append("{\"username\":");
                body.append(encloseQuotes(username)+",");
                body.append("\"folder_id\":");
                body.append(Integer.toString(folder_id)+",");
                body.append("\"release_id\":");
                body.append(extractor.toString()+"}");
                Log.d(TAG,"JSON BODY:" + body.toString() );
                addreq.addPayload(body.toString());
                mService.signRequest(mAccessToken,addreq);
                final OAuthRequest addRequest = addreq;
                //place request
                (new AsyncTask<Void,Void,Response>(){
                    @Override
                    protected Response doInBackground(Void... params) {
                        Response response = addRequest.send();
                        return response;
                    }

                    @Override
                    protected void onPostExecute(Response response){
                        try {
                            Log.d(TAG,response.getBody());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bundle returnArgs = new Bundle();
                        returnArgs.putString("token",mAccessToken.getToken());
                        returnArgs.putString("token_secret",mAccessToken.getTokenSecret());
                        returnArgs.putString("url",return_url);
                        returnArgs.putString("username",username);
                        returnArgs.putInt("folder_id",folder_id);
                        Intent intent = new Intent(AddActivity.this,AlbumActivity.class);
                        intent.putExtras(returnArgs);
                        startActivity(intent);
                    }
                } ).execute();
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    private String encloseQuotes(String orig){
        StringBuilder encloser = new StringBuilder();
        encloser.append("\"");
        encloser.append(orig);
        encloser.append("\"");
        return encloser.toString();
    }
}
