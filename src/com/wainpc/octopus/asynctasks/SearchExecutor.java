package com.wainpc.octopus.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Genre;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;
import com.wainpc.octopus.modules.HttpLoader;
import com.wainpc.octopus.modules.JsonParser;


public class SearchExecutor extends AsyncTask<String, String, String> {	
	public AsyncSeriesListResponse delegate=null;
	public static String tag = "myLogs";
	
	@Override
	protected String doInBackground(String... inputURL) { 
		return HttpLoader.sendQuery(inputURL);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
		//rewrite using normal error system!
		if(result != "ERR") {
			seriesList = JsonParser.parseSeriesList(result,"series");			
		}
		delegate.onLoadItemsSuccess(seriesList);
	}
}
