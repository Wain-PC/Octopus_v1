package com.wainpc.octopus.modules;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

import com.wainpc.octopus.core.models.Series;

public class JsonParser {
	public static ArrayList<HashMap<String, String>> parseSeriesList(
			String jsonString) {
		try {

			String tag = "myLogs";
			Log.d(tag, "Parsing JSON!");

			HashMap<String, String> map = new HashMap<String, String>();
			ArrayList<HashMap<String, String>> seriesList = new ArrayList<HashMap<String, String>>();
			JSONParser parser = new JSONParser();
			JSONArray root = (JSONArray) parser.parse(jsonString);
			JSONObject jsonSeries, jObj;
			String title = "";
			JSONArray posters;
			String posterURL;
			Integer i = 0;
			Log.d(tag, "Size:" + root.size());
			for (i = 0; i < root.size(); i++) {
				try {
					map = new HashMap<String, String>();
					jsonSeries = (JSONObject) root.get(i);
					title = (String) jsonSeries.get("title_ru");
					posters = (JSONArray) jsonSeries.get("poster");
					if (posters.size() != 0) {
						jObj = (JSONObject) posters.get(0);
						posterURL = (String) jObj.get("url");
						map.put("posterURL", posterURL);
					}
					map.put("id",
							(String) Long.toString((Long) jsonSeries.get("id")));
					Log.d(tag, "Id:" + map.get("id"));
					map.put("title", title);
					seriesList.add(map);
				} catch (NullPointerException e) {
					Log.d(tag, "Null pointer for " + i);
					continue;
				} catch (IndexOutOfBoundsException e) {
					Log.d(tag, "Out of bounds for " + title);
					continue;
				}
			}

			return seriesList;
		} catch (ParseException e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return null;
		}
	}

	public static Series parseSeries(String jsonString) {
		try {
			Integer i;
			Series series = new Series();
			JSONParser parser = new JSONParser();
			JSONArray jArr;
			JSONObject jObj;
			JSONObject jsonSeries = (JSONObject) parser.parse(jsonString);

			series.title_ru = (String) jsonSeries.get("title_ru");
			series.title_en = (String) jsonSeries.get("title_en");
			series.description = (String) jsonSeries.get("description");
			series.poster = new ArrayList<String>();
			series.episodeList = getArrayListOfEpisodes(jsonSeries);
			jArr = (JSONArray) jsonSeries.get("poster");

			// getting posters
			for (i = 0; i < jArr.size(); i++) {
				try {
					jObj = (JSONObject) jArr.get(i);
					series.poster.add(jObj.get("url").toString());
				} catch (NullPointerException e) {
					Log.d("myLogs", "Null pointer for  " + i);
					continue;
				} catch (IndexOutOfBoundsException e) {
					Log.d("myLogs", "Out of bounds for " + i);
					continue;
				}
			}

			return series;
		} catch (ParseException e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return null;
		}

	}
	
	public static ArrayList<HashMap<String, String>> getArrayListOfEpisodes(JSONObject jsonSeries) {
		
		HashMap<String, String> map;
		ArrayList<HashMap<String, String>> episodeList = new ArrayList<HashMap<String, String>>();
		Integer i, j, k;
		String epTitle;
		JSONArray jSeason, jEpisode, jVideo;
		JSONObject jObj, jS, jE, jV;
		
		// getting season
					jSeason = (JSONArray) jsonSeries.get("season");
					//iterating over Seasons
					for (i = 0; i < jSeason.size(); i++) {
						jS = (JSONObject) jSeason.get(i);
						jEpisode = (JSONArray) jS.get("episode");
						//iterating over Episodes in a season
						for(j=0;j<jEpisode.size();j++) {
							map = new HashMap<String, String>();
							jE = (JSONObject) jEpisode.get(j);
							epTitle = (String) jE.get("title");
							if(epTitle == null || epTitle.length() == 0) epTitle = "Серия "+Long.toString((Long)jE.get("number"));
							map.put("seasonNumber", (String) jS.get("number").toString());
							map.put("episodeNumber", (String) jE.get("number").toString());
							map.put("title", epTitle);
							map.put("posterURL", (String) jE.get("thumbnail"));
							
							jVideo = (JSONArray) jE.get("video");
							for(k=0;k<jVideo.size();k++) {
								jV = (JSONObject) jVideo.get(k);
								map.put("url", (String) jV.get("url"));
								break;
							}
							//add episode to list
							episodeList.add(map);
						}
					}
		
		return episodeList;		
		}
}
