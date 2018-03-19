package com.dzbkq.myapplication.task;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.dzbkq.myapplication.OpenSimplexNoise;
import com.dzbkq.myapplication.model.MetaResPoint;
import com.dzbkq.myapplication.model.PointList;
import com.dzbkq.myapplication.model.ResPoint;

/**
 * @date 2015-8-21 下午3:33:21
 */
public class PerlinPointsLoadTask implements IPointLoadTask {
    private static final String TAG = "ResPointsLoadTask";
    final Context mContext;
    final CoordinateConverter converter;
    private boolean enabled;

    public PerlinPointsLoadTask(Context context) {
        this.mContext = context;
        converter = new CoordinateConverter();
        converter.from(CoordType.GPS);
    }


    @Override
    public PointList call() {
        try {
            return callInner();
        } catch (Exception e) {
            final PointList ret = new PointList();
            ret.isSuccess = false;
            ret.errMsg = "发生未知异常:" + (e == null ? "null" : e.getMessage());
            return ret;
        }
    }


    public PointList callInner() {
        OpenSimplexNoise noise = new OpenSimplexNoise();
        final PointList ret = new PointList();
        if (!enabled) {
            ret.isSuccess = false;
            ret.errMsg = "task is cancled";
            ret.errType = 1;
        }
        double lat = 39.945;
        double lng = 116.404;
        double fraction = 0.8;
        for (int i = -100; i < 100; i++) {
            for (int j = -100; j < 100; j++) {
                double value;

                if ((value = noise.eval(i, j)) > fraction) {
                    ResPoint temp = new ResPoint(lat + i * 0.01, lng + j * 0.01);
                    MetaResPoint meta = new MetaResPoint();
                    meta.lineId = (int) ((value - fraction)/(1-fraction) * 10) + "";
                    meta.value = value * 100;
                    temp.meta = meta;
                    ret.add(temp);
                }
            }
        }
        Log.e(TAG, "callInner: " +  ret.size() );
        ret.isSuccess = true;
        return ret;
    }


    public void cancel() {
        enabled = false;
    }


}
