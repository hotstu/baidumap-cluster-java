package com.dzbkq.myapplication;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.dzbkq.bmapcluster.cluster.ClusterManager;
import com.dzbkq.bmapcluster.cluster.MarkerManager;
import com.dzbkq.myapplication.icon.InterfaceIconMgr;
import com.dzbkq.myapplication.model.ResPoint;

import java.util.Collection;


/**
 *
 * @version: V1.0
 * @date 2015-8-18 下午2:01:18
 * 
 */
public class MyClusterManager extends ClusterManager<ResPoint> implements MyClusterRenderer.RenderListener {
	final private InterfaceIconMgr iconManager;

	public MyClusterManager(Context context, BaiduMap map,
			MarkerManager markerManager, InterfaceIconMgr manager) {
		super(context, map, markerManager);
		this.iconManager = manager;
		init(context, map);
	}

	public MyClusterManager(Context context, BaiduMap map, InterfaceIconMgr manager) {
		super(context, map);
		this.iconManager = manager;
		init(context, map);
	}
	
	private void init(Context context, BaiduMap map) {
		this.setRenderer(new MyClusterRenderer(context, map, this, this));
	}
	
	/**
	 * 释放资源, 不再使用这个类之前调用
	 */
	public void release() {
		try {
			iconManager.release();
		} catch(Exception e) {e.printStackTrace();};
	}

	
	 /**
     * Get the marker from a ClusterItem
     * @param item ClusterItem which you will obtain its marker
     * @return a marker from a ClusterItem or null if it does not exists
     */
	public Marker getItemMarker(ResPoint item) {
		return ((MyClusterRenderer)mRenderer).getMarker(item);
	}
	
	public InterfaceIconMgr getIconManager() {
		return this.iconManager;
	}
		
	
	public Collection<ResPoint> getItems() {
		return mAlgorithm.getItems();
	}
	
	//-----------------private method---------------------------

	
	//----------------inner class--------------------------------
  
	@Override
	public void onBeforeClusterItemRendered(ResPoint item,
			MarkerOptions markerOptions) {
		iconManager.iconFactory(item, markerOptions);
	}

	@Override
	public void onClusterItemRendered(ResPoint clusterItem, Marker marker) {
		
	}
	
	

}
