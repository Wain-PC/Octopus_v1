package com.wainpc.octopus.activities;

import java.util.Locale;

import android.os.Bundle;
import android.provider.Contacts.SettingsColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.MyListAdapter;
import com.wainpc.octopus.asynctasks.JsonSeriesItemLoader;
import com.wainpc.octopus.core.models.Series;
import com.wainpc.octopus.interfaces.AsyncSeriesItemResponse;

public class SeriesActivity extends FragmentActivity implements AsyncSeriesItemResponse {
	
	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	public JsonSeriesItemLoader loader = new JsonSeriesItemLoader();
	public static String tag = "myLogs";
	public static Series series;
	public String seriesId;
	public String seriesTitle;
	public String rootURL = "http://192.168.1.104:1337/api/series/";
	private static ImageLoader him;
	
	//do something when series is loaded!
	public void onLoadSeriesSuccess(Series s) {
		series = s;
		spAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.pager_series);
		Log.d(tag,spAdapter.toString());
		viewPager.setAdapter(spAdapter);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        seriesId = extras.getString("id");
        seriesTitle = extras.getString("title");
        setTitle(seriesTitle);
        loader.execute(rootURL+seriesId+"?json=1");
		loader.delegate = this;
		
		him = ImageLoader.getInstance();
		
		DisplayImageOptions himOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true) //@TODO: move this to settings
        .showImageForEmptyUri(R.drawable.ic_launcher)
        .showImageOnLoading(R.drawable.ic_launcher)
        .showImageOnFail(R.drawable.ic_launcher)
        .build();
ImageLoaderConfiguration himConfig = new ImageLoaderConfiguration.Builder(getBaseContext())
        .defaultDisplayImageOptions(himOptions)
        .memoryCache(new LruMemoryCache(10 * 1024 * 1024)) //@TODO: move this to settings
        .build();
		
		him.init(himConfig);	        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch(position) {
			case 0: {
				f = new SeriesInfoFragment();
				break;
			}
			case 1: {
				f = new EpisodeListFragment();
				break;
			}
			}
			return f;
		}

		@Override
		public int getCount() {
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_series_info).toUpperCase(l);
			case 1:
				return getString(R.string.title_series_episodes).toUpperCase(l);
			}
			return null;
		}
		
    }
    
    public static class SeriesInfoFragment extends Fragment {

		View rootView;	
		public SeriesInfoFragment() {
		}
		

		//View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_series_info,
					container, false);
			ImageView poster = (ImageView)rootView.findViewById(R.id.poster);
			TextView description = (TextView)rootView.findViewById(R.id.description);
			description.setText(series.description);
			try {
			him.displayImage(series.poster.get(series.poster.size()-1), poster);
			}
			catch(IndexOutOfBoundsException e) {
				Log.d("myLogs","Out of bounds for "+series.title_en);
			}	
					return rootView;
			
		}
    }
    
    public static class EpisodeListFragment extends Fragment {
        
    	public MyListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;
		
		public EpisodeListFragment() {
		}
		

		//View---------------------------------------------------------------------
				@Override
				public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {
					rootView = inflater.inflate(R.layout.fragment_list,
							container, false);
					
					listViewFragmentMain = (ListView) rootView.findViewById(R.id.listView_fragment_main);
					
					listAdapter = new MyListAdapter(getActivity(),him,series);
					listViewFragmentMain.setAdapter(listAdapter);
					
							return rootView;
					
				}
    }

}
