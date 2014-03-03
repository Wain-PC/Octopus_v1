package com.wainpc.octopus.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wainpc.octopus.R;


public class MyListAdapter extends BaseAdapter  {

	    private Activity activity;
	    private ArrayList<HashMap<String, String>> data;
	    private static LayoutInflater inflater=null;
		private static final String tag = "myLogs";
		private ImageLoader him;
		private Context context;
	

	    public MyListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
	        activity = a;
	        data=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        context = activity.getApplicationContext();
    		him = ImageLoader.getInstance();
    		him.init(ImageLoaderConfiguration.createDefault(context));
	        
	    }

	    public int getCount() {
	        return data.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.adapter_list, null);

	        TextView title = (TextView)vi.findViewById(R.id.title); // title	      
	        ImageView image=(ImageView)vi.findViewById(R.id.image); // thumb image
	        
	        HashMap<String, String> map = new HashMap<String, String>();
	        map = data.get(position);
	      
	        title.setText(map.get("title"));	       
	      //Uri url = Uri.parse(map.get("image"));
	      
	        return vi;
	}	  	
}


