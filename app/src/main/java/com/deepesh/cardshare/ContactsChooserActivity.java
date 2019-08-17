package com.deepesh.cardshare;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deepesh.cardshare.adapters.ContactsRecycleViewAdapter;
import com.deepesh.cardshare.db.DbHelper;
import com.deepesh.cardshare.models.CardItem;
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
        final String selection = ContactsContract .Contacts.HAS_PHONE_NUMBER + " = ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";
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
                        String formattedNum = phoneNumUtil.format(phNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                        matrixCursor.addRow(new Object[]{cursor.getInt(0), cursor.getString(1), formattedNum});
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

        Intent intent = getIntent();
        ArrayList<String> guestList = intent.getStringArrayListExtra("guestlist");

        final ContactsRecycleViewAdapter recycleViewAdapter = new ContactsRecycleViewAdapter(this, contactsArray, guestList);
        contactsRv.setLayoutManager(new LinearLayoutManager(this));
        contactsRv.setAdapter(recycleViewAdapter);

        findViewById(R.id.fabDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> selectedContacts = recycleViewAdapter.getArrayList();

                if (selectedContacts.size() < 1) {
                    Snackbar.make(contactsRv, " Please choose contacts to send the card !!", Snackbar.LENGTH_SHORT).show();

                } else {
                    String sharedWith = selectedContacts.toString();

                    DbHelper dbHelper = new DbHelper(getApplicationContext());
                    Intent intent = getIntent();
                    ArrayList<String> texts = intent.getStringArrayListExtra("texts");


                    CardItem item = new CardItem(texts.get(0), 0, 0, texts.get(1), 0, 0, texts.get(2), 0, 0, sharedWith);
                    dbHelper.insertUpdateCard(item);
                    //Snackbar.make(contactsRv, " Card sent to selected contacts !!", Snackbar.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
}
