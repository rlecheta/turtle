package br.android.tarta.game.controller;

import android.util.Log;
import android.view.MotionEvent;

public class MultiTouchController implements TouchController {
	
	private final TouchListener listener;

	public MultiTouchController(TouchListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		for (int i = 0; i < e.getPointerCount(); i++) {
			boolean masked = false;
			switch (e.getActionMasked()) {
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_POINTER_UP:
					masked = true;
					break;
			}
			if (masked && i != e.getActionIndex()) {
				continue;
			}
			float x = e.getX(i);
			float y = e.getY(i);
			float dx = 0;
			float dy = 0;
			if (e.getHistorySize() != 0) {
				dx = x - e.getHistoricalX(i, 0);
				dy = y - e.getHistoricalY(i, 0);
			}
			int pid = e.getPointerId(i);
			int type = 0;
			switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				type = 0;
				break;
			case MotionEvent.ACTION_MOVE:
				type = 1;
				break;
			case MotionEvent.ACTION_OUTSIDE:
				type = 1;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				type = 0;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				type = 2;
				break;
			case MotionEvent.ACTION_UP:
				type = 2;
				break;
			case MotionEvent.ACTION_CANCEL:
				type = 2;
				break;
			}
			onTouchEventMultiTouchEvent(type, (int) x, (int) y, pid + 1,(int) dx, (int) dy);
			if (masked) {
				break;
			}
		}
		
		return true;
	}

	private boolean onTouchEventMultiTouchEvent(int type, int x, int y, int pid,int dx, int dy) {
		StringBuffer sb = new StringBuffer();
		sb.append(pid);
		boolean press = true;
		if (type == 0) {
			sb.append("DOWN ");
		} else if (type == 1) {
			sb.append("MOVE ");
		} else if (type == 2) {
			sb.append("UP ");
			press = false;
		}

		if(listener != null) {
			listener.onTouch(x, y, press);
		}

		sb.append(" x/y " + x + "/" + y);

		String s = sb.toString();
		// log(s);

		return true;
	}

	@Override
	public void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.v(TAG, sb.toString());
	}
}
