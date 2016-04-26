package co.jaypandya.myaddressplus2;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Jay on 4/18/2016.
 */
public class MyList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private SimpleCursorAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        listView = (ListView) findViewById(R.id.list);
        this.listView.setDividerHeight(2);
        fillData();
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FormActivity.class);
                Uri personUri = Uri.parse(MyContentProvider.CONTENT_ITEM_TYPE + "/" + id);
                intent.putExtra(MyContentProvider.CONTENT_ITEM_TYPE, personUri);

                startActivityForResult(intent, ACTIVITY_EDIT);
            }
        });
    }

    //create menu from xml
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.insert) {
            createPerson();
            return true;
        } else if (id == R.id.about){
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Jay Pandya 062791132 AddressPlus assignment2 part 2")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI+"/"+info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createPerson(){
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void fillData(){
        String[] from = new String[] {MyTableHandler.COLUMN_LASTNAME};
        int[] to = new int[] {R.id.label};

        getLoaderManager().initLoader(0,null,this);
        adapter = new SimpleCursorAdapter(this, R.layout.person_row, null, from, to, 0);

        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.clear);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { MyTableHandler.COLUMN_ID, MyTableHandler.COLUMN_LASTNAME };
        CursorLoader cursorLoader = new CursorLoader(this, MyContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }
}