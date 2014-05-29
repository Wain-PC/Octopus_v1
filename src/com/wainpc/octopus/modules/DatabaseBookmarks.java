package com.wainpc.octopus.modules;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wainpc.octopus.core.models.Bookmark;
import com.wainpc.octopus.core.models.Series;
 
public class DatabaseBookmarks extends SQLiteOpenHelper {
 
	private static String tag = "myLogs";
    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "octopus";
 
    public DatabaseBookmarks(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOKMARKS_TABLE = "CREATE TABLE IF NOT EXISTS bookmarks ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_ru VARCHAR(255), " +
                "title_en VARCHAR(255), " +
                "seriesId VARCHAR(10)," +
                "UNIQUE(seriesId) ON CONFLICT REPLACE)";
 
        // create books table
        db.execSQL(CREATE_BOOKMARKS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
 
        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------
 
    /**
     * CRUD operations (create "add", read "get", update, delete) bookmarks + get all bookmarks + delete all bookmarks
     */
 
    // Books table name
    private static final String TABLE_BOOKMARKS = "bookmarks";
 
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SERIES_ID = "seriesId";
    private static final String KEY_TITLE_RU = "title_ru";
    private static final String KEY_TITLE_EN = "title_en";

 
    public void addBookmark(Series series){
        Log.d(tag, "addSeries:"+ series.id);
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SERIES_ID, series.id); // get series id
        values.put(KEY_TITLE_RU, series.title_ru); // get series id
        values.put(KEY_TITLE_EN, series.title_en); // get series id
 
        // 3. insert
        db.insert(TABLE_BOOKMARKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
 
    // Get All Books
    public ArrayList<Bookmark> getAllBookmarks() {
        ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOOKMARKS;
        Bookmark bm;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, get seriesId and add it to the list
        if (cursor.moveToFirst()) {
            do {
            	bm = new Bookmark();
                bm.title_ru = cursor.getString(1);
                bm.title_en = cursor.getString(2);
                bm.seriesId = cursor.getString(3);
                // Add bookmark to the list
                bookmarks.add(bm);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBookmarks()", bookmarks.toString());
 
        // return books
        return bookmarks;
    }
    
    
    // Deleting single bookmark
    public Boolean isBookmarked(Series series) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String query = "SELECT  * FROM " + TABLE_BOOKMARKS +" WHERE seriesId = "+series.id;
        Cursor cursor = db.rawQuery(query, null);
        
        
        if (cursor != null && cursor.getCount() != 0) {
            db.close();
        	return true;
        }
        db.close();
        return false; 
    }
 
    // Deleting single bookmark
    public void deleteBookmark(String seriesId) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_BOOKMARKS,
                KEY_SERIES_ID+" = ?",
                new String[] { seriesId });
 
        // 3. close
        db.close();
 
        Log.d(tag, "deleteBookmark"+ seriesId);
 
    }
    
    public void deleteBookmark(Series series) {
    	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_BOOKMARKS,
                KEY_SERIES_ID+" = ?",
                new String[] { series.id });
 
        // 3. close
        db.close();
 
        Log.d(tag, "deleteBookmark"+ series.id);
    }
    
    public void deleteAllBookmarks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS,null,null);
        db.close();
        Log.d(tag, "deleteAllBookmarks");
    }
}
