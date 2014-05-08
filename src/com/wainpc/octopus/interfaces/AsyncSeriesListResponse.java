package com.wainpc.octopus.interfaces;

import java.util.ArrayList;

import com.wainpc.octopus.core.models.EpisodeItem;

public interface AsyncSeriesListResponse {
	void onLoadItemsSuccess(ArrayList<EpisodeItem> out);
}
