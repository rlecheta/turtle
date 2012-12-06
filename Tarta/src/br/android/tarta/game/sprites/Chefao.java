package br.android.tarta.game.sprites;

import br.android.tarta.R;
import android.graphics.Bitmap;

/**
 * Aranha
 * 
 * @author ricardo
 *
 */
public class Chefao extends Personagem {

	public Chefao(Bitmap image, int frameWidth, int frameHeight,int vidas) {
		super(image, frameWidth, frameHeight,vidas);

		setMaxSpeed(0.1f);

		chefao = true;

		TIME_STATE_DYING = 3500;

		setFrame(new int[]{0,1});
		setFrameMorrendo(new int[]{2,3});
		
		// 0-20
		int frame[] = new int[20];
		for (int i = 0; i < frame.length; i++) {
			frame[i] = i;
		}
		setFrame(frame);

		// 20-30
		int frameMorrendo[] = new int[10];
		int j = 0;
		for (int i = 20; i < 30; i++) {
			frameMorrendo[j++] = i;
		}
		setFrameMorrendo(frameMorrendo);
	}

	@Override
	public Object clone() {
		Chefao c = new Chefao(image, frameWidth, frameHeight,vidas);
		return c;
	}

	@Override
	public int getIcon() {
		return R.drawable.interface_aranha;
	}
}