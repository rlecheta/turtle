package br.android.tarta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import br.android.tarta.game.util.AndroidUtils;

/**
 * Menu do jogo Tarta
 * 
 * @author ricardo
 * 
 */
public class MenuOpcoes extends Activity implements OnClickListener {
	private Button btConfig;
	private Button btCalibrar;
	private Button btVoltar;
	private LinearLayout layout;
//	private Button btSinc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.menu_op);
		
		layout = (LinearLayout) findViewById(R.id.layout);
		AndroidUtils.animateAlpha(layout, 500);

		btConfig 	= (Button) findViewById(R.id.btConfig);
		//btCalibrar 	= (Button) findViewById(R.id.btCalibrar);
//		btSinc  	= (Button) findViewById(R.id.btSinc);
		btVoltar  	= (Button) findViewById(R.id.btVoltar);

		btConfig.setOnClickListener(this);
		//btCalibrar.setOnClickListener(this);
//		btSinc.setOnClickListener(this);
		btVoltar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btConfig) {
			Animation a = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
			a.setDuration(200);
			a.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					startActivity(new Intent(MenuOpcoes.this,PreferencesFromXml.class));
				}
			});
			btConfig.startAnimation(a);
			
		}else if(v == btCalibrar) {
			startActivity(new Intent(this,TelaCalibrarSensor.class));
		}
		/*else if(v == btSinc) {
			final Context context = this;
			new Thread(){public void run() {
				HttpHelper http = new HttpHelper(context);
				try {
					Log.i("Tarta", "Carregando...");
					http.doGet("http://gamerunningturtle.appspot.com/getCenario.htm?id=2001");
					String s = http.getString();
					TelaListaFase.FASE_STR = s;
					Log.i("Tarta", "Fase carregada\n"+s);
				} catch (IOException e) {
					JogoTarta.logError(e.getMessage(), e);
				}
			}}.start();
		}*/
		else if(v == btVoltar) {
			finish();
		}
	}
}
