package com.dzbkq.myapplication.model;

import com.baidu.mapapi.model.LatLng;
import com.dzbkq.bmapcluster.cluster.ClusterItem;

import java.io.Serializable;

/**
 *
 * 
 */
public class ResPoint implements ClusterItem, Serializable {
	public String id;
	public double lng;
	public double lat;
	public Imeta meta;
	

	public ResPoint(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public ResPoint(LatLng ll) {
		this.lat = ll.latitude;
		this.lng = ll.longitude;
	}

	@Override
	public LatLng getPosition() {
		return new LatLng(lat, lng);
	}

	@Override
	public String toString() {
		return "point[" + lat + ", " + lng + "]";
	}

}
