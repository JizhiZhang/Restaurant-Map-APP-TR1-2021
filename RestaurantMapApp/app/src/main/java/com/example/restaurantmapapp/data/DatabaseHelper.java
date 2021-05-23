package com.example.restaurantmapapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.restaurantmapapp.model.Location;
import com.example.restaurantmapapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "("
                + Util.LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.LOCATION_NAME + " TEXT, "
                + Util.LATITUDE + " TEXT, "
                + Util.LONGITUDE + " TEXT" + ")";
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_LOCATION_TABLE = "DROP TABLE IF EXISTS " + Util.TABLE_NAME;
        db.execSQL(DROP_LOCATION_TABLE);
        onCreate(db);
    }

    public long insertLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.LOCATION_NAME, location.getName());
        contentValues.put(Util.LATITUDE, location.getLatitude());
        contentValues.put(Util.LONGITUDE, location.getLongitude());
        long newRowId = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }

    public int fetchLocation(String name, String latitude, String longitude) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME, new String[]{Util.LOCATION_ID}, Util.LOCATION_NAME + "=? and " + Util.LATITUDE + "=? and " + Util.LONGITUDE + "=?",
                new String[]{name, latitude, longitude}, null, null, null);

        if (cursor.moveToFirst()) {
            db.close();
            return cursor.getInt(cursor.getColumnIndex(Util.LOCATION_ID));
        } else {
            db.close();
            return -1;
        }
    }

    public List<Location> fetchAllLocation() {
        List<Location> locationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = " SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setId(cursor.getInt(cursor.getColumnIndex(Util.LOCATION_ID)));
                location.setName(cursor.getString(cursor.getColumnIndex(Util.LOCATION_NAME)));
                location.setLatitude(cursor.getString(cursor.getColumnIndex(Util.LATITUDE)));
                location.setLongitude(cursor.getString(cursor.getColumnIndex(Util.LONGITUDE)));
                locationList.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return locationList;
    }

}
