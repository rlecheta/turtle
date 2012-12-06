package br.android.tarta.game.controller;

import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

public class ControllerFactory {
	
	private static boolean multiTouchSupported;

	static {
		multiTouchSupported = checkMultiTouchSupported();
	}

	public static TouchController getController(TouchListener listener) {
		int apiLevel = Integer.parseInt(Build.VERSION.SDK);

		if(!multiTouchSupported) {
			return new SingleTouchController(listener);
		}

		// É Android 2.x se API Level >= 5
		boolean android2 = apiLevel >= 5;

		TouchController c = android2 ? new MultiTouchController(listener) : new SingleTouchController(listener);

		return c;
	}

	public static boolean isMultiTouchSupported() {
		return multiTouchSupported;
	}
	
	public static boolean checkMultiTouchSupported() {
		boolean succeeded = false;
		try {
			// Android 2.0.1 stuff:
			MotionEvent.class.getMethod("getPointerCount");
			MotionEvent.class.getMethod("getPointerId", Integer.TYPE);
			MotionEvent.class.getMethod("getPressure", Integer.TYPE);
			MotionEvent.class.getMethod("getHistoricalX", Integer.TYPE, Integer.TYPE);
			MotionEvent.class.getMethod("getHistoricalY", Integer.TYPE, Integer.TYPE);
			MotionEvent.class.getMethod("getHistoricalPressure", Integer.TYPE, Integer.TYPE);
			MotionEvent.class.getMethod("getX", Integer.TYPE);
			MotionEvent.class.getMethod("getY", Integer.TYPE);
			MotionEvent.class.getMethod("getActionMasked");
			succeeded = true;
		} catch (Exception e) {
			Log.e(TouchController.TAG, "checkMultiTouchSupported = falase: " + e.getMessage(), e);
		}
		return succeeded;
	}

	public static void log(String string) {
		if(TouchController.LOG_ON) {
			Log.d(TouchController.TAG, string);
		}
	}
}
