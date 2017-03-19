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
import java.lang.StringBuilder;

import com.google.android.gms.common.api.GoogleApiClient;


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
    private ListView mSQLList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database Stuff
        mUsersDB = new UsersDB(this);
        mSQLDB = mUsersDB.getWritableDatabase();

        if(mSQLDB!=null) try {
            if (mSQLCursorAdapter != null && mSQLCursorAdapter.getCursor() != null) {
                if (!mSQLCursorAdapter.getCursor().isClosed()) {
                    mSQLCursorAdapter.getCursor().close();
                }
            }
            String[] columns = {DBContract.UserTable.COLUMN_NAME_USER_NAME,
                    DBContract.UserTable.COLUMN_NAME_PROPER_NAME,DBContract.UserTable._ID};
            Log.d(TAG,"Prequery.");
            mSQLCursor = mSQLDB.query(true,DBContract.UserTable.TABLE_NAME,columns,null,null,null,null,null,null);
            if (!(mSQLCursor.moveToFirst()) || mSQLCursor.getCount() ==0){
                Log.d(TAG,"Cursor is empty.");
            }
            //mSQLCursor = mSQLDB.rawQuery("select * from users",null);
            Log.d(TAG,"Postquery.");
            mSQLList = (ListView) findViewById(R.id.accounts_list);
            mSQLCursorAdapter = new SimpleCursorAdapter(this, R.layout.user_panel,
                    mSQLCursor, new String[]{DBContract.UserTable.COLUMN_NAME_USER_NAME,
                    DBContract.UserTable.COLUMN_NAME_PROPER_NAME},
                    new int[]{R.id.username, R.id.name}, 0);
            mSQLList.setAdapter(mSQLCursorAdapter);
        } catch (Exception e) {
            Log.d(TAG, "Error loading data from database.");
        }

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
