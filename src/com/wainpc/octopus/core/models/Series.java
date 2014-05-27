package com.wainpc.octopus.core.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Series {
	public String id;
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
	public ArrayList<EpisodeItem> episodeList;
	
	
	public ArrayList<EpisodeItem> getListArrayOfEpisodes() {
		return episodeList;	
	}
};
