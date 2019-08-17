package com.deepesh.cardshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.deepesh.cardshare.models.CardItem;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cardsdb";
    private static final String TABLE_CARDS = "cards";
    private static final String COL_ID = "id";
    private static final String COL_PERSON1 = "person1";
    private static final String COL_PERSON1X = "person1x";
    private static final String COL_PERSON1Y = "person1y";
    private static final String COL_PERSON2 = "person2";
    private static final String COL_PERSON2X = "person2x";
    private static final String COL_PERSON2Y = "person2y";
    private static final String COL_CONJ = "conj";
    private static final String COL_CONJX = "conjx";
    private static final String COL_CONJY = "conjy";
    private static final String COL_MESSAGE = "message";
    private static final String COL_MESSAGEX = "messagex";
    private static final String COL_MESSAGEY = "messagey";
    private static final String COL_GUEST_LIST = "guestlist";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CARDS + "(" +
                COL_ID + " INTEGER PRIMARY KEY," +
                COL_PERSON1 + " TEXT," +
                COL_PERSON1X + " INTEGER," +
                COL_PERSON1Y + " INTEGER," +
                COL_PERSON2 + " TEXT," +
                COL_PERSON2X + " INTEGER," +
                COL_PERSON2Y + " INTEGER," +
                COL_CONJ + " TEXT," +
                COL_CONJX + " INTEGER," +
                COL_CONJY + " INTEGER," +
                COL_MESSAGE + " TEXT," +
                COL_MESSAGEX + " INTEGER," +
                COL_MESSAGEY + " INTEGER," +
                COL_GUEST_LIST + " TEXT" + ")";
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
        cv.put(COL_PERSON1, cardItem.getPerson1());
        cv.put(COL_PERSON1X, cardItem.getPerson1x());
        cv.put(COL_PERSON1Y, cardItem.getPerson1y());
        cv.put(COL_PERSON2, cardItem.getPerson2());
        cv.put(COL_PERSON2X, cardItem.getPerson2x());
        cv.put(COL_PERSON2Y, cardItem.getPerson2y());
        cv.put(COL_CONJ, cardItem.getConjunction());
        cv.put(COL_CONJX, cardItem.getConjX());
        cv.put(COL_CONJY, cardItem.getConjY());
        cv.put(COL_MESSAGE, cardItem.getMessage());
        cv.put(COL_MESSAGEX, cardItem.getMessageX());
        cv.put(COL_MESSAGEY, cardItem.getMessageY());
        cv.put(COL_GUEST_LIST, cardItem.getGuestList());

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
                         cursor.getString(10),
                         cursor.getInt(11),
                         cursor.getInt(12),
                        cursor.getString(13));

               // cardItems.add(items);
            //}
            cursor.close();
        }
        db.close();
        return cardItem;
    }
}
