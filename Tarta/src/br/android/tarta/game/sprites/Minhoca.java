package br.android.tarta.game.sprites;

import br.android.tarta.R;
import android.graphics.Bitmap;

/**
 * Um Goomba é uma Creature que se move devagar no chão.
 */
public class Minhoca extends Personagem {

	private final Bitmap image;

	public Minhoca(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight,1);
		this.image = image;

		setMaxSpeed(0.05f);
		
		// Frames: andando e morrendo
		setFrame(new int[]{0,1});
		setFrameMorrendo(new int[]{2,3});
	}

	@Override
	public Minhoca clone() {
		return new Minhoca(image, frameWidth, frameHeight);
	}
	
	@Override
	public int getIcon() {
		return R.drawable.interface_minhoca;
	}
}