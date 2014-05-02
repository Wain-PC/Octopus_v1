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
import com.wainpc.octopus.core.models.Series;


public class MyListAdapter extends BaseAdapter implements PinnedSectionListAdapter {

	    private Activity activity;
	    private ArrayList<HashMap<String, String>> data;
	    private LayoutInflater inflater=null;
		private static final String tag = "myLogs";
		private static ImageLoader him;
		private Context context;
	

	    public MyListAdapter(Activity a, ImageLoader im, ArrayList<HashMap<String, String>> d) {
	        activity = a;
	        data=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        context = activity.getApplicationContext();
	        him = im;
	    }
	    
	    public MyListAdapter(Activity a, ImageLoader im, Series s) {
	        activity = a;
	        //get list of episodes from series
	        data = s.getListArrayOfEpisodes();
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		  View vi=convertView;
		  
		  if(convertView==null) {
	            vi = inflater.inflate(R.layout.adapter_list, null);
		  }

	        TextView title = (TextView)vi.findViewById(R.id.title); // title	      
	        ImageView image=(ImageView)vi.findViewById(R.id.image); // thumb image
	        //ss
	        HashMap<String, String> map = new HashMap<String, String>();
	        map = data.get(position);
	        
	        //if the item is pinned
	        if(getItemViewType(position) == 1) {
	        	vi.setBackgroundColor(parent.getResources().getColor(R.color.red));
	        }
	      
	        title.setText(map.get("title"));
	        him.displayImage(map.get("posterURL"), image);
	      
	        
	         
	        return vi;
	}
	
	
    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
    	String type = getItem(position).get("type");
    	int typeN = 0;
    	if(type == "ep") typeN = 0;
    	else if(type == "se") typeN = 1;
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
	
	
	
}


