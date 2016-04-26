package co.jaypandya.myaddressplus2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jay on 4/18/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mytable.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // method is called when creating the database- essentially this onCreate method simply calls/uses the oncreate method defined in MyTableHandler
    @Override
    public void onCreate(SQLiteDatabase database){
        MyTableHandler.onCreate(database);
    }

    //method called when needing to upgrade the database version- uses/calls the upgrade method defined in MyTableHandler
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        MyTableHandler.onUpgrade(database, oldVersion, newVersion);
    }
}
