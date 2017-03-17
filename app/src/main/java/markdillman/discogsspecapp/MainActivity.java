package markdillman.discogsspecapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth1AccessTokenExtractor;
import com.github.scribejava.core.extractors.OAuth1RequestTokenExtractor;
import com.github.scribejava.core.model.OAuthConfig;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity{

    private GoogleApiClient mGoogleApiClient;
    private UsersDB mUsersDB;
    private SQLiteDatabase mSQLDB;
    private Cursor mSQLCursor;
    private SimpleCursorAdapter mSQLCursorAdapter;
    private String TAG = "DiscogsDB";
    private Button mButton;
    private String consumerKey    = "aYthzoqSYthdKLpfsDRI"; //api key
    private String consumerSecret = "LBAAQZeGbWJVbXcJksnRzmUIfaoWpsnu"; //api secret
    private String userAgent = "Crate-a-logue 1.0";
    private String requestUrl = "https://www.discogs.com/oauth/authorize";
    private OAuth10aService mService;
    private OAuth1RequestToken mToken;
    private String authUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database Stuff
        mUsersDB = new UsersDB(this);
        mSQLDB = mUsersDB.getWritableDatabase();

        //Button stuff
        mButton = (Button) findViewById(R.id.oauth_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,OAuthActivity.class);
                startActivity(intent);
            }
        });
    }
}
