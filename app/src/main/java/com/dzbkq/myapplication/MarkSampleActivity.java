package com.dzbkq.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dzbkq.myapplication.fragment.FragmentMarkerTest;


/**
 *
 * 
 */
public class MarkSampleActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gis_activity_fragment_container);
		if (savedInstanceState == null) {
			
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, new FragmentMarkerTest())
			.commit();
		}
	}

}








