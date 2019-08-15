package com.deepesh.cardshare;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.deepesh.cardshare.adapters.ContactsRecycleViewAdapter;
import com.deepesh.cardshare.models.Contacts;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;

public class ContactsChooserActivity extends AppCompatActivity {

    RecyclerView contactsRv;
    PhoneNumberUtil phoneNumUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_chooser);

        phoneNumUtil = PhoneNumberUtil.getInstance();
        contactsRv = findViewById(R.id.contactsRecyclerView);

        String[] projection = new String[]
                {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";

        String[] selectionArgs = new String[]{"1", "1"};

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, orderBy);

        int prevId = -1;
        int id = -1;
        Contacts contacts;
        ArrayList<Contacts> contactsArray = new ArrayList<>();
        String tag = "ContactsTesting";

        // Taking only mobile numbers from the contacts
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"id", "name", "number"});
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                try {
                    Phonenumber.PhoneNumber phNumber = phoneNumUtil.parse(cursor.getString(2), "");
                    PhoneNumberUtil.PhoneNumberType phType = phoneNumUtil.getNumberType(phNumber);

                    if (phType == PhoneNumberUtil.PhoneNumberType.MOBILE) {
                        matrixCursor.addRow(new Object[]{cursor.getInt(0), cursor.getString(1), cursor.getString(2).replace(" ","").replace("-","") });
                    }
                } catch (NumberParseException NumEx) {
                    Log.d("NumberParseException", NumEx.getMessage());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        // Filtering unique contact numbers
        matrixCursor.moveToFirst();
        while (!matrixCursor.isAfterLast()) {
            id = Integer.parseInt(matrixCursor.getString(0));
            if (prevId != id) {
                contacts = new Contacts(id, matrixCursor.getString(1), matrixCursor.getString(2));
                contactsArray.add(contacts);
            }
            prevId = id;
            matrixCursor.moveToNext();
        }

        if (!matrixCursor.isClosed())
            matrixCursor.close();

        for (int i = 0; i < contactsArray.size(); i++) {
            Contacts c = contactsArray.get(i);
            Log.d(tag, "id = " + c.getId() + "\n" +
                    "*************" + "\n" +
                    "name: " + c.getName() + "\n" +
                    "Phone: " + c.getPhNumber());

        }

        final ContactsRecycleViewAdapter recycleViewAdapter = new ContactsRecycleViewAdapter(this, contactsArray);
        contactsRv.setLayoutManager(new LinearLayoutManager(this));
        contactsRv.setAdapter(recycleViewAdapter);

        findViewById(R.id.fabDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Selected Contacts are: " + recycleViewAdapter.getArrayList().size(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
