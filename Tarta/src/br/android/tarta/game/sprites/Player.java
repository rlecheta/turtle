package br.android.tarta.game.sprites;

import android.graphics.Bitmap;

/**
 * O jogador (Tartaruga)
 * 
 * @author ricardo
 * 
 */
public class Player extends Personagem {
	private static final int[] frameParado = new int[] { 1 };
	private static final int[] frameAndando = new int[] { 0, 1 };
	private static final int[] frameAbaixado = new int[] { 2 };
	private static final int[] frameCima = new int[] { 3, 4 };

	private static final float JUMP_SPEED = -.7f;

	protected boolean onGround;

	// indica se está abaixado
	public boolean down;

	public boolean pulando;
	public int qtdeTiros=1;

	public Player(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight,0);

		// Primeiro frame de animacao
		this.frame = this.frameParado;

		frameMorrendo = new int[] { 5, 6 };
		TIME_MOVE_DIE = 500;

		right = true;
		
		setMaxSpeed(0.2f);
		down = false;

		TIME_STATE_DYING = 2500;
	}

	@Override
	public Player clone() {
		return new Player(image, frameWidth, frameHeight);
	}

	public boolean collideHorizontal() {
		moveX = 0;
		return true;
	}

	public void collideVertical() {
		// verifica se colidiu com o chão
		if (moveY > 0) {
			onGround = true;
			pulando = false;
		}
		moveY = 0;
	}

	public void setY(float y) {
		// verifica se está caindo
		if (Math.round(y) > Math.round(this.y)) {
			onGround = false;
		}
		super.setY(y);
	}

	public void wakeUp() {
		// não faz nada
	}

	/**
	 * Faz o jogador pular se o mesmo estiver no chão ou então se forceJump é
	 * true.
	 */
	public void jump(boolean forceJump) {
		if (onGround || forceJump) {
			onGround = false;
			moveY = JUMP_SPEED;
		}
		if (!onGround) {
			pulando = true;
		} else {
			pulando = false;
		}
	}
	
	@Override
	protected void setFrame(int[] frame) {
		super.setFrame(frame);
	}
	
	/**
	 * Atualiza a animação desse jogador.
	 */
	public void update(long elapsedTime) {

		// Pulando
		if (moveY < 0) {
			setFrame(frameCima);
			pulando = true;
		}

		if (moveX < 0) {
			// Andando Para Esquerda
			if (!pulando) {
				setFrame(frameAndando);
			}
			right = false;
		} else if (moveX > 0) {
			// Andando para Direita
			if (!pulando) {
				setFrame(frameAndando);
			}
			right = true;

		} else {
			// Está parado
			if (down) {
				setFrame(frameAbaixado);
			} else if (pulando) {
				setFrame(frameCima);
			} else {
				// Está parado
				setFrame(frameParado);
			}
		}

		// Faz a transformacao na imagem
		if (right) {
			turnRight();
		} else {
			turnLeft();
		}

		updateFrame(elapsedTime);
	}
	
	@Override
	protected void updateFrame(long elapsedTime) {
		super.updateFrame(elapsedTime);
	}

	private void turnLeft() {
		setTransform(javax.microedition.lcdui.game.Sprite.TRANS_NONE);
	}

	private void turnRight() {
		setTransform(javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
	}

	/**
	 * Reinicia a posição do jogador ao iniciar cada fase
	 */
	public void reset() {
//		int x = TileMap.tilesToPixels(3);
//		setX(x);
//		setY(0);
//		turnRight();
	}

	@Override
	public int getIcon() {
		return 0;
	}
}