package co.jaypandya.myaddressplus2;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Jay on 4/18/2016.
 */
public class MyContentProvider extends ContentProvider{

    // database instance
    private DatabaseHandler database;

    // used for UriMatcher to match Uri id that we're looking for with the Uri in the table
    // basically we'll attach the id we want to PERSON_ID and we'll cycle through all the IDs in PERSONS.
    // then, when we have a match between PERSONS and PERSON_ID, we'll nail down the row we were looking for
    private static final int PERSONS = 10;
    private static final int PERSON_ID = 20;

    private static final String AUTHORITY = "co.jaypandya.myaddressplus2.persons.contentprovider";

    private static final String BASE_PATH = "persons";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/persons";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/person";

    //sURIMatcher with a default value of false
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // people to look through
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, PERSONS);
        // person to find
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PERSON_ID);
    }

    @Override
    public boolean onCreate(){
        database = new DatabaseHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        // using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //check if the caller has requested a column which doesn't exist
        checkColumns(projection);

        // set the table
        queryBuilder.setTables(MyTableHandler.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType){
            case PERSONS:
                break;
            case PERSON_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(MyTableHandler.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("really? unknown uri: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // ensure that possible listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri){
        return null;
    }

    //insert into table method
    @Override
    public Uri insert(Uri uri, ContentValues values){
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;

        switch (uriType){
            case PERSONS:
                id = sqlDB.insert(MyTableHandler.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("RUH ROH, unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH+"/"+id);
    }

    // delete entry
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType){
            case PERSONS:
                rowsDeleted = sqlDB.delete(MyTableHandler.TABLE_NAME, selection, selectionArgs);
                break;
            case PERSON_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    rowsDeleted = sqlDB.delete(MyTableHandler.TABLE_NAME, MyTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MyTableHandler.TABLE_NAME, MyTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("WTF is this URI? " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType){
            case PERSONS:
                rowsUpdated = sqlDB.update(MyTableHandler.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PERSON_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    rowsUpdated = sqlDB.update(MyTableHandler.TABLE_NAME, values, MyTableHandler.COLUMN_ID + "=" + id, null);
                }else {
                    rowsUpdated = sqlDB.update(MyTableHandler.TABLE_NAME, values, MyTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("vadernooooo, unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection){
        String[] available = {MyTableHandler.COLUMN_FIRSTNAME, MyTableHandler.COLUMN_LASTNAME, MyTableHandler.COLUMN_ADDRESS, MyTableHandler.COLUMN_PROVINCE, MyTableHandler.COLUMN_COUNTRY, MyTableHandler.COLUMN_POSTALCODE, MyTableHandler.COLUMN_DESIGNATION, MyTableHandler.COLUMN_ID};

        if (projection!=null){
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)){
                throw new IllegalArgumentException("Unknown columns in projections");
            }
        }
    }
}
