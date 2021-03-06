package com.wainpc.octopus.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.cast.MediaInfo;
import com.google.sample.castcompanionlibrary.cast.VideoCastManager;
import com.google.sample.castcompanionlibrary.cast.callbacks.IVideoCastConsumer;
import com.google.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.sample.castcompanionlibrary.widgets.MiniController;
import com.wainpc.octopus.R;

public abstract class CastActivity extends ActionBarActivity {
	
	//Cast variables
	public static String tag = "myLogs";
	private MenuItem mediaRouteMenuItem;
	public VideoCastManager mCastManager;
    public IVideoCastConsumer mCastConsumer;
    public MediaInfo mSelectedMedia;
	public MiniController mMini;
	private int menuResource = R.menu.main;
    
    
    private void castInitialize() {
   	 VideoCastManager.checkGooglePlayServices(this);
   	 mCastManager = CastApplication.getCastManager(this);

        
        mCastConsumer = new VideoCastConsumerImpl() {

            @Override
            public void onFailed(int resourceId, int statusCode) {
            }
           

            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(tag, "onConnectionSuspended() was called with cause: " + cause);
            }

            @Override
            public void onConnectivityRecovered() {

            }

            @Override
            public void onCastDeviceDetected(final RouteInfo info) {

                    Log.d(tag, "Route is visible: " + info);
                }
            };
            mCastManager.reconnectSessionIfPossible(this, false);
    }
    
    
    private void setupActionBar(ActionBar actionBar) {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setTitle(R.string.app_name);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(menuResource, menu);

		//add Cast button
        Log.d(tag,"CM:"+mCastManager == null? "null":"not null");
		 mediaRouteMenuItem = mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
		
		// add Search button
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchMenuItem.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);		

		return true;
	}
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
		castInitialize();
        setupActionBar(actionBar);
        mCastManager = CastApplication.getCastManager(this);
	}
    
	@Override
    protected void onResume() {
        super.onResume();
		Log.d(tag, "onResume() was called");
        mCastManager = CastApplication.getCastManager(this);
        mCastManager.addVideoCastConsumer(mCastConsumer);
        mCastManager.incrementUiCounter();
    }
	
	@Override
	protected void onPause() {
		super.onPause();     		
		mCastManager.removeVideoCastConsumer(mCastConsumer);
        mCastManager.decrementUiCounter();
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    	Log.d(tag, "onDestroy() is called");
        if (null != mCastManager) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);        		
            mCastManager.removeMiniController(mMini);
            mCastManager.clearContext(this);
        }  
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	Intent i;
        switch (item.getItemId()) {
        	case R.id.action_settings: {
        		// Start new activity
                i = new Intent(this.getApplicationContext(), SettingsActivity.class);
                this.startActivity(i);
        		break;
        	}
        	case R.id.action_bookmarks: {
        		// Start new activity
                i = new Intent(this.getApplicationContext(), BookmarkActivity.class);
                this.startActivity(i);
        		break;
        	}
        	
        	case R.id.action_history: {
        		// Start new activity
                i = new Intent(this.getApplicationContext(), HistoryActivity.class);
                this.startActivity(i);
        		break;
        	}        	
        }
        return true;
 }
    
	   public void setupMiniController(View miniView) {
	        mMini = (MiniController) miniView;
	        mCastManager.addMiniController(mMini);
	    }
	   
	   public void setupMenu(int resource) {
		   menuResource = resource;
	   }

	
}
