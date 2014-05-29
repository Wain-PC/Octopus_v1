package com.wainpc.octopus.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.wainpc.octopus.R;
import com.wainpc.octopus.core.models.Bookmark;
import com.wainpc.octopus.core.models.HistoryItem;

public class BookmarkListAdapter extends BaseAdapter implements
		PinnedSectionListAdapter {

	private Activity activity;
	private ArrayList<Bookmark> data;
	private LayoutInflater inflater = null;
	private static final String tag = "myLogs";
	private Context context;

	public BookmarkListAdapter(Activity a,
			ArrayList<Bookmark> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context = activity.getApplicationContext();
	}

	public void refreshDataset(ArrayList<Bookmark> items) {
	    this.data = items;
	    notifyDataSetChanged();
	}

	public int getCount() {
		return data.size();
	}

	public Bookmark getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		Bookmark item = new Bookmark();
		ViewHolder holder;

		// no view, we have to create one
		if (vi == null) {
			holder = new ViewHolder();

			// it's a normal item
			if (getItemViewType(position) == 0) {
				vi = inflater.inflate(R.layout.simplelistadapter_item, null);
				holder.title = (TextView) vi.findViewById(R.id.title); // title
				vi.setTag(holder);

			} 
			//it's a header
			else if (getItemViewType(position) == 1) {
				vi = inflater.inflate(R.layout.serieslistadapter_header, null);
				holder.title = (TextView) vi.findViewById(R.id.title); // title
				vi.setBackgroundColor(parent.getResources().getColor(
						R.color.red));
			}
			// save row View for recycling
			vi.setTag(holder);
		}
		// view recycling
		else {
			holder = (ViewHolder) vi.getTag();
		}

		// get the item
		item = data.get(position);
		if (getItemViewType(position) == 0) {
			holder.title.setText(item.title_ru);	
		}
		else if (getItemViewType(position) == 1) {
			holder.title.setText(item.title_ru);
		}

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

	@Override
	public boolean isEnabled(int position) {
		return !isItemViewTypePinned(getItemViewType(position));
	}

	// -----------------------------------------------------------------------------
	// ViewHolder for this adapter is defined here

	public class ViewHolder {
		// fields are public for more performance
		public TextView title;
		public ImageView image;
	}

}
