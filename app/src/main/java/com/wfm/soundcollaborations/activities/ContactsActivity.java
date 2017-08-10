package com.wfm.soundcollaborations.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.ContactsAdapter;

/**
 * Created by Markus Eberts on 22.10.16.
 */
public class ContactsActivity extends MainActivity implements  LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    private final static String TAG = ContactsActivity.class.getSimpleName();

    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_KEY_INDEX = 1;
    private static final int CONTACT_NAME_INDEX = 2;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";


    private ListView lvContacs;
    private ContactsAdapter contactsAdapter;

    private String searchString = "";
    private String[] selectionArgs = {searchString};


    public ContactsActivity(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.contacts, (ViewGroup) findViewById(R.id.content_layout));
        getSupportLoaderManager().initLoader(0, null, this);

        lvContacs = (ListView) findViewById(R.id.lv_contacs);
        contactsAdapter = new ContactsAdapter(this, null, 0);
        lvContacs.setAdapter(contactsAdapter);
        lvContacs.setOnItemClickListener(this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        selectionArgs[0] = "%" + searchString + "%";

        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
        );
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((ContactsAdapter) parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        long contactId = cursor.getLong(CONTACT_ID_INDEX);
        String contactKey = cursor.getString(CONTACT_KEY_INDEX);
        String contactName = cursor.getString(CONTACT_NAME_INDEX);
        Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey);

        if (!view.isEnabled()){
            Toast.makeText(this, "Du hast " + contactName + " bereits als Partner hinzugefügt.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Return result to activity
        Intent data = new Intent();
        data.putExtra("contactId", contactId);
        data.putExtra("contactName", contactName);
        setResult(RESULT_OK, data);
        finish();
    }
}
