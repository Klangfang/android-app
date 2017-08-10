package com.wfm.soundcollaborations.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.ContactsActivity;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.FriendEntity;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 22.10.16.
 */

public class ContactsAdapter extends CursorAdapter {

    @SuppressLint("InlinedApi")
    private static final String columnName = Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME;

    private LayoutInflater cursorInflater;

    // Database
    private Dao<FriendEntity, Long> friendDao;

    public ContactsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context,
                DatabaseHelper.class);

        try {
            friendDao = databaseHelper.getFriendDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvPhoneNumber = (TextView) view.findViewById(R.id.tv_phone_number);

        String title = cursor.getString(cursor.getColumnIndex(columnName));
        tvName.setText(title);

        // Query phone numbers
        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

        while (phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    tvPhoneNumber.setText(number);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    break;
            }
        }
        phones.close();

        // Disable if partner was already added
        boolean exists = false;

        try {
            exists = friendDao.idExists(contactId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (exists){
            view.setEnabled(false);
            tvName.setEnabled(false);
            tvPhoneNumber.setEnabled(false);
        } else {
            view.setEnabled(true);
            tvName.setEnabled(true);
            tvPhoneNumber.setEnabled(true);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.contact_row, parent, false);
    }
}
