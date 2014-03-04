package com.wainpc.octopus.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.wainpc.octopus.core.models.Series;
import com.wainpc.octopus.interfaces.AsyncSeriesItemResponse;
import com.wainpc.octopus.modules.HttpLoader;
import com.wainpc.octopus.modules.JsonParser;


public class JsonSeriesItemLoader extends AsyncTask<String, String, String> {	
	public AsyncSeriesItemResponse delegate=null;
	public static String tag = "myLogs";
	
	@Override
	protected String doInBackground(String... inputURL) {
		Log.d(tag,"Sending request");
		return HttpLoader.sendQuery(inputURL);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Series series = JsonParser.parseSeries(result);
		delegate.onLoadSeriesSuccess(series);
	}
}
