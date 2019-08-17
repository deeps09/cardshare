package com.deepesh.cardshare.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.deepesh.cardshare.R;
import com.deepesh.cardshare.models.Contacts;
import com.deepesh.cardshare.utils.MyUtils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;

public class ContactsRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final ArrayList<Contacts> mAllContacts;
    private final ArrayList<Integer> posClicked = new ArrayList<>();
    private final ArrayList<String> phNumbers = new ArrayList<>();
    private ArrayList<String> selectedPhNumbers = new ArrayList<>();
    private int isAnyItemSelected = -1; // -1 no change, 1 added, 2 deleted
    private int selPhNumPrevSize;
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private String[] uniqueNumbers;
    private boolean[] uniqueNumChecked;
    private final MyUtils myUtils = new MyUtils();

    // constructor
    public ContactsRecycleViewAdapter(Context context, ArrayList<Contacts> allContacts, ArrayList<String> existingGuestList) {
        this.mContext = context;
        this.mAllContacts = allContacts;
        this.selectedPhNumbers = existingGuestList;
    }

    public ArrayList<String> getArrayList() {
        return selectedPhNumbers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_list_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Contacts contactsObj = mAllContacts.get(position);
        int contactId = contactsObj.getId();
        String name = contactsObj.getName();
        String initial = name.substring(0, 1).toUpperCase();
        String number = contactsObj.getPhNumber();


        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.initial.setText(initial);
        itemViewHolder.name.setText(name + "\n" + number);

        if (posClicked.contains(position) || selectedPhNumbers.contains(number)) {
            itemViewHolder.checkBox.setChecked(true);
            posClicked.add(position);

        } else
            itemViewHolder.checkBox.setChecked(false);

        // checking if any contact of contact_id was previously selected
        Cursor cursor;
        String[] projection = new String[]
                {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(contactId), "1"};
        cursor = mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);

        boolean matchFound = false;
        if (cursor != null && cursor.getCount() > 1 && selectedPhNumbers.size() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && !matchFound) {
                String numWoSpaces = myUtils.replaceSpacesAndDash(cursor.getString(1));

                for (String selectedPhNum : selectedPhNumbers) {
                    selectedPhNum = myUtils.replaceSpacesAndDash(selectedPhNum);

                    numWoSpaces = myUtils.getLast10Letters(numWoSpaces);
                    selectedPhNum = myUtils.getLast10Letters(selectedPhNum);

                    if (numWoSpaces.contains(selectedPhNum)) {
                        itemViewHolder.checkBox.setChecked(true);
                        posClicked.add(position);
                        matchFound = true;
                        break;
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return mAllContacts != null ? mAllContacts.size() : 0;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView initial;
        final TextView name;
        final CheckBox checkBox;

        private ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.contactIcon);
            name = itemView.findViewById(R.id.displayName);
            checkBox = itemView.findViewById(R.id.checkbox);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contacts contacts = mAllContacts.get(getAdapterPosition());

                    String[] projection = new String[]
                            {BaseColumns._ID,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER};


                    String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";
                    String[] selectionArgs = new String[]{String.valueOf(contacts.getId()), "1"};
                    Cursor cursorNumbers = mContext.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);

                    if (cursorNumbers != null) {
                        cursorNumbers.moveToFirst();
                        while (!cursorNumbers.isAfterLast()) {
                            String phNum = myUtils.replaceSpacesAndDash(cursorNumbers.getString(3));

                            // in case phone number is starting with 0 then replacing it with India country code
                            if (phNum.startsWith("0"))
                                phNum = phNum.replaceFirst("0", "+91");

                            // if no country code is added with number then appending Indian country code with it
                            if (!phNum.startsWith("0") && !phNum.startsWith("+"))
                                phNum = "+91" + phNum;

                            // converting code in International format
                            try {
                                Phonenumber.PhoneNumber phNumber = phoneNumberUtil.parse(phNum, "");
                                phNum = phoneNumberUtil.format(phNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                            } catch (NumberParseException NumEx) {
                                Log.d("NumberParseException", NumEx.getMessage());
                            }

                            // avoid adding same number again in phNumbers array list
                            if (!phNumbers.contains(phNum)) {
                                phNumbers.add(phNum);
                            }
                            cursorNumbers.moveToNext();
                        }

                        // making string & boolean array to show alert dialog with additonal number of a contact
                        // boolean array will decide which item is preselected based on existing guest list received in constructor
                        uniqueNumbers = new String[phNumbers.size()];
                        uniqueNumChecked = new boolean[phNumbers.size()];
                    }

                    // converting item of phNumbers to string array and its corresponding boolean to show it checked
                    for (int i = 0; i < phNumbers.size(); i++) {
                        uniqueNumbers[i] = phNumbers.get(i);
                        uniqueNumChecked[i] = selectedPhNumbers.contains(phNumbers.get(i));
                    }

                    // checking the item if it has only one contact then enabling its checkbox to check and uncheck
                    if (phNumbers.size() == 1) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            posClicked.remove(posClicked.indexOf(getAdapterPosition()));
                            selectedPhNumbers.remove(phNumbers.get(0));

                        } else {
                            checkBox.setChecked(true);
                            posClicked.add(getAdapterPosition());
                            selectedPhNumbers.add(phNumbers.get(0));
                        }
                    } else {

                        // Showing list of additional contacts to user upon item click
                        Dialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(mContext.getString(R.string.select_ph_number));
                        builder.setMultiChoiceItems(uniqueNumbers, uniqueNumChecked,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                        if (isChecked) {
                                            selectedPhNumbers.add(uniqueNumbers[which]);
                                        } else {
                                            selectedPhNumbers.remove(uniqueNumbers[which]);
                                        }
                                    }
                                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // new elements are added in selectedPhNumbers setting item clicked
                                for (String uniqueNumber : uniqueNumbers) {
                                    if (selectedPhNumbers.contains(uniqueNumber)) {
                                        isAnyItemSelected = 1;
                                        break;
                                    } else if (!selectedPhNumbers.contains(uniqueNumber)) {
                                        isAnyItemSelected = 2;
                                    }
                                }

                                if (isAnyItemSelected == 1) {
                                    checkBox.setChecked(true);
                                    posClicked.add(getAdapterPosition());
                                } else if (isAnyItemSelected == 2) {
                                    checkBox.setChecked(false);
                                    if (posClicked.contains(getAdapterPosition()))
                                        posClicked.remove(posClicked.indexOf(getAdapterPosition()));
                                }

                            }
                        });

                        dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                    phNumbers.clear();
                }

            });
        }
    }
}
