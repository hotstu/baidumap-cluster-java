package com.dzbkq.myapplication.task;

import android.content.Context;
import android.content.res.AssetManager;

import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.dzbkq.myapplication.model.MetaResPoint;
import com.dzbkq.myapplication.model.PointList;
import com.dzbkq.myapplication.model.ResPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @date 2015-8-21 下午3:33:21
 * 
 */
public class ResPointsLoadTask implements IPointLoadTask {
	private static final String TAG = "ResPointsLoadTask";
	final Context mContext;
	final CoordinateConverter converter;
	private boolean enabled;

	public ResPointsLoadTask(Context context) {
		this.mContext = context;
		converter = new CoordinateConverter();
		converter.from(CoordType.GPS);  
	}
	

	@Override
	public PointList call() {
		try {
			return callInner();
		} catch(Exception e) {
			final PointList ret = new PointList();
			ret.isSuccess = false;
			ret.errMsg = "发生未知异常:" + (e == null? "null" : e.getMessage());
			return ret;
		}
	}


	public PointList callInner() {
		final PointList ret = new PointList();
		if (!enabled) {
			ret.isSuccess = false;
			ret.errMsg = "task is cancled";
			ret.errType = 1;
		}
		double lat = 39.945;
		double lng = 116.404;
		double radus = 1;
		for (int i = 0; i < 1000; i++) {
			double dx = radus * Math.cos((i % 40) * (2 * Math.PI) / 40);
			double dy = radus * Math.sin((i % 40) * (2 * Math.PI) / 40);
			if (i > 0 && i % 40 == 0) {
				radus +=  1;
			}

			ResPoint temp = new ResPoint(lat + dx*0.01, lng + dy*0.01);
			MetaResPoint meta = new MetaResPoint();
			meta.lineId  = (i % 10) + "";
			temp.meta = meta;
			ret.add(temp);
		}

		ret.isSuccess = true;
		return ret;
	}
	


	public void cancel() {
		enabled = false;
	}


	
	private String loadLocal() throws IOException {
		AssetManager am = mContext.getAssets();
		BufferedReader bin = new BufferedReader(new InputStreamReader(am.open("respointsample2.json")));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = bin.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
		}

}
