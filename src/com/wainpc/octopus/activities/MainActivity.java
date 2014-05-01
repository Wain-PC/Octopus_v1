package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.MyListAdapter;
import com.wainpc.octopus.asynctasks.JsonSeriesListLoader;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;
import com.wainpc.octopus.modules.LocalDataBase;

//Activity---------------------------------------------------------------------
public class MainActivity extends FragmentActivity implements
		AsyncSeriesListResponse {

	public SectionsPagerAdapter mSectionsPagerAdapter;
	public ViewPager mViewPager;
	static ProgressBar progressBar;
	public JsonSeriesListLoader loader = new JsonSeriesListLoader();
	public static String tag = "myLogs";
	private static ImageLoader him;
	public static LocalDataBase localDB;

	public static ArrayList<HashMap<String, String>> seriesList = new ArrayList<HashMap<String, String>>();
	public HashMap<String, String> map;
	public String rootURL = "http://192.168.1.106:1337/api/latest?json=1";

	// success handler on
	public void onLoadItemsSuccess(ArrayList<HashMap<String, String>> data) {
		Log.d(tag, "Got data!" + data.size());
		seriesList = data;

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// localDB = new LocalDataBase(this);
		// Log.d(tag,"data base is created " +localDB);

		// initialize ImageLoader
		him = ImageLoader.getInstance();

		DisplayImageOptions himOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				// @TODO: move this to settings
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).build();
		ImageLoaderConfiguration himConfig = new ImageLoaderConfiguration.Builder(
				getBaseContext()).defaultDisplayImageOptions(himOptions)
				.memoryCache(new LruMemoryCache(10 * 1024 * 1024)) // @TODO:
																	// move this
																	// to
																	// settings
				.build();

		him.init(himConfig);

		// make async request
		Log.d(tag, "make async request");
		loader.execute(rootURL);
		loader.delegate = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	// Adapter---------------------------------------------------------------------
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch (position) {
			case 0: {
				f = new DummySectionFragment();
				break;
			}
			case 1: {
				f = new DummySectionFragment();
				break;
			}
			}
			return f;
		}
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_latest).toUpperCase(l);
			case 1:
				return getString(R.string.title_alphabet).toUpperCase(l);
		
			}
			return null;
		}
	}

	// Fragment---------------------------------------------------------------------
	public static class DummySectionFragment extends Fragment {

		public MyListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public DummySectionFragment() {
		}

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_list, container,
					false);
			
		    
			listViewFragmentMain = (ListView) rootView
					.findViewById(R.id.listView_fragment_main);
			listAdapter = new MyListAdapter(getActivity(), him, seriesList);
			listViewFragmentMain.setAdapter(listAdapter);

			
			listViewFragmentMain
					.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String seriesId = seriesList.get(position)
									.get("id").toString();
							String seriesTitle = seriesList.get(position)
									.get("title").toString();

							// add to db
							/*
							 * SQLiteDatabase db =
							 * localDB.getWritableDatabase(); ContentValues
							 * newValues = new ContentValues();
							 * newValues.put("SERIES_ID", seriesId);
							 * newValues.put("SERIES_NAME", seriesTitle); long
							 * rowID =db.insert("bookmark", null, newValues);
							 * Log.d(tag,"db_id "+rowID); //////////////////
							 */

							// open series page
							Intent seriesPage = new Intent(getActivity()
									.getApplicationContext(),
									SeriesActivity.class);
							seriesPage.putExtra("id", seriesId);
							seriesPage.putExtra("title", seriesTitle);
							startActivity(seriesPage);
						}
					});

			listViewFragmentMain.setOnScrollListener(new OnScrollListener() {

				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
				}

				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem + visibleItemCount == totalItemCount
							&& totalItemCount != 0) {
					}
				}
			});
			return rootView;
		}
	}

}
