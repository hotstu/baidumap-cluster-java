package com.dzbkq.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dzbkq.myapplication.fragment.FragmentClusterMap;
import com.dzbkq.myapplication.fragment.FragmentLocationInfoDebug;

/**
 * @date 2015-8-12 上午11:27:30
 * 
 */
public class MapActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DebugConstants.debuginfo) {
			setContentView(R.layout.gis_activity_map_debug);
		} else {
			setContentView(R.layout.gis_activity_map);
		}

		int layerControl = getIntent().getIntExtra("layerControl", 0);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.map_container, FragmentClusterMap.newInstance(layerControl))
			.commit();
			if (DebugConstants.debuginfo) {
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.map_info, new FragmentLocationInfoDebug())
				.commit();
			}
		}
	}

}








