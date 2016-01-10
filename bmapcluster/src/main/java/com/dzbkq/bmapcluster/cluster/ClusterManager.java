/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzbkq.bmapcluster.cluster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.dzbkq.bmapcluster.cluster.agl.Algorithm;
import com.dzbkq.bmapcluster.cluster.agl.NonHierarchicalDistanceBasedAlgorithm;
import com.dzbkq.bmapcluster.cluster.agl.PreCachingAlgorithmDecorator;
import com.dzbkq.bmapcluster.view.DefaultClusterRenderer;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * &#x6ce8;&#x610f; baidu &#x6ca1;&#x6709;&#x50cf;google&#x4e00;&#x6837;&#x7684;&#x5e26;&#x53c2;&#x6570;&#x7684;InfoWindowClickLinstener&#x56de;&#x8c03;, &#x6240;&#x4ee5;&#x8fd9;&#x91cc;&#x53bb;&#x6389;&#x4e86;&#x76f8;&#x5173;&#x63a5;&#x53e3;&#x56de;&#x8c03;, &#x53ef;&#x4ee5;&#x5728;OnmarkerClickListener&#x4e2d;&#x4f7f;&#x7528;&#x533f;&#x540d;&#x7c7b;&#x7684;&#x65b9;&#x5f0f;&#x4ee3;&#x66ff;
 * <p/>
 * Groups many items on a map based on zoom level.
 * <p/>
 * ClusterManager should be added to the map as an: <ul> <li>{@link OnMapStatusChangeListener}</li>
 * <li>{@link OnMarkerClickListener}</li> </ul>
 */
public class ClusterManager<T extends ClusterItem> implements OnMapStatusChangeListener, OnMarkerClickListener {
    private final MarkerManager.Collection mMarkers;
    private final MarkerManager.Collection mClusterMarkers;

    protected Algorithm<T> mAlgorithm;
    private final ReadWriteLock mAlgorithmLock = new ReentrantReadWriteLock();
    protected ClusterRenderer<T> mRenderer;
    private MarkerManager mMarkerManager;

    protected BaiduMap mMap;
    private ClusterTask mClusterTask;
    private final ReadWriteLock mClusterTaskLock = new ReentrantReadWriteLock();

    private OnClusterItemClickListener<T> mOnClusterItemClickListener;
    private OnClusterClickListener<T> mOnClusterClickListener;
    private float previousZoom = -1f;

    public ClusterManager(Context context, BaiduMap map) {
		this(context, map, new MarkerManager(context, map));
	}

    public ClusterManager(Context context, BaiduMap map, MarkerManager markerManager) {
        mMap = map;
        mMarkerManager = markerManager;
        mClusterMarkers = markerManager.newCollection();
        mMarkers = markerManager.newCollection();
        mRenderer = new DefaultClusterRenderer<T>(context, map, this);
        mAlgorithm = new PreCachingAlgorithmDecorator<T>(new NonHierarchicalDistanceBasedAlgorithm<T>());
        mClusterTask = new ClusterTask();
        mRenderer.onAdd();
    }
    
    public MarkerManager.Collection getMarkerCollection() {
        return mMarkers;
    }

    public MarkerManager.Collection getClusterMarkerCollection() {
        return mClusterMarkers;
    }

    public MarkerManager getMarkerManager() {
        return mMarkerManager;
    }


    public void setRenderer(ClusterRenderer<T> view) {
        mRenderer.setOnClusterClickListener(null);
        mRenderer.setOnClusterItemClickListener(null);
        mClusterMarkers.clear();
        mMarkers.clear();
        mRenderer.onRemove();
        mRenderer = view;
        mRenderer.onAdd();
        mRenderer.setOnClusterClickListener(mOnClusterClickListener);
        mRenderer.setOnClusterItemClickListener(mOnClusterItemClickListener);
        cluster();
    }

    public void setAlgorithm(Algorithm<T> algorithm) {
        mAlgorithmLock.writeLock().lock();
        try {
            if (mAlgorithm != null) {
                algorithm.addItems(mAlgorithm.getItems());
            }
            mAlgorithm = new PreCachingAlgorithmDecorator<T>(algorithm);
        } finally {
            mAlgorithmLock.writeLock().unlock();
        }
        cluster();
    }

