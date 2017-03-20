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
    private String url;
    private final String WEBVIEW_URL = "https://www.discogs.com/search";
    private final String TAG = "AddActivity";
    private OAuth1AccessToken mAccessToken;
    private String token;
    private String token_secret;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        //setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.equals("url")) {
                    url = args.getString(key);
                } else if (key.equals("username")) {
                    username = args.getString(key);
                } else if (key.equals("folder_id")) {
                    folder_id = args.getInt(key);
                } else if (key.equals("token")){
                    token = args.getString(key);
                }
                else if (key.equals("token_secret")){
                    token_secret=args.getString(key);
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
                extractor.delete(0,targIndex+8);
                Log.d(TAG,"RELEASE: " + extractor.toString());
                //remove anything following the next slash or ? if there is anything
                targIndex = extractor.indexOf("/");
                if (targIndex != -1){
                    extractor.delete(targIndex,extractor.length()-1);
                }
                targIndex = extractor.indexOf("?");
                if (targIndex != -1){
                    extractor.delete(targIndex,extractor.length()-1);
                }
                Log.d(TAG,"RELEASE: " + extractor.toString());
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

}
