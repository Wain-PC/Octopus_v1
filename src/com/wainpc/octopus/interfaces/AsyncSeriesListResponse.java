package com.wainpc.octopus.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import com.wainpc.octopus.core.models.Series;

public interface AsyncSeriesListResponse {
	void onLoadItemsSuccess(ArrayList<HashMap<String, String>> out);
}
