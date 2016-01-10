package com.dzbkq.myapplication.model;

import java.util.ArrayList;


/**
 *
 * 
 */
public class PointList extends ArrayList<ResPoint> {
	public boolean isSuccess = true;
	public String errMsg = null;
	public int errType = 0;
	//ugly
	public Object extra = null;
	public Object extra2 = null;

}
