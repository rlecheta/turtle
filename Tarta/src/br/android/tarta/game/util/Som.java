package br.android.tarta.game.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import br.android.tarta.JogoTarta;

/**
 * Controla o Som
 * 
 * @author ricardo
 *
 */
public class Som implements OnCompletionListener {
	private MediaPlayer mp;
	
	private int status;

	private int READY 		= 1;
	private int PLAYING		= 2;
	private int COMPLETED 	= 3;
	private Runnable runnable;

	public Som(Context context, int resId) {
		this(context,resId,null);
	}

	public Som(Context context, int resId,Runnable runnable) {
		mp = MediaPlayer.create(context, resId);
		mp.setOnCompletionListener(this);
		status = READY;
		this.runnable = runnable;
	}
	
	public Som(Context context, int resId,boolean loop) {
		mp = MediaPlayer.create(context, resId);
		mp.setLooping(loop);
		mp.setOnCompletionListener(this);

		status = READY;
	}

	public void start() {
		if(status == COMPLETED) {
			try {
				mp.stop();
				mp.prepare();

				status = READY;

			} catch (Exception e) {
				JogoTarta.logError(e.getMessage(),e);
			}
		}

		if (status == READY) {
			status = PLAYING;
			mp.start();
		}
	}

	@Override
	public void onCompletion(MediaPlayer m) {
		status = COMPLETED;
		if(runnable != null) {
			runnable.run();
		}
	}

	public void pause() {
		mp.pause();
	}

	public void stop() {
		mp.stop();
	}

	public void release() {
		mp.release();
	}
}
