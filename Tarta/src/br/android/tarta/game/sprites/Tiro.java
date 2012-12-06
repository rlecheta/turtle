package br.android.tarta.game.sprites;

import android.graphics.Bitmap;
import br.android.tarta.R;

/**
 * Mosquito fica voando
 */
public class Tiro extends Personagem {
	public static final int ICON = R.drawable.tiro;
	private static final int[] frameAnimacao = new int[]{15,13,11,9,7,5};

	public Tiro(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight, 1);

		setMaxSpeed(0.1f);
		TIME_MOVE = 100;

		//setFrame(new int[]{15,14,13,12,11,10,9,8,7,6,5});
		setFrame(frameAnimacao);
		setFrameMorrendo(new int[]{16,17,18,19,20,21,22});

		//setTransform(javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
	}
	
	@Override
	public void setState(int state) {
		super.setState(state);
		if (state == STATE_ALIVE) {
			// Volta o frame para o estado inicial
			setFrame(frameAnimacao);
		}
	}

	public boolean collideHorizontal() {
		setState(STATE_GONE);
		return false;
	}
	
	@Override
	protected void updateFrame(long elapsedTime) {
		super.updateFrame(elapsedTime);
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
	}

	@Override
	public Object clone() {
		// Nao cria outra instancia (para sempre atualizar a mesma)
		return this;
	}

	@Override
	public int getIcon() {
		return ICON;
	}
}