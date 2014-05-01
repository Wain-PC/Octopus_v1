package com.wainpc.octopus.interfaces;


import com.wainpc.octopus.core.models.Series;

public interface AsyncSeriesItemResponse {
	void onLoadSeriesSuccess(Series S);
	void onGetRightUrlVideo(String str);
}
