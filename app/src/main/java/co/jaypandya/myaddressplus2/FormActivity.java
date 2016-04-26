package co.jaypandya.myaddressplus2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FormActivity extends AppCompatActivity {

    Spinner provincialSpinner, designationSpinner;
    Button submitButton;
    EditText firstName, lastName, address, country, postalCode;
    String aboutMe;

    private Uri personUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);

        //wiring up edittexts
        firstName = (EditText)findViewById(R.id.first_name);
        lastName = (EditText)findViewById(R.id.last_name);
        address = (EditText)findViewById(R.id.address);
        country = (EditText)findViewById(R.id.country);
        postalCode = (EditText)findViewById(R.id.postal_code);
        submitButton = (Button)findViewById(R.id.submit_button);

        // setting up the spinner
        designationSpinner = (Spinner) findViewById(R.id.designations);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.designations, R.layout.support_simple_spinner_dropdown_item);
        designationSpinner.setAdapter(adapter2);

        // setting up the spinner
        provincialSpinner = (Spinner) findViewById(R.id.province_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.provinces, R.layout.support_simple_spinner_dropdown_item);
        provincialSpinner.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();

        // check from the saved instance
        personUri = (savedInstanceState == null) ? null : (Uri)savedInstanceState.getParcelable(MyContentProvider.CONTENT_ITEM_TYPE);

        // passed from another activity?
        if (extras != null){
            personUri = extras.getParcelable(MyContentProvider.CONTENT_ITEM_TYPE);
            fillData(personUri);
        }

        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(TextUtils.isEmpty(firstName.getText().toString())){
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void fillData(Uri uri){
        String[] projection = {
                MyTableHandler.COLUMN_DESIGNATION,
                MyTableHandler.COLUMN_FIRSTNAME,
                MyTableHandler.COLUMN_LASTNAME,
                MyTableHandler.COLUMN_ADDRESS,
                MyTableHandler.COLUMN_PROVINCE,
                MyTableHandler.COLUMN_COUNTRY,
                MyTableHandler.COLUMN_POSTALCODE
        };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor!=null){
            cursor.moveToFirst();

            String designation = cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_DESIGNATION));
            for(int i = 0; i < designationSpinner.getCount(); i++){
                String s = (String)designationSpinner.getItemAtPosition(i);
                if(s.equalsIgnoreCase(designation)){
                    designationSpinner.setSelection(i);
                }
            }

            firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_FIRSTNAME)));
            lastName.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_LASTNAME)));
            address.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_ADDRESS)));

            String province = cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_PROVINCE));
            for(int i = 0; i < provincialSpinner.getCount(); i++){
                String s = (String)provincialSpinner.getItemAtPosition(i);
                if(s.equalsIgnoreCase(province)){
                    provincialSpinner.setSelection(i);
                }
            }

            country.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_COUNTRY)));
            postalCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyTableHandler.COLUMN_POSTALCODE)));

            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyContentProvider.CONTENT_ITEM_TYPE, personUri);
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    private void saveState(){
        String sDesignation = (String) designationSpinner.getSelectedItem();
        String sFirstName = firstName.getText().toString();
        String sLastName = lastName.getText().toString();
        String sAddress = address.getText().toString();
        String sProvince = (String)provincialSpinner.getSelectedItem();
        String sCountry = country.getText().toString();
        String sPostalCode = postalCode.getText().toString();

        // only save if the text fields were filled
        if(sFirstName.length() == 0 || sLastName.length() == 0 || sAddress.length() == 0 || sCountry.length() == 0 || sPostalCode.length() == 0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MyTableHandler.COLUMN_DESIGNATION, sDesignation);
        values.put(MyTableHandler.COLUMN_FIRSTNAME, sFirstName);
        values.put(MyTableHandler.COLUMN_LASTNAME, sLastName);
        values.put(MyTableHandler.COLUMN_ADDRESS, sAddress);
        values.put(MyTableHandler.COLUMN_PROVINCE, sProvince);
        values.put(MyTableHandler.COLUMN_COUNTRY, sCountry);
        values.put(MyTableHandler.COLUMN_POSTALCODE, sPostalCode);

        if (personUri == null){
            // new person
            personUri = getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
        } else {
            // update person
            getContentResolver().update(personUri, values, null, null);
        }
    }

    private void makeToast(){
        Toast.makeText(FormActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            new AlertDialog.Builder(FormActivity.this)
                    .setTitle("About")
                    .setMessage("Jay Pandya 062791132 AddressPlus assignment2 part 2")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
