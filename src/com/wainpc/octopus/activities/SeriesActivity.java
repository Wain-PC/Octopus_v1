package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.SeriesListAdapter;
import com.wainpc.octopus.asynctasks.RightUrlVideoLoader;
import com.wainpc.octopus.asynctasks.SeriesItemLoader;
import com.wainpc.octopus.core.models.EpisodeItem;
import com.wainpc.octopus.core.models.Series;
import com.wainpc.octopus.core.models.Video;
import com.wainpc.octopus.interfaces.AsyncSeriesItemResponse;
import com.wainpc.octopus.modules.DatabaseBookmarks;
import com.wainpc.octopus.modules.DatabaseHistory;
import com.wainpc.octopus.modules.HttpLoader;

public class SeriesActivity extends BaseFragmentActivity implements
		AsyncSeriesItemResponse {

	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	public SeriesItemLoader loader = null;
	public static RightUrlVideoLoader urlLoader = null;
	public static String tag = "myLogs";
	public static Series series;
	public String seriesId;
	public String seriesTitle;
	public static String seriesPoster;
	public static EpisodeItem selectedEpisode;
	public String rootURL = "http://173.44.34.162:1337/series/";
	public static String urlVideo = "http://173.44.34.162:1337/api/getdirectlink?url=";
	private static ImageLoader him;
	public static ProgressDialog progress;
	public static DatabaseHistory db;

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
			//отобразить сообщение об ошибке
			//TODO: дать пользователю возможность сообщить об отсутствующем видео!!!
			progress.dismiss();
			//this.switchToErrorView(getString(R.string.error_no_direct_video_url));
		}
		else {
			progress.dismiss();
			
			//add to history
			db.addHistoryItem(series, selectedEpisode);
			
			//remote play
			try {
                mCastManager.checkConnectivity();
                MediaInfo m = buildMediaInfo(str,selectedEpisode); 
                Log.d(tag,"MEDIAINFO:"+m);
                loadRemoteMedia(m);
                finish();
            } catch (Exception e) {
                Log.d(tag,"Cannot start casting:"+e.getMessage());
    			//local play
    			Intent intent = new Intent(Intent.ACTION_VIEW);
    			intent.setDataAndType(Uri.parse(str), "video/mp4");
    			startActivity(Intent.createChooser(intent, "Complete action using"));
                return;
            }
		}
	}
	
    private void loadRemoteMedia(MediaInfo media) {
        mCastManager.startCastControllerActivity(this, media, 0, true);
    }
	
    private MediaInfo buildMediaInfo(String directURL, EpisodeItem episode) {
    	
    	String sn = episode.getSeasonNumber();
    	String en = episode.getEpisodeNumber();
    	
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, seriesTitle);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, episode.get("title")+" ("+sn+"x"+en+")");
        movieMetadata.putString(MediaMetadata.KEY_STUDIO, getString(R.string.app_name));
        
        String imgUrl = episode.get("posterURL");
        if(imgUrl != null) {
        	Uri uri = Uri.parse(imgUrl);
            movieMetadata.addImage(new WebImage(uri));
        }
        
        imgUrl = seriesPoster;
        if(imgUrl != null) {
        	Uri uri = Uri.parse(imgUrl);
            movieMetadata.addImage(new WebImage(uri));
        }
        
        String contentType = getVideoContentType(episode);
		String url = getRidOfCORS(directURL, contentType);
		Log.d(tag,"CHRC_URL:"+url);

        return new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(contentType)
                .setMetadata(movieMetadata)
                .build();
    }
    
    private static String getVideoContentType(EpisodeItem episode) {
    	Log.d(tag,"EPISODE:"+episode);
		String type = episode.get("videoType").toString();
		Log.d(type,"VIDEO_TYPE:"+type);
		String rt = null;
		switch(type) {
		case "vk": {
			rt = "video/mp4";
			break;
		}
		case "hls": {
			rt = "application/x-mpegURL";
			break;
		}
		};
		Log.d(tag,"CT:"+rt);
		return rt;
    }
    
    private static String getRidOfCORS(String url, String provider) {
    	switch(provider) {
    	case "application/x-mpegURL": {
    		return url;
    	}
    	default: {
    		return url;
    	}}
    }
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_series;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupMenu(R.menu.series);
		this.resetUI();
		this.switchToLoadingView();
		db = new DatabaseHistory(this);
		Bundle extras = getIntent().getExtras();
		seriesId = extras.getString("id");
		seriesTitle = extras.getString("title");
		getActionBar().setTitle(seriesTitle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		loader = new SeriesItemLoader();
		loader.execute(rootURL + seriesId + "?json=1");
		loader.delegate = this;		
		him = ImageLoader.getInstance();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setupMiniController(findViewById(R.id.mc1));
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	    case android.R.id.home: {
	    	onBackPressed();
	    	break;
	    	}
	    case R.id.action_claim: {
	    	AlertDialog claimDialog = createClaimDialog();
	    	claimDialog.show();
	    }
	    }
	  return (super.onOptionsItemSelected(menuItem));
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
			DatabaseBookmarks db = new DatabaseBookmarks(getActivity());
			rootView = inflater.inflate(R.layout.fragment_series_info,
					container, false);
			ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
			TextView description = (TextView) rootView
					.findViewById(R.id.description);
			description.setText(series.description);
			
			Button bookmarkControl = (Button)rootView.findViewById(R.id.bookmark);
			if (db.isBookmarked(series)) {
				bookmarkControl.setText("”далить из закладок");
			}
			else {
				bookmarkControl.setText("ƒобавить в закладки");				
			}
			
			bookmarkControl.setOnClickListener(new OnClickListener() {
			   
				@Override
				public void onClick(View v) {
					DatabaseBookmarks db = new DatabaseBookmarks(getActivity());
					Button bookmarkControl = (Button)v;
					if (db.isBookmarked(series)) {
						db.deleteBookmark(series);
						showToast(R.string.toast_bookmarks_removed);
						bookmarkControl.setText("ƒобавить в закладки");
					}
					else {
						db.addBookmark(series);
						showToast(R.string.toast_bookmarks_added);
						bookmarkControl.setText("”далить из закладок");
					}
				}
				
				public void showToast(int stringResource) {
			    	Toast.makeText(getActivity().getApplicationContext(), getString(stringResource), 
			    			   Toast.LENGTH_LONG).show();
			    	}
				
			});
			
			try {
				seriesPoster = series.poster.get(series.poster.size()-1); //extremely prone to fail
				him.displayImage(seriesPoster,
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
							selectedEpisode = series.getListArrayOfEpisodes().get(position);
							AlertDialog selectVideoSourceDialog = createVideoListDialog();
							selectVideoSourceDialog.show();
						}
					});
			return rootView;
		}
		
		public AlertDialog createVideoListDialog() {
			
			//create an adapter
			ArrayList<Video> items = selectedEpisode.video;
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.select_dialog_item);
			//fill an adapter with choices
			String title = "";
			for (Video v : items) {
				switch(v.type) {
				case "vk": {
					title = getString(R.string.dialog_series_videolist_vk);
					break;
				}
				case "hls": {
					title = getString(R.string.dialog_series_videolist_hls);
					break;
				}
				}
				arrayAdapter.add(title);
			}
			
			DialogInterface.OnClickListener itemClicklistener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int position) {
					//do all stuff to get direct link and show choosePlayer dialog
					//prepare and send request to acquire direct video URL (or HLS stream)
					String videoUrl = selectedEpisode.video.get(position).url.toString();
					videoUrl = HttpLoader.encodeURIComponent(videoUrl);
					
					//set loading state
					progress = new ProgressDialog(getActivity());
					progress.setMessage(getString(R.string.operation_loading));
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
			};
			
			DialogInterface.OnClickListener cancelClicklistener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.dialog_series_videolist_title)
		           .setAdapter(arrayAdapter, itemClicklistener)
		           .setNegativeButton(android.R.string.cancel, cancelClicklistener);
		    return builder.create();
		}
	}
	
	public AlertDialog createClaimDialog() {
		
		//create an adapter
		final Activity a = this;
		LayoutInflater inflater = this.getLayoutInflater();
		 // Pass null as the parent view because its going in the dialog layout
		View claimDialogView = inflater.inflate(R.layout.dialog_claim, null);
		
		//SEND click listener
		DialogInterface.OnClickListener sendClicklistener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//do stuff prior to sending the claim
				progress = new ProgressDialog(a);
				progress.setMessage(getString(R.string.operation_sending));
				progress.setCancelable(true);
				progress.show();
				
				//execute the request
				/*urlLoader = new RightUrlVideoLoader();
				urlLoader.execute(urlVideo + videoUrl);
				urlLoader.delegate = (AsyncSeriesItemResponse) a;*/
			}
		};
		
		//Cancel click listener
		DialogInterface.OnClickListener cancelClicklistener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
	    builder.setTitle(R.string.dialog_series_claim_title)
	    	.setView(claimDialogView)
	        .setNegativeButton(android.R.string.cancel, cancelClicklistener)
	    	.setPositiveButton(R.string.dialog_series_claim_send, sendClicklistener);
	    return builder.create();
	}

}
