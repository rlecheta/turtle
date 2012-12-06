package br.android.tarta;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import br.android.tarta.game.Controle;
import br.android.tarta.game.GameManager;
import br.android.tarta.game.sensor.AccelerometerManager;
import br.android.tarta.game.util.AndroidUtils;
import br.android.tarta.game.util.Pref;
import br.android.tarta.game.util.SomUtil;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

/**
 * Activity principal
 * 
 * Inicia a thread do jogo, e controla o estado
 * 
 * @author ricardo
 *
 */
public class JogoTarta extends Activity {
	public static boolean LOG_ON = false;
	public static boolean DEV_MODE = false;

	public static final String TAG = "JogoTarta";
	
	private static final String TAG_FREE = "JogoTarta_debug";
	public static final boolean FAKE = false;
	private GameManager gameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		JogoTarta.logFree("0 - JogoTarta Activity : " + (Runtime.getRuntime().freeMemory()/1024));

		initPreferences();

		setContentView(R.layout.tarta);
		
		gameView = (GameManager) findViewById(R.id.gameview);
		gameView.setJogoTarta(this);

		admobs();
		
		//gameView = new GameManager(this);
		//setContentView(gameView);
	}

	private void admobs() {
		AdView adview = (AdView) findViewById(R.id.adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
//		String imei = AndroidUtils.getIMEI(this);
		// nexus s
		request.addTestDevice("355031040408409");
		//request.addKeyword("android");
		//request.addKeyword("game");
		adview.loadAd(request);
	}

	private void initPreferences() {
		boolean soundOn = Pref.getSoundOn(this);
		SomUtil.SOM_ON = soundOn;
		String controlMode 				= Pref.getControlMode(this);
		Controle.ON 					= "touch".equals(controlMode);
		AccelerometerManager.ON 		= "sensor".equals(controlMode);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		boolean android3 = AndroidUtils.isAndroid_3_x();
		
		MenuItem m = menu.add(0, 0, 0, getString(R.string.menu_pause));
		m.setIcon(R.drawable.pause);
//		if(android3) {
//			m.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		}
		m = menu.add(0, 1, 1, getString(R.string.menu_restart));
		m.setIcon(R.drawable.restart);
//		if(android3) {
//			m.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem itemPause = menu.getItem(0);
		if(gameView.state == GameManager.PAUSE) {
			itemPause.setIcon(R.drawable.resume);
			itemPause.setTitle(R.string.menu_continue);
		} else {
			itemPause.setIcon(R.drawable.pause);
			itemPause.setTitle(R.string.menu_pause);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case 0:
			if(gameView.state == GameManager.PAUSE) {
				gameView.resume();
			}else {
				gameView.pause();
			}
			break;
		case 1:
			gameView.restartLevel();
			break;
		}
		return true;
	}
	
	public static void logTrace(String msg) {
		if(LOG_ON){
			Log.v(JogoTarta.TAG, msg);
		}
	}
	public static void logDebug(String msg) {
		if(LOG_ON){
			Log.d(JogoTarta.TAG, msg);
		}
	}
	public static void log(String msg) {
		if(LOG_ON){
			Log.i(JogoTarta.TAG, msg);
		}
	}
	public static void logFree(String msg) {
		if(LOG_ON){
			Log.i(JogoTarta.TAG_FREE, msg);
		}
	}
	public static void log(String tag,String msg) {
		if(LOG_ON){
			Log.i(tag, msg);
		}
	}
	public static void logError(String string, Exception e) {
		Log.e(TAG, string, e);
	}

	public static void logError(String s) {
		Log.e(TAG, s);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (gameView != null) {
			int fase = gameView.fase;
			Pref.setFase(this, fase);
		}
		
		SomUtil.pause();
//		log("onPause() SomUtil.close() OK.");
	}

	protected void onResume() {
		super.onResume();
		if(gameView != null) {
			// registra no init()
			//gameView.registerListener();
		}
		
		if(gameView.state == GameManager.PAUSE)
		{
			gameView.resume();
		}
		
		SomUtil.resume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		SomUtil.close();

		if (gameView != null) {
			gameView.stopThread();
			gameView.unregisterListener();
		}

		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (gameView != null) {
			gameView.unregisterListener();
		}
		
		SomUtil.close();

//		gameView.stopThread();

	}

	public static void gc() {
		System.gc();		
	}

	public static void init(Context context) {
		LOG_ON = false;
		DEV_MODE = false;
		
		if(AndroidUtils.isEmuladororHTC(context)) {
			//LOG_ON = true;
			DEV_MODE = true;
		}
	}
}