package com.wainpc.octopus.core.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Series {
	public String title_ru; 
	public String title_en; 
	public Integer imdbid; 
	public Integer kpid; 
	public Integer thetvdbid; 
	public Integer tvrageid;
	public String status;
	public String description;
	public ArrayList<String> poster;
	public ArrayList<Season> season;
	//added for comfort
	public ArrayList<HashMap<String,String>> episodeList;
	
	
	public ArrayList<HashMap<String,String>> getListArrayOfEpisodes() {
		return episodeList;	
	}
};
