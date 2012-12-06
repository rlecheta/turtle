package br.android.tarta.game.sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import br.android.tarta.R;

/**
 * View que desenha o bonequinho
 * 
 * @author ricardo
 * 
 */
public class BonecoView extends View implements AccelerometerListener {

	private float x;
	private float y;

	private float mAccelX;
	private float mAccelY;
	private float mSensorBuffer = 0;
	private Bitmap bitmap;

	private int largura;

	private int altura;

	private AccelerometerManager sensor;

	public BonecoView(Context context, Activity activity) {
		super(context);

		bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.android);

		sensor = new AccelerometerManager(context, this);

		x = 150;
		y = 150;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		largura = w;
		altura = h;

//		Log.i("sensor", "largura: " + largura);
//		Log.i("sensor", "altura: " + altura);
	}

	/**
	 * Register the accelerometer sensor so we can use it in-game.
	 */
	public void registerListener() {
		sensor.registerListener();
	}

	/**
	 * Unregister the accelerometer sensor otherwise it will continue to operate
	 * and report values.
	 */
	public void unregisterListener() {
		sensor.unregisterListener();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, x, y, paint);

		gameTick();
	}

	private void gameTick() {
		if (mAccelX > mSensorBuffer || mAccelX < -mSensorBuffer) {
			x += mAccelX;
			Log.v("sensor", "x: " + x + ", mAccelX: " + mAccelX);
			if (largura > 0) {
				if (x > largura) {
					x = largura - 50;
				} else if (x < 0) {
					x = 10;
				}
			}

		}
		if (mAccelY > mSensorBuffer || mAccelY < -mSensorBuffer) {
			y -= mAccelY;
			Log.v("sensor", "y: " + y + ", mAccelY: " + mAccelY);
			if (altura > 0) {
				if (y > altura) {
					y = altura - 50;
				} else if (y < 0) {
					y = 10;
				}
			}
		}

		// redraw the screen once our tick function is complete.
		invalidate();
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		mAccelX = x;
		mAccelY = y;
	}
}