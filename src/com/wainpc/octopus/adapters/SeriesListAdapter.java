package com.wainpc.octopus.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Series;

public class SeriesListAdapter extends BaseAdapter implements
		PinnedSectionListAdapter {

	private Activity activity;
	private ArrayList<EpisodeItem> data;
	private LayoutInflater inflater = null;
	private static final String tag = "myLogs";
	private static ImageLoader him;
	private Context context;

	public SeriesListAdapter(Activity a, ImageLoader im,
			ArrayList<EpisodeItem> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context = activity.getApplicationContext();
		him = im;
	}

	public SeriesListAdapter(Activity a, ImageLoader im, Series s) {
		activity = a;
		// get list of episodes from series
		data = s.getListArrayOfEpisodes();
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context = activity.getApplicationContext();
		him = im;
	}

	public int getCount() {
		return data.size();
	}

	public HashMap<String, String> getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View vi, ViewGroup parent) {
		HashMap<String, String> item = new HashMap<String, String>();
		ViewHolder holder;

		// no view, we have to create one
		if (vi == null) {
			holder = new ViewHolder();

			// it's a normal item
			if (getItemViewType(position) == 0) {
				vi = inflater.inflate(R.layout.serieslistadapter_item, null);
				holder.title = (TextView) vi.findViewById(R.id.title); // title
				holder.image = (ImageView) vi.findViewById(R.id.image); // thumb image
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
			holder.title.setText(item.get("title"));
			him.displayImage(item.get("posterURL"), holder.image);	
		}
		else if (getItemViewType(position) == 1) {
			holder.title.setText(item.get("title"));
		}

		return vi;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		String type = getItem(position).get("type");
		int typeN = 0;
		if (type == "ep")
			typeN = 0;
		else if (type == "se")
			typeN = 1;
		return typeN;
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
