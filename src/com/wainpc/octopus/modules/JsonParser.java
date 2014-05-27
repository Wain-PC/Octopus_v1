package com.wainpc.octopus.modules;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Genre;
import com.wainpc.octopus.core.models.Series;

public class JsonParser {
	
	public static String tag = "myLogs";
	
	public static ArrayList<EpisodeItem> parseSeriesList(
			String jsonString) {
		return parseSeriesList(jsonString, "list");
		
	};
	
	public static ArrayList<EpisodeItem> parseSeriesList(
			String jsonString, String listName) {

		try {
			Log.d(tag, "Parsing JSON! "+listName);

			EpisodeItem map = new EpisodeItem();
			ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
			JSONParser parser = new JSONParser();
			JSONObject r = (JSONObject) parser.parse(jsonString);
			JSONArray root = (JSONArray) r.get(listName);
			JSONObject jsonSeries, jObj;
			String title = "";
			JSONArray posters;
			String posterURL;
			Integer i = 0;
			Log.d(tag, "Size:" + root.size());
			for (i = 0; i < root.size(); i++) {
				try {
					map = new EpisodeItem();
					jsonSeries = (JSONObject) root.get(i);
					title = (String) jsonSeries.get("title_ru");
					posters = (JSONArray) jsonSeries.get("poster");
					if (posters != null && posters.size() != 0) {
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
					Log.d(tag, "(1)Null pointer for " + i);
					continue;
				} catch (IndexOutOfBoundsException e) {
					Log.d(tag, "Out of bounds for " + title);
					continue;
				}
			}

			return seriesList;
		} catch (ParseException e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return new ArrayList<EpisodeItem>();
		}
		 catch (Exception e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return new ArrayList<EpisodeItem>();
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

			series.id = (String) Long.toString((Long) jsonSeries.get("id"));
			series.title_ru = (String) jsonSeries.get("title_ru");
			series.title_en = (String) jsonSeries.get("title_en");
			series.description = (String) jsonSeries.get("description");
			series.poster = new ArrayList<String>();
			series.episodeList = getArrayListOfEpisodes(jsonSeries);
			jArr = (JSONArray) jsonSeries.get("poster");
			
			if(jArr != null) {
				Log.d("myLogs","JARR:"+jArr);
			// getting posters
				for (i = 0; i < jArr.size(); i++) {
					try {
						jObj = (JSONObject) jArr.get(i);
						series.poster.add(jObj.get("url").toString());
					} catch (NullPointerException e) {
						Log.d("myLogs", "(2)Null pointer for " + i);
						continue;
					} catch (IndexOutOfBoundsException e) {
						Log.d("myLogs", "Out of bounds for " + i);
						continue;
					}
				}
			}

			return series;
		} catch (ParseException e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return null;
		}
		catch (Exception e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return null;
		}

	}
	
	public static ArrayList<EpisodeItem> getArrayListOfEpisodes(JSONObject jsonSeries) {
		
		EpisodeItem episode;
		ArrayList<EpisodeItem> episodeList = new ArrayList<EpisodeItem>();
		Integer i, j, k;
		String epTitle;
		JSONArray jSeason, jEpisode, jVideo;
		JSONObject  jS, jE, jV;
		
		// getting season
					jSeason = (JSONArray) jsonSeries.get("season");
					//iterating over Seasons
					for (i = 0; i < jSeason.size(); i++) {
						jS = (JSONObject) jSeason.get(i);
						
						//first, let's add season divider to the list
						episode = new EpisodeItem();
						episode.put("type", "se");
						episode.put("title", "Сезон "+(String) jS.get("number").toString());
						episode.put("seasonNumber", (String) jS.get("number").toString());
						episode.put("episodeNumber", "-1");
						episodeList.add(episode);
						
						jEpisode = (JSONArray) jS.get("episode");
						//iterating over Episodes in a season
						for(j=0;j<jEpisode.size();j++) {
							
							//now let's parse episodes
							episode = new EpisodeItem();
							jE = (JSONObject) jEpisode.get(j);
							epTitle = (String) jE.get("title");
							if(epTitle == null || epTitle.length() == 0) epTitle = "Серия "+Long.toString((Long)jE.get("number"));
							episode.put("seasonNumber", (String) jS.get("number").toString());
							episode.put("episodeNumber", (String) jE.get("number").toString());
							episode.put("title", epTitle);
							episode.put("posterURL", (String) jE.get("thumbnail"));
							//type is "episode"
							episode.put("type", "ep");
							
							jVideo = (JSONArray) jE.get("video");
							for(k=0;k<jVideo.size();k++) {
								jV = (JSONObject) jVideo.get(k);
								episode.put("url", (String) jV.get("url"));
								episode.put("videoType", (String) jV.get("type"));
								break; //dead code warning is caused by this string. This only gets the first video from the list (temp solution) 
							}
							//add episode to list
							episodeList.add(episode);
						}
					}
		
		return episodeList;		
		}

	public static ArrayList<Genre> parseGenreList(String jsonString) {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Log.d(tag,"Parsing genres list");
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject r = (JSONObject) parser.parse(jsonString);
			JSONArray root = (JSONArray) r.get("genres"); 
			JSONObject jGenre = null;
			Genre genre = null;
			String title_ru, title_en;
			String id;
			
			Integer i = 0;
			Log.d(tag,"NUM genres:"+root.size());
			for (i = 0; i < root.size(); i++) {
				try {
				genre = new Genre();
				jGenre = (JSONObject) root.get(i);
				title_ru = (String) jGenre.get("title_ru").toString();
				title_en = (String) jGenre.get("title_en").toString();
				id = (String) jGenre.get("id").toString();
				genre.id = id;
				genre.title_ru = title_ru;
				genre.title_en = title_en;
				//
				genreList.add(genre);
				} catch (NullPointerException e) {
					Log.d(tag, "(1)Null pointer for " + i);
					continue;
				} catch (IndexOutOfBoundsException e) {
					Log.d(tag, "Out of bounds for " + i);
					continue;
				}
			}
			Log.d(tag,"Returning genreList with items:"+genreList.size());
			return genreList;
		} catch (ParseException e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return genreList;
		}
		catch (Exception e) {
			Log.d("myLogs", "Parsing JSON ERROR!" + e);
			return genreList;
		}
	}
}
