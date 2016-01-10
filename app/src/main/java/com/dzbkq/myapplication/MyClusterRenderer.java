package com.dzbkq.myapplication;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.dzbkq.bmapcluster.cluster.Cluster;
import com.dzbkq.bmapcluster.cluster.ClusterManager;
import com.dzbkq.bmapcluster.view.DefaultClusterRenderer;
import com.dzbkq.myapplication.model.ResPoint;

/**
 *
 * @version: V1.0
 * @date 2015-8-18 上午8:18:45
 * 
 */
public class MyClusterRenderer extends DefaultClusterRenderer<ResPoint> {
	public interface RenderListener {
		 /**
	     * Called before the marker for a ClusterItem is added to the map.如果缓存中已有该item对应的marker, 这此方法不会被call
	     */
	    public void onBeforeClusterItemRendered(ResPoint item, MarkerOptions markerOptions) ;
	    /**
	     * Called after the marker for a ClusterItem has been added to the map.
	     */
	    public void onClusterItemRendered(ResPoint clusterItem, Marker marker);
	}

	private RenderListener mRenderListener;
	public static boolean dontRenderAsCluster = false;//set to true if you want stop clustering function
	
	public MyClusterRenderer(Context context, BaiduMap map,
			ClusterManager<ResPoint> clusterManager, RenderListener renderListener) {
		super(context, map, clusterManager);
		this.mRenderListener = renderListener;
	}
	
	@Override
	protected void onBeforeClusterItemRendered(ResPoint item,
			MarkerOptions markerOptions) {
		if (mRenderListener != null)
			mRenderListener.onBeforeClusterItemRendered(item, markerOptions);
	}
	
	@Override
	protected void onClusterItemRendered(ResPoint clusterItem, Marker marker) {
		super.onClusterItemRendered(clusterItem, marker);
		if (mRenderListener != null) 
			mRenderListener.onClusterItemRendered(clusterItem, marker);
	}
	
	@Override
	protected boolean shouldRenderAsCluster(Cluster<ResPoint> cluster) {
		if (dontRenderAsCluster)
			return false;
		try {
			if (mMap == null || mMap.getMapStatus() == null) {
				return super.shouldRenderAsCluster(cluster);
			}
			else if (mMap.getMapStatus().zoom >= 20.0f) {
				return false;
			}
		} catch(Exception e) {}//ignore 
		return super.shouldRenderAsCluster(cluster);
	}

}











