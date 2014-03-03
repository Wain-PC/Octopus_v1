package com.wainpc.octopus.modules;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

import com.wainpc.octopus.core.Series;

public class JsonParser {
	public static ArrayList<HashMap<String, String>> parse(String jsonString) {
		try {
		
			String tag = "myLogs";
			Log.d(tag,"Parsing JSON!");
			
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> seriesList = new ArrayList<HashMap<String, String>>();
		JSONParser parser = new JSONParser();
		JSONArray root = (JSONArray) parser.parse(jsonString);
		JSONObject jsonSeries;
		String title;
		JSONArray posters;
		String posterURL;
		Series series = new Series();
		Integer i = 0;
		Log.d(tag,"Size:"+root.size());
		for(i = 0; i< root.size(); i++) {
			map = new HashMap<String, String>();
			jsonSeries = (JSONObject) root.get(i);
			title = (String) jsonSeries.get("title_ru");
			//posters = (JSONArray) jsonSeries.get("posters");
			//posterURL = (String) posters.get(0);
			Log.d(tag,"Size:"+root.size());
			map.put("title",title);
			//map.put("posterURL",posterURL);
			seriesList.add(map);
		}
		
		return seriesList;
	}
		catch(ParseException e) {
			Log.d("myLogs","Parsing JSON ERROR!"+e);
			return null;
		}
	}
}
