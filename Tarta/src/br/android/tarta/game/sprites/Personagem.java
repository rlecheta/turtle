package br.android.tarta.game.sprites;

import android.graphics.Bitmap;

/**
 * Personagem é um Sprite que se move.
 * 
 * Pode ser inimigos ou o Player
 * 
 * @author rlecheta
 */
public abstract class Personagem extends Sprite {
	
	public boolean TIRO_CAN_KILL = true;

	private float maxSpeed;
	public int vidas;
	public boolean chefao;

	/**
	 * Cria uma nova criatura com as animações especificadas.
	 */
	public Personagem(Bitmap image, int frameWidth, int frameHeight,int vidas) {
		super(image, frameWidth, frameHeight);
		this.setMaxSpeed(0);
		setState(STATE_ALIVE);

		TIME_STATE_DYING = 1500;

		this.vidas = vidas;
	}

	/**
	 * Configura a velocidade máxima dessa criatura.
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Obtém a velocidade máxima dessa criatura.
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Acorda
	 * @param player 
	 */
	public void wakeUp(Player player) {
		if (state == STATE_ALIVE && moveX == 0) {
			if(x >= player.x) {
				moveX = -maxSpeed;
			}else if(x < player.x) {
				moveX = maxSpeed;
			}
		}
	}

	/**
	 * Chamado antes de update() se a criatura colidiu com um tile
	 * horizontalmente.
	 */
	public boolean collideHorizontal() {
		moveX = -moveX;
		return true;
	}

	/**
	 * Chamado antes de update() se a criatura colidiu com um tile
	 * verticalmente.
	 */
	public void collideVertical() {
		moveY = 0;
	}

	/**
	 * Verifica se esta criatura está voando.
	 */
	public boolean isFlying() {
		return false;
	}
}