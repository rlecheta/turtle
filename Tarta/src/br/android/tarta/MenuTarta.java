
package br.android.tarta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import br.android.tarta.game.util.AndroidUtils;
import br.android.tarta.game.util.Pref;

/**
 * Menu do jogo Tarta
 * 
 * @author ricardo
 * 
 */
public class MenuTarta extends Activity implements OnClickListener {

	private final static int WHATS_NEW_DIALOG = 0;

	private Button btIniciar;
	private Button btOpcoes;
	private Button bAjuda;
//	private Button btAbout;
	private Button btExit;
	//private Button btUpdate;
	private Button btResume;

	private int versao;

	private View layoutMenu;

	private LinearLayout layout;

	private Animation a;

	private static boolean initialized = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("MenuTarta","onCreate()");
		setContentView(R.layout.menu);
		
		layoutMenu = findViewById(R.id.layoutMenu);

		layout = (LinearLayout) findViewById(R.id.layout);
		if(layout != null) {
			AndroidUtils.animateAlpha(layout, 500);
		}

		btIniciar 	= (Button) findViewById(R.id.btIniciar);
		btResume 	= (Button) findViewById(R.id.btResume);
		btOpcoes  	= (Button) findViewById(R.id.btOpcoes);
//		bAjuda 		= (Button) findViewById(R.id.btAjuda);
//		btAbout 	= (Button) findViewById(R.id.btAbout);
		btExit 		= (Button) findViewById(R.id.btExit);
//		btUpdate		= (Button) findViewById(R.id.btUpdate);

		//btAbout.setVisibility(View.GONE);

		btIniciar.setOnClickListener(this);
		btOpcoes.setOnClickListener(this);
//		bAjuda.setOnClickListener(this);
		//btAbout.setOnClickListener(this);
		btExit.setOnClickListener(this);
		//btUpdate.setOnClickListener(this);
		btResume.setOnClickListener(this);

		JogoTarta.init(this);

		try {
			PackageManager pack = getPackageManager();
			PackageInfo packageInfo = pack.getPackageInfo("br.android.tarta", 0);
			versao = packageInfo.versionCode;
			int versaoUltima = Pref.getVersao(this);

			if(versao > versaoUltima) {

				showDialog(WHATS_NEW_DIALOG);

//				AlertDialog dialog = new AlertDialog.Builder(this)
////	        .setIcon(R.drawable.icon)
////	        .setTitle(R.string.app_name)
//			    .setMessage("Versão Atual: " + versaoUltima + " - a sua?  " + versao)
//			    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			        public void onClick(DialogInterface dialog, int whichButton) {
//			        	Pref.setVersao(MenuTarta.this,versao);
//			        }
//			    })
//			    .create();
//				dialog.show();
			}
		} catch (NameNotFoundException e) {
			JogoTarta.logError(e.getMessage(),e);
		}
		
		if(JogoTarta.FAKE) {
			Pref.setFase(this, 1);
			startActivity(new Intent(this,JogoTarta.class));	
		}

//		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
    protected Dialog onCreateDialog(int id) {
		Dialog dialog;
        if (id == WHATS_NEW_DIALOG) {
            dialog = new AlertDialog.Builder(this)
	        .setTitle(R.string.whats_new_dialog_title)
	        .setPositiveButton(R.string.ok, null)
	        .setMessage(R.string.whats_new_dialog)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Pref.setVersao(MenuTarta.this,versao);
				}
			})
	        .create();
        } else {
               dialog = super.onCreateDialog(id);
        }
        return dialog;
    }

	
	@Override
	protected void onResume() {
		super.onResume();

		// O "Continuar" não aparece se for Novo Jogo
		int fase = Pref.getFase(this);
		boolean b = fase <= 1;
		if(b) {
			btResume.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		if(v == btIniciar) {
			Pref.setFase(this, 1);
			show(this,TelaListaFase.class);
		}else if(v == btResume) {
			show(this,TelaListaFase.class);
		}else if(v == btOpcoes) {
			show(this, MenuOpcoes.class);
		}else if(v == bAjuda) {
			show(this,TelaHelp.class);
		}
//		else if(v == btAbout) {
//			Alerta.alert(this, "SuperTarta 0.9 demo");
//		}else if(v == btUpdate) {
//			String url = "http://www.livroandroid.com.br/SuperTarta.apk";
//			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
//		}
		else if(v == btExit) {
			finish();
		}
	}

	private void show(final Context context, final Class<? extends Activity> cls) {
		
		if(initialized) {
			startActivity(new Intent(context,cls));
			return;
		}
		
		a = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
		a.setFillAfter(true);
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
				startActivity(new Intent(context,cls));
			}
		});
		if(layoutMenu != null) {
			layoutMenu.startAnimation(a);
			initialized = true;
		} else {
			startActivity(new Intent(context,cls));
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		layoutMenu.clearAnimation();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		initialized = false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i("MenuTarta","ConfigChange");
	}
}
