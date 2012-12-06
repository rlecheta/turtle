package br.android.tarta.game.sprites;

import android.graphics.Bitmap;

/**
 * A comida do jogo (estrela, ovinho, etc)
 * 
 * @author ricardo
 *
 */
public abstract class Comida extends Sprite {

	public Comida(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight);
	}

	/**
	 * O Ovinho para pegar
	 */
	public static class Ovo extends Comida {
		private static final int[] FRAME_ANIM_OVO = new int[]{0,1,2,1,0,0,3,4};

		public Ovo(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
			setFrame(FRAME_ANIM_OVO);
		}

		@Override
		public Object clone() {
			return new Ovo(image, frameWidth, frameHeight);
		}
	}

	/**
	 * Comida grande que da pontos
	 * 
	 * O PowerUp Mushroom faz o jogador crescer.
	 */
	public static class Ovao extends Comida {
		private static final int[] FRAME_ANIM_OVO = new int[]{4,3,0,0,1,2,2,1,1,0};

		public Ovao(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
			TIME_MOVE = 300;
			setFrame(FRAME_ANIM_OVO);
		}

		@Override
		public Object clone() {
			return new Ovao(image, frameWidth, frameHeight);
		}
	}

	/**
	 * Se pegar ganha 1 vida
	 */
	public static class Vida extends Comida {
		public Vida(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
			TIME_MOVE = 150;
		}

		@Override
		public Object clone() {
			return new Vida(image, frameWidth, frameHeight);
		}
	}

	/**
	 * A comida FireFlower dá ao jogador poder de jogar fogo.
	 */
	public static class FireFlower extends Comida {
		public FireFlower(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
			
			setFrame(new int[]{0,1,2,3,4});
			//setFrameMorrendo(new int[]{2,3});]
		}

		@Override
		public Object clone() {
			return new FireFlower(image, frameWidth, frameHeight);
		}
	}

	/**
	 * A comida Estrela. Avança para o próximo mapa.
	 */
	public static class Estrela extends Comida {
		public Estrela(Bitmap image, int frameWidth, int frameHeight) {
			super(image, frameWidth, frameHeight);
		}

		@Override
		public Object clone() {
			return new Estrela(image, frameWidth, frameHeight);
		}
	}

}