package com.dzbkq.myapplication.icon;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.dzbkq.bmapcluster.view.IconGenerator;
import com.dzbkq.myapplication.model.MetaResPoint;
import com.dzbkq.myapplication.model.ResPoint;

import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_BLUE;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_GREEN;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_ORANGE;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_OTHER1;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_OTHER2;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_OTHER3;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_OTHER4;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_OTHER5;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_PURPLE;
import static com.dzbkq.bmapcluster.view.IconGenerator.STYLE_RED;

/**
 * 
 */
public class TaskLineIconManager implements InterfaceIconMgr {
	private static final int[] colors = {
		    STYLE_RED,
		    STYLE_BLUE,
		    STYLE_GREEN,
		    STYLE_PURPLE,
		    STYLE_ORANGE,
		    STYLE_OTHER1,
		    STYLE_OTHER2,
		    STYLE_OTHER3,
		    STYLE_OTHER4,
		    STYLE_OTHER5
		    };
	private IconGenerator ig;
	private SparseArray<BitmapDescriptor> backgrouds;

	
	public TaskLineIconManager(Context context) {
		ig = new IconGenerator(context);
		backgrouds = new SparseArray<>(colors.length);
	}

	public static int getColorCodeBy(String taskId) {
		int index = 0;
		if (taskId != null) {
			for (int i = 0; i < taskId.length(); i++) {
				index += taskId.charAt(i);
			}
		}
	    index %= colors.length;
	    return IconGenerator.getStyleColor(colors[index]);
	}

	@Override
	public void iconFactory(ResPoint p, MarkerOptions markerOptions) {
		if (markerOptions == null) {
			Log.d("IconFactory", "markerOptions is null");
			return;
		}
		MetaResPoint meta = (MetaResPoint) p.meta;
		int index = 0;
		if (meta.lineId != null ) {
			for (int i = 0; i < meta.lineId.length(); i++) {
				index += meta.lineId.charAt(i);
			}
		} 
		index %= colors.length;
		Log.d("iconFactory_Options", meta.lineId + "-->" + index);
		markerOptions.icon(getIconAt(index));

	}

	@Override
	public void iconFactory(ResPoint p, Marker marker) {
		MetaResPoint meta = (MetaResPoint) p.meta;
		int index = 0;
		if (meta.lineId != null ) {
			for (int i = 0; i < meta.lineId.length(); i++) {
				index += meta.lineId.charAt(i);
			}
		} 
		index %= colors.length;
		Log.d("iconFactory_marker", meta.lineId + "-->" + index);
		setIcon(marker, getIconAt(index));

	}
	
	private BitmapDescriptor getIconAt(int index) {
		Log.d("IconFactory", "getIconAt:" + index);
		if (backgrouds.get(index) == null) {
			ig.setStyle(colors[index]);
			backgrouds.put(index, BitmapDescriptorFactory.fromBitmap(ig.makeIcon("P"+index)));
			
		}
		return backgrouds.get(index);
	}
	
	private void setIcon(Marker marker, BitmapDescriptor icon) {
		if (marker == null) {
			Log.d("IconFactory", "marker is null");
			return;
		}
		marker.setIcon(icon);
		
	}

	@Override
	public void release() {
		// FIXME: 2016/9/6 在异步环境下如果直接recycle会引发问题，这里不recycle实际影响有多大？
	}

}
