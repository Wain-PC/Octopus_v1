package com.wainpc.octopus.modules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.HistoryItem;
import com.wainpc.octopus.core.models.Series;
 
public class DatabaseHistory extends SQLiteOpenHelper {
 
	private static String tag = "myLogs";
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "octopus";    
    private static final String TABLE_NAME = "history";
 
    public DatabaseHistory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d(tag,"CREATING HISTORY TABLE OF DB!"+TABLE_NAME);
        String createTableSQL = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title_ru VARCHAR(255), " +
                "title_en VARCHAR(255), " +
                "seriesId VARCHAR(10)," +
                "seasonNumber VARCHAR(10)," +
                "episodeNumber VARCHAR(10)," +
                "datetime DATETIME)";
 
        db.execSQL(createTableSQL);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.d(tag,"UPGRADING HISTORY DB! "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

 
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SERIES_ID = "seriesId";
    private static final String KEY_TITLE_RU = "title_ru";
    private static final String KEY_TITLE_EN = "title_en";
    private static final String KEY_SEASON_NUMBER = "seasonNumber";
    private static final String KEY_EPISODE_NUMBER = "episodeNumber";
    private static final String KEY_DATETIME = "datetime";

 
    public void addHistoryItem(Series series, EpisodeItem episode){
        Log.d(tag, "addEpisode to History:"+ episode.getSeasonNumber()+"x"+episode.getEpisodeNumber());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SERIES_ID, series.id);
        values.put(KEY_TITLE_RU, series.title_ru);
        values.put(KEY_TITLE_EN, series.title_en);
        values.put(KEY_SEASON_NUMBER, episode.getSeasonNumber());
        values.put(KEY_EPISODE_NUMBER, episode.getEpisodeNumber());
        values.put(KEY_DATETIME, getNowDateTime());
 
        // 3. insert
        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
 
    // Get All Books
    public ArrayList<HistoryItem> getAllHistory() {
        ArrayList<HistoryItem> historyList = new ArrayList<HistoryItem>();
 
        String currDate = " ",
        	   itemDate = " ";
        HistoryItem item;
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_NAME +" ORDER BY "+KEY_ID+" DESC";
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row
        if (cursor.moveToFirst()) {
            do {
            	item = getItemFromCursor(cursor);
            	itemDate = getDateString(item.datetime);
            	Log.d(tag, item.seasonNumber);
            	//another date found, we should add a header
            	if(!itemDate.equals(currDate)) {
            		currDate = itemDate;
            		historyList.add(getHistoryItemHeader(currDate));
            	}
                historyList.add(item);
            } while (cursor.moveToNext());
        }
        Log.d("getAllHistoryItems()", historyList.toString());
 
        // return books
        return historyList;
    }
    
    //get a list of episodes in history for each show
    public ArrayList<HistoryItem> getWatchedEpisodes(Series series) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HistoryItem> historyList = new ArrayList<HistoryItem>();
        String query = "SELECT * FROM " + TABLE_NAME +" WHERE "+ KEY_SERIES_ID +" = "+series.id;
        Cursor cursor = db.rawQuery(query, null);
                
        if (cursor.moveToFirst()) {
            do {           	
                historyList.add(getItemFromCursor(cursor));
            } while (cursor.moveToNext());
        }      
        db.close();
        return historyList; 
    }
    
    
    public void deleteHistoryItem(Series series, EpisodeItem episode) {
    	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        String where = KEY_SERIES_ID + " = ? AND "
        		+ KEY_SEASON_NUMBER + " = ? AND "
        		+ KEY_EPISODE_NUMBER + " = ?";
        
        db.delete(TABLE_NAME,where,
                new String[] { series.id, episode.getSeasonNumber(), episode.getEpisodeNumber() });
 
        db.close();
 
        Log.d(tag, "deleteHistoryItem "+ series.id+" : "+episode.getSeasonNumber()+"x"+episode.getEpisodeNumber());
    }
    
    
    public void deleteHistoryForSeries(Series series) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_SERIES_ID + " = ?";    
        db.delete(TABLE_NAME,where,
                new String[] { series.id});
        db.close();
        Log.d(tag, "deleteHistoryForSeries "+ series.id);
    }
    
    public void deleteAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();  
        db.delete(TABLE_NAME, null, null);
        db.close();
        Log.d(tag, "deleteAllHistory");
    }
    
    //----------Some helpful shortcuts
    
    private HistoryItem getItemFromCursor(Cursor cursor) {
    	HistoryItem hi = new HistoryItem();
        hi.title_ru = cursor.getString(1);
        hi.title_en = cursor.getString(2);
        hi.seriesId = cursor.getString(3);
        hi.seasonNumber = cursor.getString(4);
        hi.episodeNumber = cursor.getString(5);
        hi.datetime = setDateFromString(cursor.getString(6));
        hi.put("type", "item");
        return hi;
    }
    
    private HistoryItem getHistoryItemHeader(String date) {
    	HistoryItem hi = new HistoryItem();
        hi.seriesId = "-1";
        hi.title_ru = date;
        hi.title_en = date;
        hi.seasonNumber = "-1";
        hi.episodeNumber = "-1";
        hi.put("type", "header");
        return hi;
    }
    
    private String getNowDateTime(){
        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    private Date setDateFromString(String input){
    	try {
        	Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input);
            return date;	
    	}
    	catch(Exception e) {
    		return new Date();
    	}
    }
    
    
    private String getDateString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLLL yyyy",Locale.getDefault());
        return dateFormat.format(date);
    }
}
