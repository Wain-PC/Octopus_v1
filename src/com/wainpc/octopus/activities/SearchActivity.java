package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.activities.SeriesActivity.SectionsPagerAdapter;
import com.wainpc.octopus.adapters.SeriesListAdapter;
import com.wainpc.octopus.asynctasks.SearchExecutor;
import com.wainpc.octopus.asynctasks.SeriesListLoader;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;
import com.wainpc.octopus.modules.HttpLoader;

public class SearchActivity extends BaseFragmentActivity implements AsyncSeriesListResponse {
	public static String tag = "myLogs";
	private static ImageLoader him;
	public String rootURL = "http://173.44.34.162:1337/search?json=1&q=";
	public SearchExecutor loader = new SearchExecutor();
	public static ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.resetUI();
		this.switchToLoadingView();
	
		Intent intent = getIntent();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		him = ImageLoader.getInstance();

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.d(tag, "query=" + query);
			loader.execute(rootURL + HttpLoader.encodeURIComponent(query));
			loader.delegate = this;
			setTitle(query);
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setupMiniController(findViewById(R.id.mc1));
    }
	

	@Override
	public void onLoadItemsSuccess(ArrayList<EpisodeItem> out) {
		seriesList=out;
		
		if(seriesList == null || seriesList.size() == 0) {
			this.switchToErrorView(getString(R.string.error_json_parse));
		}
		else {
			spAdapter = new SectionsPagerAdapter(getSupportFragmentManager()); 
			//disable loading state
			this.switchToListView();
			// Set up the ViewPager with the sections adapter.
			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(spAdapter);
		}
	
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
				f = new SearchFragment();
				break;
			}
			}
			return f;
		}
		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return getString(R.string.title_latest).toUpperCase(l);
		}
	}
	
	
	// Fragment---------------------------------------------------------------------
	public static class SearchFragment extends Fragment {
		
		public SeriesListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public SearchFragment() {
		}

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_list, container,
					false);
			
		    
			listViewFragmentMain = (ListView) rootView
					.findViewById(R.id.listView_fragment_main);
			listAdapter = new SeriesListAdapter(getActivity(), him, seriesList);
			listViewFragmentMain.setAdapter(listAdapter);

			
			listViewFragmentMain
			.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					String seriesId = seriesList.get(position)
							.get("id").toString();
					String seriesTitle = seriesList.get(position)
							.get("title").toString();

					// open series page
					Intent seriesPage = new Intent(getActivity()
							.getApplicationContext(),
							SeriesActivity.class);
					seriesPage.putExtra("id", seriesId);
					seriesPage.putExtra("title", seriesTitle);
					startActivity(seriesPage);
				}
			});
			
			return rootView;
		}
	}
	//----END FRAGMENT-------------------------------------------
	
	


}