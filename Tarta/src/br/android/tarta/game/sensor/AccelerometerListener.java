package br.android.tarta.game.sensor;

/**
 * Simplesmente implemente esta interface e bingo
 * 
 * @author ricardo
 *
 */
public interface AccelerometerListener {
	 
	/**
	 * Mudou o sensor
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void onAccelerationChanged(float x, float y, float z);
 
}