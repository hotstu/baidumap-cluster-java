package com.dzbkq.myapplication.task;


import com.dzbkq.myapplication.model.PointList;

/**
 * @date 2015-9-25 上午8:43:53
 * 
 */
public class DummyPointListLoadTask implements IPointLoadTask {

	public DummyPointListLoadTask() {
	}

	@Override
	public PointList call()  {
		return new PointList();
	}

	@Override
	public void cancel() {
		
	}

}
