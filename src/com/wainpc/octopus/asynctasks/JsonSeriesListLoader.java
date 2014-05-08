package com.wainpc.octopus.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;
import com.wainpc.octopus.interfaces.MainActivityInterface;
import com.wainpc.octopus.modules.HttpLoader;
import com.wainpc.octopus.modules.JsonParser;


public class JsonSeriesListLoader extends AsyncTask<String, String, String> {	
	public MainActivityInterface delegate=null;
	public AsyncSeriesListResponse delegateList=null;
	public static String tag = "myLogs";
	
	@Override
	protected String doInBackground(String... inputURL) {
		Log.d(tag,"Sending request"); 
		return HttpLoader.sendQuery(inputURL);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
		//rewrite using normal error system!
		if(result != "ERR") {
			seriesList = JsonParser.parseSeriesList(result);			
		}
		if(delegate != null) delegate.onLoadLatestSeriesSuccess(seriesList);
		if(delegateList != null) delegateList.onLoadItemsSuccess(seriesList);
	}
}
