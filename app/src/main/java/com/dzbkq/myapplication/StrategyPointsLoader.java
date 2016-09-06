package com.dzbkq.myapplication;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.dzbkq.myapplication.model.PointList;
import com.dzbkq.myapplication.task.DummyPointListLoadTask;
import com.dzbkq.myapplication.task.IPointLoadTask;
import com.dzbkq.myapplication.task.PerlinPointsLoadTask;
import com.dzbkq.myapplication.task.ResPointsLoadTask;


/**
 * 根据点类型选择不同的点加载器
 *
 * @version: V1.0
 * @date 2015-8-20 上午10:54:45
 */
public class StrategyPointsLoader extends AsyncTaskLoader<PointList> {
    private IPointLoadTask currentTask = null;

    /**
     * @param context
     * @param type
     * @param extra
     */
    public StrategyPointsLoader(Context context, int type, Object... extra) {
        super(context);
        switch (type) {
            case 0:
                currentTask = new PerlinPointsLoadTask(context);
                break;
            case 1:
                currentTask = new ResPointsLoadTask(context);
                break;
            default:
                currentTask = new DummyPointListLoadTask();
                break;
        }
    }

    @Override
    public PointList loadInBackground() {
        //debug
        if (currentTask == null) {
            Log.e("StrategyPointsLoader", "currentTask == null!!!");
        }
        return currentTask.call();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.d("PointsLoader", "onStopLoading");
        cancelLoad();
        if (currentTask != null) {
            currentTask.cancel();
        }
    }


}
