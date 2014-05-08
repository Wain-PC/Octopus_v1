package com.wainpc.octopus.interfaces;

import java.util.ArrayList;

import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Genre;

public interface MainActivityInterface {
	void onLoadLatestSeriesSuccess(ArrayList<EpisodeItem> out);
	void onLoadGenresListSuccess(ArrayList<Genre> out);
}
