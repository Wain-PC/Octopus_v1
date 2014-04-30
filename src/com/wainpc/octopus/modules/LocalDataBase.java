package com.wainpc.octopus.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDataBase extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "bookmark";
	public String SERIES_NAME;
	public String ERIES_ID; // ID= KPID+IMDB
	public static String tag = "myLogs";
	
	public LocalDataBase(Context context) {
		super(context, "MyDB", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d(tag,"CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, SERIES_ID INTEGER, SERIES_NAME TEXT)");
		db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, SERIES_ID INTEGER, SERIES_NAME TEXT);"); 
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
