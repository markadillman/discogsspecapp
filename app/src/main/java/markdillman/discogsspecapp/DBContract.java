package markdillman.discogsspecapp;

import android.provider.BaseColumns;

/**
 * Created by markdillman on 3/16/17.
 */

public class DBContract {


    private DBContract(){};

    public final class UserTable implements BaseColumns {
        public static final String DB_NAME = "users_db";
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_PROPER_NAME = "user_proper_name";
        public static final String COLUMN_NAME_TOKEN = "user_token";
        public static final String COLUMN_NAME_LOGGED_IN = "logged_in";
        public static final int DB_VERSION = 4;

        public static final String SQL_CREATE_DEMO_TABLE = "CREATE TABLE " +
                DBContract.UserTable.TABLE_NAME + "(" + DBContract.UserTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                DBContract.UserTable.COLUMN_NAME_USER_NAME + " VARCHAR(255)," +
                DBContract.UserTable.COLUMN_NAME_PROPER_NAME + " VARCHAR(255)," +
                DBContract.UserTable.COLUMN_NAME_TOKEN + " VARCHAR(255)," +
                DBContract.UserTable.COLUMN_NAME_LOGGED_IN + " INT);";

        public static final String SQL_DROP_DEMO_TABLE = "DROP TABLE IF EXISTS " + DBContract.UserTable.TABLE_NAME;
    }
}
