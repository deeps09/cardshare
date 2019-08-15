package com.deepesh.cardshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    private EditText textHeader;
    private EditText textMessage;
    private EditText textFooter;
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private String msg;
    private int RC_PICK_CONTACT = 1;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        textHeader = (EditText) findViewById(R.id.txtHeader);
        textMessage = (EditText) findViewById(R.id.txtMsg);
        textFooter = (EditText) findViewById(R.id.txtFooter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                /*Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/people"));
                startActivityForResult(intent, RC_PICK_CONTACT);*/
                getContacts();
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

    private void getContacts() {

        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, RC_PICK_CONTACT);
        } else {
            Intent intent = new Intent(this, ContactsChooserActivity.class);
            startActivity(intent);

        }
    }
}

