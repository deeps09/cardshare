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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deepesh.cardshare.R;
import com.deepesh.cardshare.models.Contacts;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactsRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ArrayList<Contacts> mAllContacts;
    private ArrayList<Integer> posClicked = new ArrayList<>();
    private ArrayList<String> phNumbers = new ArrayList<>();
    private ArrayList<String> selectedPhNumbers = new ArrayList<>();
    private boolean isAnyItemSelected = false;
    private int selPhNumPrevSize;

    public ContactsRecycleViewAdapter(Context context, ArrayList<Contacts> allContacts) {
        this.mContext = context;
        this.mAllContacts = allContacts;
    }

    public ArrayList<String> getArrayList(){
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
        String name = contactsObj.getName();
        String initial = name.substring(0, 1).toUpperCase();
        String number = contactsObj.getPhNumber();


        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        ((ItemViewHolder) viewHolder).initial.setText(initial);
        ((ItemViewHolder) viewHolder).name.setText(name + "\n" + number);

        if (posClicked.contains(position))
            ((ItemViewHolder) viewHolder).checkBox.setChecked(true);
        else
            ((ItemViewHolder) viewHolder).checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return mAllContacts != null ? mAllContacts.size() : 0;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView initial, name;
        CheckBox checkBox;

        private ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.contactIcon);
            name = itemView.findViewById(R.id.displayName);
            checkBox = itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Item at position: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    Contacts contacts = mAllContacts.get(getAdapterPosition());
                    Log.d("Adapter", contacts.getId() + " " + contacts.getName() + " " + contacts.getPhNumber());

                    String[] projection = new String[]
                            {BaseColumns._ID,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER};


                    String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? ";
                    String[] selectionArgs = new String[]{String.valueOf(contacts.getId()), "1"};
                    Cursor cursorNumbers = mContext.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);

                    Log.d("ContactData", DatabaseUtils.dumpCursorToString(cursorNumbers));

                    if (cursorNumbers != null) {
                        final String[] uniqueNumbers;
                        boolean[] uniqueNumChecked;
                        cursorNumbers.moveToFirst();
                        while (!cursorNumbers.isAfterLast()) {
                            String phNum = cursorNumbers.getString(3).replace("-", "").replace(" ", "");

                            if (!phNumbers.contains(phNum)) {
                                phNumbers.add(phNum);
                            }
                            cursorNumbers.moveToNext();
                        }

                        uniqueNumbers = new String[phNumbers.size()];
                        uniqueNumChecked = new boolean[phNumbers.size()];

                        for (int i = 0; i < phNumbers.size(); i++) {
                            uniqueNumbers[i] = phNumbers.get(i);
                            uniqueNumChecked[i] = selectedPhNumbers.contains(phNumbers.get(i));
                        }

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
                            // Showing list of contacts to user upon item click

                            Dialog dialog;
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Select phone number:");
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

                                    isAnyItemSelected = false;
                                    for (String uniqueNumber : uniqueNumbers) {
                                        if (selectedPhNumbers.contains(uniqueNumber)) {
                                            isAnyItemSelected = true;
                                            break;
                                        }
                                    }

                                    if (isAnyItemSelected) {
                                        checkBox.setChecked(true);
                                        posClicked.add(getAdapterPosition());
                                    } else {
                                        checkBox.setChecked(false);
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
                }
            });
        }
    }
}
