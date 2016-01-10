package com.dzbkq.bmapcluster.utils;

import com.baidu.mapapi.model.LatLng;

public class TransformUtil {

	public static LatLng GCJ2WSG(double latitude, double longitude) {
//		if (TransformUtil.outOfChina(latitude, longitude)) {
//			return new double[] { latitude, longitude };
//		}
		double[] delta = TransformUtil.delta(latitude, longitude);
		return new LatLng(latitude - delta[0], longitude - delta[1] );
	}


	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
				+ 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
				* Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
				* Math.PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
				* Math.PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
				* Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
				* Math.PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
				/ 30.0 * Math.PI)) * 2.0 / 3.0;
		return ret;
	}

	/**
	 * 
	 * @param lat纬度
	 * @param lng经度
	 * @return delta[0] 是纬度差，delta[1]是经度差
	 */
	private static double[] delta(double lat, double lng) {
		double[] delta = new double[2];
		double a = 6378245.0;
		double ee = 0.00669342162296594323;
		double dLat = transformLat(lng - 105.0, lat - 35.0);
		double dLng = transformLon(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		delta[0] = (dLat * 180.0)
				/ ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
		delta[1] = (dLng * 180.0)
				/ (a / sqrtMagic * Math.cos(radLat) * Math.PI);
		return delta;
	}
}
