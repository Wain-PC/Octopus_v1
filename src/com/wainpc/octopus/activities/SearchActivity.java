package com.wainpc.octopus.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SearchActivity extends Activity {
	public static String tag = "myLogs";
	
	protected void onCreate(Bundle savedInstanceState){
		String result="такого сериала нет =(";
		Intent intent=getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query=intent.getStringExtra(SearchManager.QUERY);
			Log.d(tag,"quer="+query);
			
		}
	}
}
