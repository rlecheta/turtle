package br.android.tarta.game.sensor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Exemplo de acelerômetro
 * 
 * @author ricardo
 * 
 */
public class ExemploSensor extends Activity {

	// custom view
	private BonecoView mView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// setup our view, give it focus and display.
		mView = new BonecoView(getApplicationContext(), this);
		mView.setFocusable(true);
		setContentView(mView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mView.registerListener();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		mView.unregisterListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mView.unregisterListener();
	}

}