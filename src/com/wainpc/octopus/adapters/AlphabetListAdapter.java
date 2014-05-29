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

public class AlphabetListAdapter extends BaseAdapter implements
		PinnedSectionListAdapter {

	private Activity activity;
	private String[] data;
	private LayoutInflater inflater = null;
	private static final String tag = "myLogs";

	public AlphabetListAdapter(Activity a, String[] letters) {
		activity = a;
		// get list of episodes from series
		data = letters;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
		return data.length;
	}

	public String getItem(int position) {
		return data[position];
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

				vi = inflater.inflate(R.layout.simplelistadapter_item, null);
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
		String letter = getItem(position);
		holder.title.setText(letter.toUpperCase());	

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
