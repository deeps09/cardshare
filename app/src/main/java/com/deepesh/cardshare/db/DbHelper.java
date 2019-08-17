package com.deepesh.cardshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.deepesh.cardshare.models.CardItem;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "cardsdb";
    private static final String TABLE_CARDS = "cards";
    private static final String COL_ID = "id";
    private static final String COL_TEXT1 = "text1";
    private static final String COL_TEXT1X = "text1x";
    private static final String COL_TEXT1Y = "text1y";
    private static final String COL_TEXT2 = "text2";
    private static final String COL_TEXT2X = "text2x";
    private static final String COL_TEXT2Y = "text2y";
    private static final String COL_TEXT3 = "text3";
    private static final String COL_TEXT3X = "text3x";
    private static final String COL_TEXT3Y = "text3y";
    private static final String COL_SHARED_WITH = "shared_with";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CARDS + "(" +
                COL_ID + " INTEGER PRIMARY KEY," +
                COL_TEXT1 + " TEXT," +
                COL_TEXT1X + " INTEGER," +
                COL_TEXT1Y + " INTEGER," +
                COL_TEXT2 + " TEXT," +
                COL_TEXT2X + " INTEGER," +
                COL_TEXT2Y + " INTEGER," +
                COL_TEXT3 + " TEXT," +
                COL_TEXT3X + " INTEGER," +
                COL_TEXT3Y + " INTEGER," +
                COL_SHARED_WITH + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

    public void insertUpdateCard(CardItem cardItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_ID, 1);
        cv.put(COL_TEXT1, cardItem.getText1());
        cv.put(COL_TEXT1X, cardItem.getText1x());
        cv.put(COL_TEXT1Y, cardItem.getText1y());
        cv.put(COL_TEXT2, cardItem.getText2());
        cv.put(COL_TEXT2X, cardItem.getText2x());
        cv.put(COL_TEXT2Y, cardItem.getText2y());
        cv.put(COL_TEXT3, cardItem.getText3());
        cv.put(COL_TEXT3X, cardItem.getText3x());
        cv.put(COL_TEXT3Y, cardItem.getText3y());
        cv.put(COL_SHARED_WITH, cardItem.getGuestList());

        Cursor cursor = db.rawQuery("Select * from " + TABLE_CARDS, null);

        if (cursor.getCount() < 1)
            db.insert(TABLE_CARDS, null, cv);
        else
            db.update(TABLE_CARDS, cv, null, null);

        cursor.close();
        db.close();
    }

    public CardItem getCard() {

        String selectQuery = "SELECT  * FROM " + TABLE_CARDS + " WHERE " + COL_ID + " = 1";
       // ArrayList<CardItem> cardItems = new ArrayList<>();
        CardItem cardItem = null;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
                 cardItem = new CardItem(
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        cursor.getString(10));

               // cardItems.add(items);
            //}
            cursor.close();
        }
        return cardItem;
    }


}
