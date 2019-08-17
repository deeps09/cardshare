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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepesh.cardshare.db.DbHelper;
import com.deepesh.cardshare.models.CardItem;
import com.deepesh.cardshare.utils.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText person1;
    private EditText message;
    private EditText person2;
    private EditText conjuction;
    private ImageView editGuestList;
    private BottomSheetBehavior bottomSheetBehavior;
    private List<String> guestNum;
    private ListView guestList;
    private TextView guestListHeader;

    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private String msg;
    private final MyUtils myUtils = new MyUtils();
    private final int RC_PICK_CONTACT = 1;
    private final int RC_CONTACTS_CHOOSER = 101;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        person1 = findViewById(R.id.person1);
        message = findViewById(R.id.txtMsg);
        person2 = findViewById(R.id.person2);
        conjuction = findViewById(R.id.conjunction);

        editGuestList = findViewById(R.id.editGuestList);
        guestList = findViewById(R.id.guestList);
        TextView listEmptyView = findViewById(R.id.empty_view);
        guestListHeader = findViewById(R.id.guestListHead);

        listEmptyView.setText(getString(R.string.empty_list_text));
        guestList.setEmptyView(listEmptyView);

        loadInitialScreen();
        refreshGuestListView();

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
        // if coming back from another activity and bottom sheet is opened then minimizing it
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PICK_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getContacts();
            else
                Snackbar.make(person1, "Permission denied, unable to load contacts", Snackbar.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // reloading the listview to show the updated list of guests upon coming back
        if (requestCode == RC_CONTACTS_CHOOSER && resultCode == RESULT_OK) {
            refreshGuestListView();
            Toast.makeText(this, "Guest List updated !!", Toast.LENGTH_SHORT).show();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void loadInitialScreen() {
        DbHelper dbHelper = new DbHelper(this);
        CardItem cardItem = dbHelper.getCard();

        if (cardItem != null) {
            person1.setText(cardItem.getPerson1());
            person2.setText(cardItem.getPerson2());
            conjuction.setText(cardItem.getConjunction());
            message.setText(cardItem.getMessage());
        }
    }

    private void refreshGuestListView() {

        DbHelper dbHelper = new DbHelper(this);
        CardItem item = dbHelper.getCard();

        if (item != null) {
            // converting contacts in string to array list
            guestNum = Arrays.asList(item.getGuestList().replace("[", "")
                    .replace("]", "").split("\\s*,\\s*"));
            List<String> guestNumWithName = new ArrayList<>();


            // preparing for searching contacts from contact storage
            Cursor cursor;
            String[] projection = new String[]
                    {BaseColumns._ID,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER};

            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?";
            String[] selectionArgs;
            String numWithINformat;

            // formatting phone numbers to get only last 10 digit without any extra characters
            for (String num : guestNum) {
                numWithINformat = num;
                num = num.replaceAll(" ", "");

                if (num.length() > 10)
                    num = num.substring(num.length() - 10);

                selectionArgs = new String[]{myUtils.format10DigitPhoneNumberForSearching(num)};
                cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);


                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    guestNumWithName.add(cursor.getString(2) + "\n" + " " + numWithINformat);
                    cursor.close();
                } else {
                    // if in any case contact name is not found then showing number only
                    guestNumWithName.add(numWithINformat);
                }
            }

            guestListHeader.setText(getString(R.string.guestlist_header_text1) + guestNumWithName.size() + ")");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.tvListItem, guestNumWithName);
            guestList.setAdapter(arrayAdapter);
        }
    }

    private void getContacts() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, RC_PICK_CONTACT);
        } else {

            ArrayList<String> existingGuests = new ArrayList<>();

            if (guestNum != null && guestNum.size() > 0)
                existingGuests.addAll(guestNum);

            Intent intent = new Intent(this, ContactsChooserActivity.class);
            ArrayList<String> cardTexts = new ArrayList<>();
            cardTexts.add(person1.getText().toString());
            cardTexts.add(person2.getText().toString());
            cardTexts.add(conjuction.getText().toString());
            cardTexts.add(message.getText().toString());

            intent.putStringArrayListExtra(getString(R.string.cardtexts), cardTexts);
            intent.putStringArrayListExtra(getString(R.string.guestlist), existingGuests);
            Bundle bundle = new Bundle();

            startActivityForResult(intent, RC_CONTACTS_CHOOSER);
        }
    }
}
