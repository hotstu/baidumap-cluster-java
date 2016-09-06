package com.dzbkq.myapplication.fragment;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.dzbkq.myapplication.GisEventFactory;
import com.dzbkq.myapplication.LocationRecorder;
import com.dzbkq.myapplication.MyApp;
import com.dzbkq.myapplication.MyClusterManager;
import com.dzbkq.myapplication.R;
import com.dzbkq.myapplication.StrategyPointsLoader;
import com.dzbkq.myapplication.icon.TaskLineIconManager;
import com.dzbkq.myapplication.model.PointList;


/**
 *
 * @date 2015-8-12 上午11:40:32
 * 
 */
public class FragmentClusterMap extends Fragment implements LoaderManager.LoaderCallbacks<PointList>{
	private static final String TAG = FragmentClusterMap.class.getSimpleName();

	private TextureMapView mMapView;
	private BaiduMap mBaiduMap;
	private static CoordinateConverter mConverter;
	private Context mContext;
	private int layerControl = 0;

	static {
		mConverter = new CoordinateConverter();
		mConverter.from(CoordinateConverter.CoordType.GPS);
	}

	MyClusterManager cmgr;
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			switch (action) {
			case GisEventFactory.ACTION_POINT_ADD:
				Location l = intent.getParcelableExtra("point");
				onNewLocation(l);
            	break;
			default:
				break;
			}
			
		}
	};

	public static FragmentClusterMap newInstance(int layerControl) {
		FragmentClusterMap fragment = new FragmentClusterMap();
		Bundle bundle = new Bundle();
		bundle.putInt("layerControl", layerControl);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.layerControl = getArguments().getInt("layerControl", layerControl);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gis_fragment_map, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapView = (TextureMapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(10.0f));
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
		try { optionalConfig();} catch(Exception e) {e.printStackTrace();}
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");
		mContext = getActivity().getApplicationContext();
		cmgr = new MyClusterManager(mContext, mBaiduMap, new TaskLineIconManager(mContext));
		mBaiduMap.setOnMarkerClickListener(cmgr);
		mBaiduMap.setOnMapStatusChangeListener(cmgr);
		LatLng lastPosition = loadLastPosition(mContext);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(lastPosition));
		// Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
		// getLoaderManager().initLoader(0, null, this);
		reloadResPoint();

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GisEventFactory.ACTION);
		filter.addAction(GisEventFactory.ACTION_POINT_ADD);
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveLatestPosition(mContext, mBaiduMap.getMapStatus().target);
		mMapView.onPause();
		mContext.unregisterReceiver(mBroadcastReceiver);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		cmgr.release();
	}

	
	//-------------------AyncLoader callbacks-------
	@Override
	public Loader<PointList> onCreateLoader(int id, Bundle args) {
		return new StrategyPointsLoader(mContext, layerControl);

	}

	@Override
	public void onLoadFinished(Loader<PointList> loader, PointList data) {
		int id = loader.getId();
		Log.d(TAG, "onLoadFinished id=" + id);
		Log.d(TAG, ""+data.isSuccess +":" + data.errMsg);
		if (data.isSuccess) {
			cmgr.clearItems();
			cmgr.addItems(data);
			cmgr.cluster();
		}
		
	}

	@Override
	public void onLoaderReset(Loader<PointList> loader) {
		Log.d(TAG, "onLoaderReset");
	}

	//----------------------------------------------
	private void onNewLocation(Location l) {
		Log.d(TAG, "onNewLocation");
	}

	
	//去掉baidu logo
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void optionalConfig() {
	    //获取mapview中的缩放控件  
        //ZoomControls zoomControls = (ZoomControls) mMapView.getChildAt(2);  
        //mapView.removeViewAt(2);  
        //调整缩放控件的位置  
        //zoomControls.setPadding(0, 0, 0, 100);  
        //获取mapview中的百度地图图标  
        ImageView iv = (ImageView) mMapView.getChildAt(1);
        iv.setVisibility(View.GONE);
	}

	
	private void reloadResPoint() {
		getLoaderManager().restartLoader(0, null, this);
	}

	
	/**
	 * 优先返回内存中的, 其次外存中的, 再此北京
	 * @param context
	 * @return
	 */
	private LatLng loadLastPosition(Context context) {
		Location memoryLocation = LocationRecorder.getInstance().get();
		if ( memoryLocation != null) {
			return new LatLng(memoryLocation.getLatitude(), memoryLocation.getLongitude());
		}
		String positonStr = MyApp.getInstance()
				.getSharedPreferences("gis", Context.MODE_PRIVATE).getString("LastPosition", null);
		if (positonStr != null) {
			try {
				String[] array = positonStr.split(",");
				double lat = Double.valueOf(array[0]);
				double lng = Double.valueOf(array[1]);
				return new LatLng(lat, lng);
			} catch(Exception e) {return new LatLng(39.945, 116.404);}
		}
		else {
			return new LatLng(39.945, 116.404);
		}
	}
	
	private void saveLatestPosition(Context context, LatLng positon) {
		if (positon == null)
			return;
		try {
			String str = String.format("%f,%f", positon.latitude, positon.longitude);
			MyApp.getInstance()
					.getSharedPreferences("gis", Context.MODE_PRIVATE)
					.edit().putString("LastPosition", str).commit();
		} catch(Exception e) {/*NOOP*/};
	}


}
