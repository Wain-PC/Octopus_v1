package com.wainpc.octopus.asynctasks;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.util.Log;

import com.wainpc.octopus.core.models.Genre;
import com.wainpc.octopus.interfaces.MainActivityInterface;
import com.wainpc.octopus.modules.HttpLoader;
import com.wainpc.octopus.modules.JsonParser;


public class GenresListLoader extends AsyncTask<String, String, String> {	
	public MainActivityInterface delegate=null;
	public static String tag = "myLogs";
	
	@Override
	protected String doInBackground(String... inputURL) { 
		return HttpLoader.sendQuery(inputURL);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		//rewrite using normal error system!
		if(result != "ERR") {
			genreList = JsonParser.parseGenreList(result);			
		}
		delegate.onLoadGenresListSuccess(genreList);
	}
}
