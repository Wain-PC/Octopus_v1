package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.GenresListAdapter;
import com.wainpc.octopus.adapters.SeriesListAdapter;
import com.wainpc.octopus.asynctasks.GenresListLoader;
import com.wainpc.octopus.asynctasks.SeriesListLoader;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Genre;
import com.wainpc.octopus.interfaces.MainActivityInterface;

//Activity---------------------------------------------------------------------
public class MainActivity extends BaseFragmentActivity implements
		MainActivityInterface {

	public SectionsPagerAdapter mSectionsPagerAdapter;
	public ViewPager mViewPager;
	public static String tag = "myLogs";
	private static ImageLoader him;
	private SeriesListLoader latestSeriesLoader = null;
	private GenresListLoader genreListLoader = null;
	public static ArrayList<EpisodeItem> seriesList = new ArrayList<EpisodeItem>();
	public static ArrayList<Genre> genreList = new ArrayList<Genre>();
	public HashMap<String, String> map;
	public String latestSeriesURL = "http://173.44.34.162:1337/latest?json=1";
	public String genresURL = "http://173.44.34.162:1337/genres?json=1";
	
	// success handler on
	public void onLoadLatestSeriesSuccess(ArrayList<EpisodeItem> data) {
		if(data.size() == 0) {
			this.switchToErrorView("Îøèïêî!");
		}
		//loaded normally
		else {
			Log.d(tag, "Got data!" + data.size());
			seriesList = data;
			
			//load genres then
			genreListLoader = new GenresListLoader();
			genreListLoader.execute(genresURL);
			genreListLoader.delegate = this;
					
		}
	}
	
	@Override
	public void onLoadGenresListSuccess(ArrayList<Genre> out) {
		genreList = out;
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		//disable loading mode
		this.switchToListView();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);	
	}
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    
     private void loadImageManager() {
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
     }

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		this.resetUI();
		this.switchToLoadingView();
		loadImageManager();

		// make async request
		Log.d(tag, "make async request");
		latestSeriesLoader = new SeriesListLoader();
		latestSeriesLoader.execute(latestSeriesURL);
		latestSeriesLoader.delegate = this;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setupMiniController(findViewById(R.id.mc1));
    }
	
    @Override
    public void onDestroy() {
        if (this.latestSeriesLoader != null) {
            this.latestSeriesLoader.cancel(true);
        }
        super.onDestroy();  
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
				f = new LatestAddedFragment();
				break;
			}
			case 1: {
				f = new GenresFragment();
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
				return getString(R.string.title_genres).toUpperCase(l);
		
			}
			return null;
		}
	}

	// Fragment---------------------------------------------------------------------
	public static class LatestAddedFragment extends Fragment {
		public SeriesListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public LatestAddedFragment() {
			// TODO Auto-generated constructor stub
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
	
	
	// Fragment---------------------------------------------------------------------
	public static class GenresFragment extends Fragment {

		
		public GenresListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public GenresFragment() {
		}

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.fragment_list, container,
					false);
			
		    
			listViewFragmentMain = (ListView) rootView
					.findViewById(R.id.listView_fragment_main);
			listAdapter = new GenresListAdapter(getActivity(), genreList);
			listViewFragmentMain.setAdapter(listAdapter);

			
			listViewFragmentMain
					.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String genreId = genreList.get(position).id;
							Log.d(tag,"GENRE_ID:"+genreId);
							// open series page
//							Intent seriesPage = new Intent(getActivity()
//									.getApplicationContext(),
//									SeriesActivity.class);
//							seriesPage.putExtra("id", genreId);
//							startActivity(seriesPage);
						}
					});
			
			return rootView;
		}
	}
	//----END FRAGMENT-------------------------------------------

}
//----END ACTIVITY----------------------------------------------
