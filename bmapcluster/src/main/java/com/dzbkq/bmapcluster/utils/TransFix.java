package com.dzbkq.bmapcluster.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;


/**
 *  
 * @Description:
 * @date 2015-8-28 上午8:15:55
 * 
 */
public class TransFix {
	private static final String TAG = TransFix.class.getSimpleName();
	// jzA = 6378245.0, 1/f = 298.3
	// b = a * (1 - f)
	// ee = (a^2 - b^2) / a^2;
	final double jzA = 6378245.0;
	final double jzEE = 0.00669342162296594323;
	/**
	 * bd09 to gcj02
	 * @param lat
	 * @param lng
	 * @return  gcj02
	 */
	LatLng bd09Decrypt(double lat, double lng) {
		double x = lng - 0.0065, y = lat - 0.006;
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
	    double longitude = z * Math.cos(theta);
	    double latitude = z * Math.sin(theta);
	    return new LatLng(latitude, longitude);
	}
	
	LatLng gcj02Decrypt(double lat, double lng) {
	    LatLng  gPt = gcj02Encrypt(lat, lng);
	    double dLon = gPt.longitude - lng;
	    double dLat = gPt.latitude - lat;
	    double latitude = lat - dLat;
	    double longitude = lng - dLon;
	    return new LatLng(latitude, longitude);
	}
	
	LatLng gcj02Encrypt(double lat, double lon) {
	    double mgLat;
	    double mgLon;

	    double dLat = transformLat(lon - 105.0, lat - 35.0);
	    double dLon = transformLon(lon - 105.0, lat - 35.0);
	    double radLat = lat / 180.0 * Math.PI;
	    double magic = Math.sin(radLat);
	    magic = 1 - jzEE * magic * magic;
	    double sqrtMagic = Math.sqrt(magic);
	    dLat = (dLat * 180.0) / ((jzA * (1 - jzEE)) / (magic * sqrtMagic) * Math.PI);
	    dLon = (dLon * 180.0) / (jzA / sqrtMagic * Math.cos(radLat) * Math.PI);
	    mgLat = lat + dLat;
	    mgLon = lon + dLon;

	    return new LatLng(mgLat, mgLon);
	}
	
	double transformLat( double x, double y)
	{
	    double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
	    ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
	    ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
	    return ret;
	}

	double transformLon(double x, double y) 
	{
	    double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
	    ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
	    ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
	    return ret;
	}
	
	LatLng bd09ToGcj02(LatLng location)
	{
	    return bd09Decrypt(location.latitude, location.longitude);
	}
	
	public LatLng bd09ToWgs84(LatLng location)
	{
		LatLng gcj02 = bd09ToGcj02(location);
	    return gcj02Decrypt(gcj02.latitude, gcj02.longitude);
	}
	
	public LatLng midlleTrans(LatLng middle) {
		LatLng p2 = bd09ToWgs84(middle);
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		p2 = converter.coord(p2).convert();
		LatLng p1 = new LatLng(2* middle.latitude - p2.latitude, 2 * middle.longitude - p2.longitude);
		return p1;
	}
	
	public LatLng wgs84ToBd09(LatLng src) {
		return new CoordinateConverter().from(CoordType.GPS).coord(src).convert();
	}
	
	/**
	 * 从from 出发逼近to, 找出最小距离的wgs84点, 
	 * @param from wgs84坐标
	 * @param to bd09坐标
	 * @param depth 递归深度, 初始建议设为20, 当depth <=0 直接返回 from
	 * @return
	 */
	public LatLng nearByPointWhoHaveMindistance(LatLng from, LatLng to, int depth) {
		if (depth <= 0)
			return from;
		double decent = 0.00003;
		double distance0 = DistanceUtil.getDistance(wgs84ToBd09(from), to);
		Log.d(TAG, "" + from + " depth:"+ depth + " distance:" + distance0);
		LatLng adj1 = new LatLng(from.latitude + decent, from.longitude);
		LatLng adj2 = new LatLng(from.latitude - decent, from.longitude);
		LatLng adj3 = new LatLng(from.latitude, from.longitude + decent);
		LatLng adj4 = new LatLng(from.latitude, from.longitude - decent);
		LatLng minPostion = from;
		double minDistance = distance0;
		
		double distance1 = DistanceUtil.getDistance(wgs84ToBd09(adj1), to);
		if (distance1 < minDistance) {
			minPostion = adj1;
			minDistance = distance1;
		}
		double distance2 = DistanceUtil.getDistance(wgs84ToBd09(adj2), to);
		if (distance2 < minDistance) {
			minPostion = adj2;
			minDistance = distance2;
		}
		double distance3 = DistanceUtil.getDistance(wgs84ToBd09(adj3), to);
		if (distance3 < minDistance) {
			minPostion = adj3;
			minDistance = distance3;
		}
		double distance4 = DistanceUtil.getDistance(wgs84ToBd09(adj4), to);
		if (distance4 < minDistance) {
			minPostion = adj4;
			minDistance = distance4;
		}
		if (minPostion.equals(from)) {
			return from;
		}
		else {
			return nearByPointWhoHaveMindistance(minPostion, to, depth - 1);
		}
	}

}










