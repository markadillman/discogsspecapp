package markdillman.discogsspecapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by markdillman on 3/16/17.
 */

public class UsersDB extends SQLiteOpenHelper {
    public UsersDB(Context context){
        super(context, DBContract.UserTable.DB_NAME,null, DBContract.UserTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DBContract.UserTable.SQL_CREATE_DEMO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DBContract.UserTable.SQL_DROP_DEMO_TABLE);
        onCreate(db);
    }
}
