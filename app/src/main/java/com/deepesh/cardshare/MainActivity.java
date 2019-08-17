package com.deepesh.cardshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepesh.cardshare.db.DbHelper;
import com.deepesh.cardshare.models.CardItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText textHeader;
    private EditText textMessage;
    private EditText textFooter;
    private ImageView editGuestList;
    private BottomSheetBehavior bottomSheetBehavior;
    private List<String> guestNum;
    private ListView guestList;
    private TextView guestListHeader;

    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private String msg;
    private int RC_PICK_CONTACT = 1;
    private int RC_CONTACTS_CHOOSER = 101;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set to adjust screen height automatically, when soft keyboard appears on screen

        textHeader = findViewById(R.id.txtHeader);
        textMessage = findViewById(R.id.txtMsg);
        textFooter = findViewById(R.id.txtFooter);
        editGuestList = findViewById(R.id.editGuestList);
        guestList = findViewById(R.id.guestList);
        TextView listEmptyView = findViewById(R.id.empty_view);
        guestListHeader = findViewById(R.id.guestListHead);

        listEmptyView.setText("No guest added yet !! Use pencil icon to add guests");
        guestList.setEmptyView(listEmptyView);

        refreshGuestListView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                getContacts();

            }
        });

        editGuestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
// The View with the BottomSheetBehavior
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.peed_height));

        // to avoid bringing down bottom sheet while scrolling listview down
        guestList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PICK_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getContacts();
            else
                Snackbar.make(textHeader, "Permission denied, unable to load contacts", Snackbar.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CONTACTS_CHOOSER && resultCode == RESULT_OK) {
            refreshGuestListView();
            Toast.makeText(this, "Guest List updated !!", Toast.LENGTH_SHORT).show();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void refreshGuestListView() {

        DbHelper dbHelper = new DbHelper(this);
        CardItem item = dbHelper.getCard();

        if (item != null) {
            guestNum = Arrays.asList(item.getGuestList().replace("[", "")
                    .replace("]", "").split("\\s*,\\s*"));
            List<String> guestNumWithName = new ArrayList<>();


            Cursor cursor;
            String[] projection = new String[]
                    {BaseColumns._ID,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER};

            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";
            String[] selectionArgs;
            String numWithINformat;

            for (String num : guestNum) {
                numWithINformat = num;
                num = num.replaceAll(" ", "");

                if (num.length() > 10)
                    num = num.substring(num.length() - 10);

                selectionArgs = new String[]{'%' + num + '%', "1"};
                cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);


                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    guestNumWithName.add(cursor.getString(2) + "\n" + " " + numWithINformat);
                    cursor.close();
                }
            }

            guestListHeader.setText("Guest Listq (" + guestNumWithName.size() + ")");
            String numbers = guestNumWithName.toString();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.tvListItem, guestNumWithName);
            guestList.setAdapter(arrayAdapter);
        }
    }

    private void getContacts() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, RC_PICK_CONTACT);
        } else {

            ArrayList<String> existingGuests = new ArrayList<String>();

            if (guestNum != null && guestNum.size() > 0)
                existingGuests.addAll(guestNum);

            Intent intent = new Intent(this, ContactsChooserActivity.class);
            ArrayList<String> texts = new ArrayList<>();
            texts.add(textHeader.getText().toString());
            texts.add(textMessage.getText().toString());
            texts.add(textFooter.getText().toString());
            intent.putStringArrayListExtra("texts", texts);
            intent.putStringArrayListExtra("guestlist", existingGuests);
            Bundle bundle = new Bundle();

            startActivityForResult(intent, RC_CONTACTS_CHOOSER);
        }
    }
}
