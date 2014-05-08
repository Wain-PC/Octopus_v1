package com.wainpc.octopus.core.models;

import java.util.HashMap;

public class EpisodeItem extends HashMap<String, String> {

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
