package com.dzbkq.myapplication.task;


import com.dzbkq.myapplication.model.PointList;

/**
 *
 * @date 2015-10-16 下午4:19:31
 * 
 */
public interface IPointLoadTask {

		 void cancel();
		
		 PointList call();
}
