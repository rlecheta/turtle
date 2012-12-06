package br.android.tarta.game.controller;

import android.view.MotionEvent;

public interface TouchController {
	public static final String TAG = "Controller";
	public static final boolean LOG_ON = false;
	
	public boolean onTouchEvent(MotionEvent ev);
	
	public void dumpEvent(MotionEvent event);

}
