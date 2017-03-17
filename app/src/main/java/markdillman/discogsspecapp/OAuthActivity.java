package markdillman.discogsspecapp;

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


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.StringBuilder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth1AccessTokenExtractor;
import com.github.scribejava.core.extractors.OAuth1RequestTokenExtractor;
import com.github.scribejava.core.model.OAuthConfig;

/**
 * Created by markdillman on 3/16/17.
 */

public class OAuthActivity extends AppCompatActivity {

    private static final String CALLBACK_URL = "http://convectionmeadows.com";
    private String consumerKey    = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String userAgent = "Crate-a-logue 1.0";
    private String requestUrl = "https://www.discogs.com/oauth/authorize";
    private OAuth10aService mService;
    private OAuth1RequestToken mToken;
    private String authUrl;
    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    static final String TAG = "OAuthActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        //set user-agent
        System.setProperty("http.agent", userAgent);

        //OAuth Stuff
        Log.d(TAG,"Prebuild:\n");
        mService = new ServiceBuilder()
                .signatureType(SignatureType.QueryString)
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .userAgent(userAgent)
                .callback(CALLBACK_URL)
                .debug()
                .build(DiscogsApi.instance());
        Log.d(TAG,"Postbuild:\n");

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        //mWebView.setWebChromeClient(mWebChromeClient);
        ((ViewGroup)mWebView.getParent()).removeView(mWebView);
        setContentView(mWebView);
        Log.d(TAG,"Postload\n");
        asynchAuthorize();
        //rebuild after WebView closes
        
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
                        try {
                            Log.d(TAG,"__________GET ACCESS VERIFIER_________");
                            return mService.getAccessToken(mToken, verifier);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return mToken;
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        // AccessToken is passed here! Do what you want!
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    private void asynchAuthorize(){
        (new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Log.d(TAG,"Pre: getRequestToken()\n");
                    mToken = mService.getRequestToken();
                    Log.d(TAG,mToken.getToken());
                    Log.d(TAG,"Post: getRequestToken()\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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
}
