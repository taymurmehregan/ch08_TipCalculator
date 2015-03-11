package com.rabor.tipcalculator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class TipProvider extends ContentProvider {
    public static final String AUTHORITY = "com.murach.tiplist.provider";

    public static final int NO_MATCH =- -1;
    public static final int ALL_TIPS_URI = 0;
    public static final int SINGLE_TIPS_URI = 1;

    private TipDB database;
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        database = new TipDB(getContext());

        matcher = new UriMatcher(NO_MATCH);
        matcher.addURI(AUTHORITY, "tips", ALL_TIPS_URI);
        matcher.addURI(AUTHORITY, "tips/#", SINGLE_TIPS_URI);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] cols, String where, String[] whereArgs, String orderBy) {
        switch(matcher.match(uri)){
            case ALL_TIPS_URI:
                return database.queryTips(cols, where, whereArgs, orderBy);
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch(matcher.match(uri)){
            case ALL_TIPS_URI:
                return "vnd.android.cursor.dir/vnd.murach.tiplist.tips";
            case SINGLE_TIPS_URI:
                return "vnd.android.cursor.item/vnd.murach.tiplist.tips";
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(matcher.match(uri)){
            case ALL_TIPS_URI:
                long insertId = database.insertTip(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return uri.buildUpon().appendPath(Long.toString(insertId)).build();
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
       int deleteCount;
        switch(matcher.match(uri)){
            case SINGLE_TIPS_URI:
                String tipId = uri.getLastPathSegment();
                String where2 = "tip_id = ?";
                String[] whereArgs2 = {tipId};
                deleteCount = database.deleteTip(where2, whereArgs2);
                getContext().getContentResolver().notifyChange(uri, null);
                return deleteCount;
            case ALL_TIPS_URI:
                deleteCount = database.deleteTip(where, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return deleteCount;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String s, String[] strings) {
        switch(matcher.match(uri)){
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }
}
