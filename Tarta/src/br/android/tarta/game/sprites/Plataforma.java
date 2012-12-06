package br.android.tarta.game.sprites;

import android.graphics.Bitmap;
import br.android.tarta.R;

/**
 * Mosquito fica voando
 */
public class Plataforma extends Personagem {
	public static final int ICON = R.drawable.plataforma;

	public Plataforma(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight, 1);
		
		TIRO_CAN_KILL = false;

		setMaxSpeed(0.1f);

		//setFrame(new int[]{0,1});
		//setFrameMorrendo(new int[]{2,3});
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