package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.SeriesNoPostersListAdapter;
import com.wainpc.octopus.asynctasks.SeriesListLoader;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.interfaces.AsyncSeriesListResponse;

public class GenreActivity extends BaseFragmentActivity implements AsyncSeriesListResponse {
	public static String tag = "myLogs";
	public String rootURL = "http://173.44.34.162:1337/genre/";
	public SeriesListLoader loader = new SeriesListLoader();
	public static ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_list;
    }

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.resetUI();
		this.switchToLoadingView();
	
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		String genreId = extras.getString("id");
		String genreTitle = extras.getString("title");
		getActionBar().setTitle("Æàíð:"+genreTitle);
		loader.execute(rootURL + genreId+"?json=1");
		loader.delegateList = this;

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
		super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            this.finish();
	    }
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
				f = new GenreFragment();
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
			return getString(R.string.action_search).toUpperCase(l);
		}
	}
	
	
	// Fragment---------------------------------------------------------------------
	public static class GenreFragment extends Fragment {
		
		public SeriesNoPostersListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public GenreFragment() {
		}

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_list, container,
					false);
			
		    
			listViewFragmentMain = (ListView) rootView
					.findViewById(R.id.listView_fragment_main);
			listAdapter = new SeriesNoPostersListAdapter(getActivity(), seriesList);
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