package com.dzbkq.myapplication.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dzbkq.myapplication.GisEventFactory;
import com.dzbkq.myapplication.LocationRecorder;
import com.dzbkq.myapplication.R;

/**
 *
 * 
 */
public class FragmentLocationInfoDebug extends Fragment implements OnClickListener{
	TextView tvInfo;
	BroadcastReceiver mReceiver;
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.gis_fragment_locationinfo_debug, container, false);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		tvInfo.setOnClickListener(this);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		onNewLocation(LocationRecorder.getInstance().get());
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				switch (action) {
				case GisEventFactory.ACTION_POINT_ADD:
					Location  l = (Location) intent.getParcelableExtra("point");
					onNewLocation(l);
					break;        
				default:
					break;
				}
				
			}

		};
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mReceiver, new IntentFilter(GisEventFactory.ACTION_POINT_ADD));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() != tvInfo.getId())
			return;
		final String text = tvInfo.getText().toString();
		if ("".equals(text))
			return;
		shareText(getActivity(), text);
	}
	
	private void onNewLocation(Location l) {
		if (tvInfo == null || l == null)
			return;
		tvInfo.setText("当前坐标:" + l.getLatitude() + ", " + l.getLongitude());
		
	}
	
	private void shareText(Context context, String text) {
		Intent sendIntent = new Intent();
	    sendIntent.setAction(Intent.ACTION_SEND);
	    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
	    sendIntent.setType("text/plain");
	    try {
			context.startActivity(Intent.createChooser(sendIntent, "Share via..."));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}















