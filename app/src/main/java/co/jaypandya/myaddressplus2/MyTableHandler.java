package co.jaypandya.myaddressplus2;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.PublicKey;

/**
 * Created by Jay on 4/18/2016.
 */
public class MyTableHandler {

    //Database table
    public static final String TABLE_NAME = "mytable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESIGNATION = "designation";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_POSTALCODE = "postalcode";

    //table creation query/command
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DESIGNATION + " text not null, "
            + COLUMN_FIRSTNAME + " text not null, "
            + COLUMN_LASTNAME + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_PROVINCE  + " text not null, "
            + COLUMN_COUNTRY + " text not null, "
            + COLUMN_POSTALCODE + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.w(MyTableHandler.class.getName(), "Update db from old version" + oldVersion + " to " + newVersion + ", which now destroys the old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
