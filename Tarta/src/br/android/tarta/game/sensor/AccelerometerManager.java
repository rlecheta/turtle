package br.android.tarta.game.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author ricardo
 * 
 */
public class AccelerometerManager implements SensorEventListener {
	private static final String TAG = "sensor";
	public static boolean ON;
	public static boolean DEBUG_ON;
	public static String debugString;
	private SensorManager mSensorManager;
	private final AccelerometerListener listener;

	public AccelerometerManager(Context context, AccelerometerListener listener) {
		this.listener = listener;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * Register the accelerometer sensor so we can use it in-game.
	 */
	public void registerListener() {
		if (mSensorManager != null) {
			Sensor orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			if (orientation != null) {
				mSensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_GAME, null);
			}
		}
	}

	/**
	 * Unregister the accelerometer sensor otherwise it will continue to operate
	 * and report values.
	 */
	public void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				final float x = event.values[0];
				final float y = event.values[1];
				final float z = event.values[2];

				listener.onAccelerationChanged(x, y, z);

				if(DEBUG_ON) {
					Log.v(TAG,"onSensorChanged x: " + x + ", y: " + y + ", z: " + z);
					AccelerometerManager.debugString = "sensor x: " + x + ", y: " + y + ", z: " + z;
				}
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		// currently not used
	}
}