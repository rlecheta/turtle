package br.android.tarta.game.sprites;

import android.graphics.Bitmap;

/**
 * Espinhos são comidas que machucam e o personagem não pode bater
 * 
 * - Exemplo: espinhos, objetos pontiagudos, fogo, etc
 * 
 * @author ricardo
 *
 */
public abstract class Espinho extends Sprite {

	public Espinho(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight);
	}

	/**
	 * Fogo se encostar queima
	 */
	public static class Fogo extends Espinho {
		public Fogo(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
		}

		@Override
		public Object clone() {
			return new Fogo(image, frameWidth, frameHeight);
		}
	}
}