package com.wainpc.octopus.activities;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.SeriesListAdapter;
import com.wainpc.octopus.asynctasks.JsonSeriesItemLoader;
import com.wainpc.octopus.asynctasks.RightUrlVideoLoader;
import com.wainpc.octopus.core.models.Series;
import com.wainpc.octopus.interfaces.AsyncSeriesItemResponse;
import com.wainpc.octopus.modules.HttpLoader;

public class SeriesActivity extends BaseFragmentActivity implements
		AsyncSeriesItemResponse {

	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	public JsonSeriesItemLoader loader = null;
	public static RightUrlVideoLoader urlLoader = null;
	public static String tag = "myLogs";
	public static Series series;
	public String seriesId;
	public String seriesTitle;
	public String rootURL = "http://192.168.1.106:1337/api/series/";
	public static String urlVideo = "http://192.168.1.106:1337/api/getdirectlink?url=";
	private static ImageLoader him;
	public static ProgressDialog progress;

	// do something when series is loaded!
	public void onLoadSeriesSuccess(Series s) {
		if(s == null) {
			this.switchToErrorView(getString(R.string.error_json_parse));
		}
		else {
			series = s;
			spAdapter = new SectionsPagerAdapter(getSupportFragmentManager()); 

			//disable loading state
			this.switchToListView();
			
			// Set up the ViewPager with the sections adapter.
			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(spAdapter);			
		}
	}

	public void onGetRightUrlVideo(String str) {
		// open series page
		Log.d(tag, "str===" + str);
		if(str == "ERR") {
			//���������� ��������� �� ������
			//TODO: ���� ������������ ����������� �������� �� ������������� �����!!!
			progress.dismiss();
			//this.switchToErrorView(getString(R.string.error_no_direct_video_url));
		}
		else {
			progress.dismiss();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(str), "video/*");
			startActivity(Intent.createChooser(intent, "Complete action using"));
		}
	}
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_series;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.resetUI();
		this.switchToLoadingView();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle extras = getIntent().getExtras();
		seriesId = extras.getString("id");
		seriesTitle = extras.getString("title");
		setTitle(seriesTitle);
		loader = new JsonSeriesItemLoader();
		loader.execute(rootURL + seriesId + "?json=1");
		loader.delegate = this;		
		him = ImageLoader.getInstance();
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.loader != null) {
            this.loader.cancel(true);
        }
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
			switch (position) {
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

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_series_info,
					container, false);
			ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
			TextView description = (TextView) rootView
					.findViewById(R.id.description);
			description.setText(series.description);
			try {
				him.displayImage(series.poster.get(series.poster.size() - 1),
						poster);
			} catch (IndexOutOfBoundsException e) {
				Log.d("myLogs", "Out of bounds for " + series.title_en);
			}
			return rootView;

		}
	}

	public static class EpisodeListFragment extends Fragment {

		public SeriesListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;

		public EpisodeListFragment() {
		}

		// View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_list, container,
					false);

			listViewFragmentMain = (ListView) rootView
					.findViewById(R.id.listView_fragment_main);

			listAdapter = new SeriesListAdapter(getActivity(), him, series);
			listViewFragmentMain.setAdapter(listAdapter);
			

			listViewFragmentMain
					.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							//prepare and send request to acquire direct video URL (or HLS stream)
							String videoUrl = series.getListArrayOfEpisodes()
									.get(position).get("url").toString();
							videoUrl = HttpLoader.encodeURIComponent(videoUrl);
							
							//set loading state
							progress = new ProgressDialog(getActivity());
							progress.setMessage(getString(R.string.loading));
							progress.setCancelable(true);
						    progress.setOnCancelListener(new OnCancelListener() {

						        @Override
						        public void onCancel(DialogInterface dialog) {
						        	//when the loading Episode window has been canceled
						        	//we should stop the related loader asynctask
						            if (urlLoader != null) {
						            	urlLoader.cancel(true);
						            }
						        }
						    });
							progress.show();
							
							//execute the request
							urlLoader = new RightUrlVideoLoader();
							urlLoader.execute(urlVideo + videoUrl);
							urlLoader.delegate = (AsyncSeriesItemResponse) getActivity();
						}
					});
			return rootView;
		}
	}

}
