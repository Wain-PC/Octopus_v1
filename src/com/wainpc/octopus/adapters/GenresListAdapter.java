package com.wainpc.octopus.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.wainpc.octopus.R;
import com.wainpc.octopus.core.models.Genre;

public class GenresListAdapter extends BaseAdapter implements
		PinnedSectionListAdapter {

	private Activity activity;
	private ArrayList<Genre> data;
	private LayoutInflater inflater = null;
	private static final String tag = "myLogs";



	public GenresListAdapter(Activity a, ArrayList<Genre> genres) {
		activity = a;
		// get list of episodes from series
		data = genres;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
		return data.size();
	}

	public Genre getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		ViewHolder holder;

		// no view, we have to create one
		if (vi == null) {
			holder = new ViewHolder();

				vi = inflater.inflate(R.layout.serieslistadapter_header, null);
				holder.title = (TextView) vi.findViewById(R.id.title); // title
				vi.setTag(holder);

			// save row View for recycling
			vi.setTag(holder);
		}
		// view recycling
		else {
			holder = (ViewHolder) vi.getTag();
		}

		// get the item
		Genre item = getItem(position);
		holder.title.setText(item.title_ru);	

		return vi;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == 1;
	}

	// -----------------------------------------------------------------------------
	// ViewHolder for this adapter is defined here

	public class ViewHolder {
		// fields are public for more performance
		public TextView title;
	}

}