    public void clearItems() {
        mAlgorithmLock.writeLock().lock();
        try {
            mAlgorithm.clearItems();
        } finally {
            mAlgorithmLock.writeLock().unlock();
        }
    }

    public void addItems(Collection<T> items) {
        mAlgorithmLock.writeLock().lock();
        try {
            mAlgorithm.addItems(items);
        } finally {
            mAlgorithmLock.writeLock().unlock();
        }
    }

    public void addItem(T myItem) {
        mAlgorithmLock.writeLock().lock();
        try {
            mAlgorithm.addItem(myItem);
        } finally {
            mAlgorithmLock.writeLock().unlock();
        }
    }

    public void removeItem(T item) {
        mAlgorithmLock.writeLock().lock();
        try {
            mAlgorithm.removeItem(item);
        } finally {
            mAlgorithmLock.writeLock().unlock();
        }
    }

    /**
     * Force a re-cluster. You may want to call this after adding new item(s).
     */
    @SuppressLint("NewApi")
	public void cluster() {
        mClusterTaskLock.writeLock().lock();
        try {
            // Attempt to cancel the in-flight request.
            mClusterTask.cancel(true);
            mClusterTask = new ClusterTask();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mClusterTask.execute(mMap.getMapStatus().zoom);
            } else {
                mClusterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMap.getMapStatus().zoom);
            }
        } finally {
            mClusterTaskLock.writeLock().unlock();
        }
    }


	@Override
	public void onMapStatusChange(MapStatus arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus arg0) {
		float zoom = arg0.zoom;
		Log.d("onMapStatusChangeFinish", zoom +"");
		if (previousZoom == zoom) {
			return;
		}
		previousZoom = zoom;
		cluster();
		
	}

	@Override
	public void onMapStatusChangeStart(MapStatus arg0) {
		
	}
	
    @Override
    public boolean onMarkerClick(Marker marker) {
        return getMarkerManager().onMarkerClick(marker);
    }
   

    /**
     * Runs the clustering algorithm in a background thread, then re-paints when results come back.
     */
    private class ClusterTask extends AsyncTask<Float, Void, Set<? extends Cluster<T>>> {
        @Override
        protected Set<? extends Cluster<T>> doInBackground(Float... zoom) {
            mAlgorithmLock.readLock().lock();
            try {
                return mAlgorithm.getClusters(zoom[0]);
            } finally {
                mAlgorithmLock.readLock().unlock();
            }
        }

        @Override
        protected void onPostExecute(Set<? extends Cluster<T>> clusters) {
            mRenderer.onClustersChanged(clusters);
        }
    }

    /**
     * Sets a callback that's invoked when a Cluster is tapped. Note: For this listener to function,
     * the ClusterManager must be added as a click listener to the map.
     */
    public void setOnClusterClickListener(OnClusterClickListener<T> listener) {
        mOnClusterClickListener = listener;
        mRenderer.setOnClusterClickListener(listener);
    }


    /**
     * Sets a callback that's invoked when an individual ClusterItem is tapped. Note: For this
     * listener to function, the ClusterManager must be added as a click listener to the map.
     */
    public void setOnClusterItemClickListener(OnClusterItemClickListener<T> listener) {
        mOnClusterItemClickListener = listener;
        mRenderer.setOnClusterItemClickListener(listener);
    }


    /**
     * 注意 , 使用clusterMnager,marker是被Renderer自动管理的,
     * 通过设置item的属性, 然后在beforeRender中设置markerOptions属性的方式才能永久改变相关属性,
     * 直接设置marker的属性在某些情况下会被覆盖.
     * Called when a Cluster is clicked.
     */
    public interface OnClusterClickListener<T extends ClusterItem> {
        public boolean onClusterClick(Cluster<T> cluster, Marker marker);
    }

    /**
     * 注意 , 使用clusterMnager,marker是被Renderer自动管理的,
     * 通过设置item的属性, 然后在beforeRender中设置markerOptions属性的方式才能永久改变相关属性,
     * 直接设置marker的属性在某些情况下会被覆盖.
     * Called when an individual ClusterItem is clicked.
     */
    public interface OnClusterItemClickListener<T extends ClusterItem> {
        public boolean onClusterItemClick(T item, Marker marker);
    }



}
