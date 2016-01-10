package com.dzbkq.myapplication.icon;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.dzbkq.myapplication.model.ResPoint;


/**
 *
 * 
 */
public interface InterfaceIconMgr {
	void iconFactory(ResPoint p, MarkerOptions markerOptions);
	void iconFactory(ResPoint p, Marker marker);
	void release();
}
