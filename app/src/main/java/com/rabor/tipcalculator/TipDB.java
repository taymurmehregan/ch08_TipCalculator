package com.rabor.tipcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TipDB {
    //database constant variables
    private static final String DB_NAME = "tipDB";
    private static final int DB_VERSION = 1;

    private static final String TIP_TABLE = "tips_table";

    private static final String TIP_ID = "tip_id";
    private static final int TIP_ID_COL = 0;

    private static final String TIP_PERCENT = "tip_percent";
    private static final int TIP_PERCENT_COL = 1;

    private static final String TIP_BILL_AMOUNT = "bill_amount";
    private static final int TIP_BILL_AMOUNT_COL = 2;

    private static final String TIP_BILL_DATE = "bill_date";
    private static final int TIP_BILL_DATE_COL = 3;

    //create table
    public static final String CREATE_TIP_TABLE =
            "CREATE TABLE " + TIP_TABLE + " (" + TIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TIP_PERCENT + " REAL NOT NULL, " + TIP_BILL_AMOUNT + " REAL NOT NULL, " +
                    TIP_BILL_DATE + " INTEGER);";

    //drop table
    public static final String DROP_TIP_TABLE =
            "DROP TABLE IF EXISTS " + TIP_TABLE;

    //database object
    private SQLiteDatabase database;

    //database helper object
    private DBHelper dbHelper;

    public TipDB(Context context){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    //open database to read from
    private void openReadableDB(){
        database = dbHelper.getReadableDatabase();
    }

    //open database to write to
    private void openWriteableDB(){
        database = dbHelper.getWritableDatabase();
    }

    //close database connection
    private void closeDB(){
        if(database != null)
            database.close();
    }

    private static Tip getTipFromCursor(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        else{
            try{
                Tip tip = new Tip(
                    cursor.getInt(TIP_ID_COL),
                    cursor.getDouble(TIP_PERCENT_COL),
                    cursor.getDouble(TIP_BILL_AMOUNT_COL),
                    cursor.getInt(TIP_BILL_DATE_COL));

                return tip;
            }
            catch(Exception e){
                return null;
            }
        }
    }

    public ArrayList<Tip> getTips(){
        ArrayList<Tip> tipList = new ArrayList<Tip>();

        Tip tipInfo;

        this.openReadableDB();
        Cursor cursor = database.query(TIP_TABLE, null, null, null, null, null, null);

        while(cursor.moveToNext()){
            tipList.add(getTipFromCursor(cursor));
        }
        if(cursor != null)
            cursor.close();

        this.closeDB();

        return tipList;
    }

    public void saveTip(Tip tip){
        this.openWriteableDB();

        database.execSQL("INSERT INTO tips_table(tip_percent, bill_amount, bill_date) VALUES(" + tip.getTip_percent() +
                ", " + tip.getBill_amount() + ", 0)");

        this.closeDB();
    }

    public float getAverageTipPercent(){
        float ave = 0.0f;
        float total = 0.0f;

        ArrayList<Tip> tipList = getTips();

        for(Tip tip : tipList){
            total += tip.getTip_percent();
        }

        ave = total / tipList.size();
        return ave;
    }

    public Cursor queryTips(String[] cols, String where, String[] whereArgs, String orderBy){
        this.openReadableDB();
        return database.query(TIP_TABLE, cols, where, whereArgs, null, null, orderBy);
    }

    public long insertTip(ContentValues values){
       this.openWriteableDB();
       return database.insert(TIP_TABLE, null, values);
    }

    public int deleteTip(String where, String[] whereArgs){
        this.openWriteableDB();
        return database.delete(TIP_TABLE, where, whereArgs);
    }

    //database helper class
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL(DROP_TIP_TABLE);
            db.execSQL(CREATE_TIP_TABLE);

            //db.execSQL("INSERT INTO tips_table(tip_percent, bill_amount, bill_date) VALUES(.15, 45.67, 0)");
            //db.execSQL("INSERT INTO tips_table(tip_percent, bill_amount, bill_date) VALUES(.25, 68.90, 0)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int old, int newDb) {
            Log.d("Tip Database", "Upgrading database from version " + old + " to version " + newDb);

            db.execSQL(DROP_TIP_TABLE);
            onCreate(db);
        }
    }
}
