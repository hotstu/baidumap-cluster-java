package com.dzbkq.myapplication.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbkq.bmapcluster.view.IconGenerator;
import com.dzbkq.myapplication.R;

import java.util.ArrayList;


/**
 *
 * @version: V1.0
 * @date 2015-8-28 下午4:25:05
 * 
 */
public class FragmentMarkerTest extends Fragment {
    GridView mGrid;
    ArrayList<Bitmap> apps = new ArrayList<Bitmap>();
    ArrayList<String> appInfos = new ArrayList<String>();

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.gis_fragment_marker_example, container, false);
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		loadIcons();
		mGrid = (GridView) view.findViewById(R.id.myGrid);
	};
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mGrid.setAdapter(new AppsAdapter(getActivity()));
	}
	

    private void loadIcons() {
        apps.clear();
        appInfos.clear();
        IconGenerator ig = new IconGenerator(getActivity());
        ig.setTextAppearance(R.style.ClusterIcon_TextAppearance);
        Drawable a = getActivity().getResources().getDrawable(R.drawable.icon_r_blue);
        Drawable b = getActivity().getResources().getDrawable(R.drawable.icon_r_green);
        Drawable c = getActivity().getResources().getDrawable(R.drawable.icon_r_grey);
        Drawable d = getActivity().getResources().getDrawable(R.drawable.icon_r_red);
        Drawable e = getActivity().getResources().getDrawable(R.drawable.icon_r_yellow);
        String[] title = {"我","们","中","国","人"};
        Drawable[] ds = {a, b, c, d, e};
        for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				ig.setBackground(ds[j]);
				apps.add(ig.makeIcon("" + title[i]));
				appInfos.add(title[i] + j);
				
			}
		}

    }
    
    public static class ViewHolder {
    	public ViewHolder(ImageView iv, TextView tv) {
			this.iv = iv;
			this.tv = tv;
		}
    	ImageView iv;
    	TextView tv;
    }

    public class AppsAdapter extends BaseAdapter {
    	LayoutInflater mInflater;
		public AppsAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;
            TextView t;

            if (convertView == null) {
            	convertView = mInflater.inflate(R.layout.gis_grid_item_marker, parent, false);
                i = (ImageView) convertView.findViewById(R.id.iv_marker);
                t = (TextView) convertView.findViewById(R.id.tv_info);
                ViewHolder holder = new ViewHolder(i, t);
                convertView.setTag(holder);
                //i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //i.setLayoutParams(new GridView.LayoutParams(50, 50));
            } 
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.iv.setImageBitmap(apps.get(position));
            holder.tv.setText(appInfos.get(position));
       
            return convertView;
        }


        public final int getCount() {
            return apps.size();
        }

        public final Object getItem(int position) {
            return apps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }
}
