package com.dzbkq.myapplication;

import android.content.Intent;
import android.location.Location;


/**
 *
 * 
 */
public class GisEventFactory {
	public static final int pointModifyEevent = 1;		//0001
	public static final int pointLabelEvent = 2;		//0010
	public static final int pointarrivalEvent = 4;      //0100
	public static final int requiretrackupdate = 8;		//1000
	public  static final String ACTION = "EventFactory_Action";
	public static final String ACTION_POINT_ADD = "ACTION_POINT_ADD";
	private static final String TAG = GisEventFactory.class.getSimpleName();


	
	public static void sendPointArrivalEvent(Location l) {
		Intent i = new Intent();
		i.setAction(ACTION_POINT_ADD);
		i.putExtra("method", pointarrivalEvent);
		i.putExtra("point", l);
		MyApp.getInstance().sendBroadcast(i);
	}

	
}







