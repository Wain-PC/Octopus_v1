package com.wainpc.octopus.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.wainpc.octopus.interfaces.AsyncSeriesItemResponse;
import com.wainpc.octopus.modules.HttpLoader;

public class RightUrlVideoLoader extends AsyncTask<String, String, String> {
	public static String tag = "myLogs";
	public AsyncSeriesItemResponse delegate=null;
	String url;
	@Override
	protected String doInBackground(String... inputUrl) {
		return HttpLoader.sendQuery(inputUrl);
		
	}	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d(tag,"url======"+result);
		delegate.onGetRightUrlVideo(result);
		
	}

}
