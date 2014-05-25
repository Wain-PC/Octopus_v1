package com.wainpc.octopus.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.SeriesListAdapter;
import com.wainpc.octopus.asynctasks.JsonSeriesListLoader;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;
import com.wainpc.octopus.modules.HttpLoader;

public class SearchActivity extends Activity implements AsyncSeriesListResponse {
	public static String tag = "myLogs";
	private static ImageLoader him;
	public String rootURL = "http://173.44.34.162:1337/search?json=1&q=";
	public JsonSeriesListLoader loader = new JsonSeriesListLoader();
	public static ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list);
		// setContentView(R.layout.
	
		Intent intent = getIntent();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		him = ImageLoader.getInstance();

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// String query=intent.getExtras().getString("query");
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.d(tag, "query=" + query);
			loader.execute(rootURL + HttpLoader.encodeURIComponent(query));
			loader.delegateList = this;
			setTitle(query);
		}

	}

	@Override
	public void onLoadItemsSuccess(ArrayList<EpisodeItem> out) {
		
		SeriesListAdapter listAdapter;
		ListView listViewFragmentMain = (ListView) findViewById(R.id.listView_fragment_main);
		
		seriesList=out;

		listAdapter = new SeriesListAdapter(this, him, seriesList);
		listViewFragmentMain.setAdapter(listAdapter);

		listViewFragmentMain.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String seriesId = seriesList.get(position).get("id").toString();
				String seriesTitle = seriesList.get(position).get("title")
						.toString();

				// add to db
				/*
				 * SQLiteDatabase db = localDB.getWritableDatabase();
				 * ContentValues newValues = new ContentValues();
				 * newValues.put("SERIES_ID", seriesId);
				 * newValues.put("SERIES_NAME", seriesTitle); long rowID
				 * =db.insert("bookmark", null, newValues);
				 * Log.d(tag,"db_id "+rowID); //////////////////
				 */

				// open series page
				Intent seriesPage = new Intent(getApplicationContext(),
						SeriesActivity.class);
				seriesPage.putExtra("id", seriesId);
				seriesPage.putExtra("title", seriesTitle);
				startActivity(seriesPage);
			}
		});

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


}
