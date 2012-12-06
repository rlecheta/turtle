package br.android.tarta.game.sprites;

import br.android.tarta.R;
import android.graphics.Bitmap;

/**
 * Mosquito fica voando
 */
public class Abelha extends Personagem {

	public Abelha(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight,1);

		setMaxSpeed(0.1f);
		
		setFrame(new int[]{0,1});
		setFrameMorrendo(new int[]{2,3});
	}

	public boolean isFlying() {
		return state == STATE_ALIVE;
	}

	@Override
	public Object clone() {
		return new Abelha(image, frameWidth, frameHeight);
	}

	@Override
	public int getIcon() {
		return R.drawable.interface_abelha;
	}
}