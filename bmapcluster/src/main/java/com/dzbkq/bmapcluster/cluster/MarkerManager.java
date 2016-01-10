package com.dzbkq.bmapcluster.cluster;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLngBounds;


/**
 *  
 * Keeps track of collections of markers on the map. Delegates all Marker-related events to each
 * collection's individually managed listeners.
 * <p/>
 * All marker operations (adds and removes) should occur via its collection class. That is, don't
 * add a marker via a collection, then remove it via Marker.remove()
 * 
 */
public class MarkerManager  implements OnMapStatusChangeListener, OnMarkerDragListener, OnInfoWindowClickListener, OnMarkerClickListener{

	private static final String TAG = MarkerManager.class.getSimpleName();
	private BaiduMap mBaiduMap;
	private HashMap<Marker, Collection> mAllMarkers = new HashMap<>();

	public MarkerManager(Context context, BaiduMap map) {
		this.mBaiduMap = map;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.d(TAG, "onMarkerClick");
		Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerClickListener != null) {
            return collection.mMarkerClickListener.onMarkerClick(marker);
        }
		return false;
	}


	//-------------onMapStatusChangeLinstener-----------
	@Override
	public void onMapStatusChange(MapStatus status) {
		Log.d(TAG, "onMapStatusChanging");			
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus status) {
		Log.d(TAG, "onMapStatusChangeFinish");
		LatLngBounds bounds = status.bound;
		Log.d(TAG, bounds.toString());
		Log.d(TAG, "zoom:" + status.zoom);
		
	}

	@Override
	public void onMapStatusChangeStart(MapStatus status) {
		Log.d(TAG, "onMapStatusChangeStart");

		
	}

	//----------OnInfoWindowClickListener-------------------
	@Override
	public void onInfoWindowClick() {
		Log.d(TAG, "onInfoWindowClick");
		mBaiduMap.hideInfoWindow();
	}
	
	//---------------------OnMarkerDragListener---------------------
    @Override
    public void onMarkerDragStart(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDragStart(marker);
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDrag(marker);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        if (collection != null && collection.mMarkerDragListener != null) {
            collection.mMarkerDragListener.onMarkerDragEnd(marker);
        }
    }
	
	//----------public method--------------------------------
	
	/**
	 * 保存坐标点 和显示状态
	 * @param outState
	 */
	public void onSaveInstance(Bundle outState) {
		
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		
	}
	
	public void ondestroy() {
	}

	public Collection newCollection() {
		return new Collection();
	}
	
    /**
     * Removes a marker from its collection.
     *
     * @param marker the marker to remove.
     * @return true if the marker was removed.
     */
    public boolean remove(Marker marker) {
        Collection collection = mAllMarkers.get(marker);
        return collection != null && collection.remove(marker);
    }

	
	//========================inner class=============================
    public class Collection {
        private final Set<Marker> mMarkers = new HashSet<Marker>();
        private OnMarkerClickListener mMarkerClickListener;
        private OnMarkerDragListener mMarkerDragListener;
        
    public Collection() {
    }

    public Marker addMarker(MarkerOptions opts) {
        Marker marker = (Marker) mBaiduMap.addOverlay(opts);
        mMarkers.add(marker);
        mAllMarkers.put(marker, Collection.this);
        return marker;
    }

    public boolean remove(Marker marker) {
        if (mMarkers.remove(marker)) {
            mAllMarkers.remove(marker);
            marker.remove();
            return true;
        }
        return false;
    }

    public void clear() {
        for (Marker marker : mMarkers) {
            marker.remove();
            mAllMarkers.remove(marker);
        }
        mMarkers.clear();
    }

    public java.util.Collection<Marker> getMarkers() {
        return Collections.unmodifiableCollection(mMarkers);
    }


    public void setOnMarkerClickListener(OnMarkerClickListener markerClickListener) {
        mMarkerClickListener = markerClickListener;
    }

    public void setOnMarkerDragListener(OnMarkerDragListener markerDragListener) {
        mMarkerDragListener = markerDragListener;
    }

}


}
