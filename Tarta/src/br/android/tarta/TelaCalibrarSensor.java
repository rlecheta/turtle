package br.android.tarta;

import br.android.tarta.game.sensor.AccelerometerListener;
import br.android.tarta.game.sensor.AccelerometerManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Tela utilizada para calibrar o sensor
 * 
 * @author ricardo
 *
 */
public class TelaCalibrarSensor extends Activity implements OnClickListener, AccelerometerListener {

	private AccelerometerManager sensor;
	private String x;
	private TextView t;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.calibrar);

		t = (TextView) findViewById(R.id.tCalibrar);

		Button b = (Button) findViewById(R.id.btCalibrar);
		b.setOnClickListener(this);

		sensor = new AccelerometerManager(this, this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		sensor.registerListener();
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		sensor.unregisterListener();
	}

	@Override
	public void onClick(View view) {
		Toast.makeText(this, "Calibrado!", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		//String s = "sensor x: " + x + ", y: " + y + ", z: " + z;
		String s = "Value : " + y;
		t.setText(s);
	}
}