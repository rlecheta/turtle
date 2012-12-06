package br.android.tarta.game.controller;

import android.util.Log;
import android.view.MotionEvent;

public class SingleTouchController implements TouchController {

	private final TouchListener listener;

	public SingleTouchController(TouchListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int x = Math.round(ev.getX());
		int y = Math.round(ev.getY());

		int action = ev.getAction();
		switch (action) {
	        case MotionEvent.ACTION_DOWN:
	        	listener.onTouch(x, y, true);
	        	break;
	        case MotionEvent.ACTION_MOVE:

	            break;
	        case MotionEvent.ACTION_UP:
	        	if(TouchController.LOG_ON){
	        		ControllerFactory.log("UP release x:y " + ev.getX() + ": " + ev.getY());
	        	}
	            listener.release();
	            break;
		}

		return true;
	}

	@Override
	public void dumpEvent(MotionEvent event) {
		int action = event.getAction();
		String s = "action("+action+") - x/y: " + event.getX() + "/" + event.getY();
		Log.v(TAG, s);
	}
}
