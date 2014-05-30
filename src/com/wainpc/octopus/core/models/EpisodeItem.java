package com.wainpc.octopus.core.models;

import java.util.ArrayList;
import java.util.HashMap;

public class EpisodeItem extends HashMap<String, String> {
	
	public ArrayList<Video> video;
	
	public EpisodeItem() {
		video = new ArrayList<Video>();
	}

	public String getSeasonNumber() {
		String sn = this.get("seasonNumber");
		if(sn != null) {
			return sn;
		}
		return "0";
	}
	
	public String getEpisodeNumber() {
		String sn = this.get("episodeNumber");
		if(sn != null) {
			return sn;
		}
		return "0";
	}
}
