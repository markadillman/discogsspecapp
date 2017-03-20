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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.StringBuilder;

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

public class CollectionActivity extends AppCompatActivity {

    //class variables
    private Button mButton;
    private UsersDB mUsersDB;
    private SQLiteDatabase mSQLDB;
    private String username;
    private String token;
    private String tokenSecret;
    private String collectionUrl;
    private int id;
    private int localId;
    private static final String CALLBACK_URL = "http://convectionmeadows.com";
    private String consumerKey    = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String userAgent = "Crate-a-logue 1.0";
    private String requestUrl = "https://www.discogs.com/oauth/authorize";
    private OAuth10aService mService;
    private OAuth1AccessToken mAccessToken;
    private final String TAG = "CollectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        //unpack bundle
        Bundle args = getIntent().getExtras();
        if (args != null){
            for (String key: args.keySet()){
                if (key.equals("token_secret")){
                    tokenSecret = args.getString(key);
                }
                else if (key.equals("collection_url")){
                    collectionUrl = args.getString(key);
                }
                else if (key.equals("username")){
                    username = args.getString(key);
                }
                else if (key.equals("id")){
                    id = args.getInt(key);
                }
                else if (key.equals("token")){
                    token = args.getString(key);
                }
                else if (key.equals("local_id")){
                    localId = args.getInt(key);
                }
            }
        }

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
        mAccessToken = new OAuth1AccessToken(token,tokenSecret);
        Log.d(TAG,mAccessToken.toString());

        //Database Stuff
        mUsersDB = new UsersDB(this);
        mSQLDB = mUsersDB.getWritableDatabase();

        //write username into TextView
        TextView userField = (TextView)findViewById(R.id.username);
        userField.setText(username);

        //make query to return folders
        getFolders(mService,mAccessToken);
        //Button stuff
        mButton = (Button) findViewById(R.id.logout);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //delete user from database
                String whereClause = DBContract.UserTable._ID + "=?";
                String[] whereArgs = new String[] {String.valueOf(localId)};
                mSQLDB.delete(DBContract.UserTable.TABLE_NAME,whereClause,whereArgs);
                Intent intent = new Intent(CollectionActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getFolders(final OAuth10aService service, final OAuth1AccessToken token){(new AsyncTask<Void,Void,Response>(){
        private String responseBody;
        @Override
        protected Response doInBackground(Void...params){
            Log.d(TAG,"URL: "+collectionUrl);
            final OAuthRequest folderRequest = new OAuthRequest(Verb.GET,collectionUrl,service);
            folderRequest.addOAuthParameter("oauth_token_secret",token.getTokenSecret());
            folderRequest.addHeader("User-Agent","Discogeronomy/1.0");
            service.signRequest(token, folderRequest);
            final Response response = folderRequest.send();
            return response;
        }
        @Override
        protected void onPostExecute(Response response){
            FolderList folderArray = null;
            try {
                responseBody = response.getBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,responseBody);
            //use an adapter to convert response to clickable fields
            ObjectMapper mapper = new ObjectMapper();
            try {
                folderArray = mapper.readValue(responseBody,FolderList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"FIRST RELEASE NAME: " + folderArray.getFolders().get(0).getName());
            //inflate layout with data fields
            LinearLayout folderList = (LinearLayout)findViewById(R.id.folder_list);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i < folderArray.getFolders().size();i++){
                View menuLayout = inflater.inflate(R.layout.folder_panel,folderList,true);
            }
            for (int i = 0 ; i < folderArray.getFolders().size();i++){
                final int iter = i;
                LinearLayout folder = (LinearLayout)folderList.getChildAt(i);
                TextView target = (TextView)folder.getChildAt(1);
                target.setText(folderArray.getFolders().get(i).getName());
                //set click listener for next view
                final FolderList fa = folderArray;
                target.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Bundle albumArgs = new Bundle();
                        albumArgs.putString("token",token.getToken());
                        albumArgs.putString("token_secret",token.getTokenSecret());
                        //construct url
                        StringBuilder folderUrl = new StringBuilder();
                        folderUrl.append(collectionUrl+"/");
                        folderUrl.append(Integer.toString(fa.getFolders().get(iter).getId()));
                        folderUrl.append("/releases");
                        albumArgs.putString("url",folderUrl.toString());
                        albumArgs.putString("username",username);
                        albumArgs.putInt("folder_id",fa.getFolders().get(iter).getId());
                        Intent intent = new Intent(CollectionActivity.this,AlbumActivity.class);
                        intent.putExtras(albumArgs);
                        startActivity(intent);
                    }
                });
            }
        }
    }).execute();

    }
    private String urlFix(String url){
        StringBuilder fixer = new StringBuilder();
        fixer.append(url);
        fixer.append("?");
        return fixer.toString();
    }
}
