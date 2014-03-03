package com.wainpc.octopus.asynctasks;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.util.Log;

import com.wainpc.octopus.interfaces.AsyncResponse;
import com.wainpc.octopus.modules.HttpLoader;
import com.wainpc.octopus.modules.JsonParser;


public class JsonLoader extends AsyncTask<String, String, String> {	
	public AsyncResponse delegate=null;
	public static String tag = "myLogs";
	
	@Override
	protected String doInBackground(String... inputURL) {
		Log.d(tag,"Sending request");
		return HttpLoader.sendQuery(inputURL);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ArrayList<HashMap<String, String>> seriesList = JsonParser.parse(result);
		delegate.success(seriesList);
	}
}
