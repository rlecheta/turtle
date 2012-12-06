package br.android.tarta.game.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * A classe Sprite define uma entidade do jogo, algo que recebe animações.
 * 
 * @author rlecheta
 */
public abstract class Sprite extends javax.microedition.lcdui.game.Sprite {

	// velocidade (pixels por milisegundo)
	public float moveX;
	public float moveY;

	protected final Bitmap image;
	protected final int frameWidth;
	protected final int frameHeight;
	
	/**
	 * Move a animaçãop dos Sprites a cada 200 ms (0.20 segundos).
	 * 
	 */
	protected int TIME_MOVE = 200;
	protected int TIME_MOVE_DIE = 200;
	protected int idx;
	protected int[] frame;

	protected long totalTime;

	// virado para direita
	public boolean right;
	
	/**
	 * Quantidade de tempo para ir de STATE_DYING para STATE_DEAD.
	 */
	protected long TIME_STATE_DYING = 3000;

	public static final int STATE_ALIVE = 0;
	public static final int STATE_DYING = 1;
	public static final int STATE_DEAD  = 2;
	public static final int STATE_GONE  = 3;
	private static final String TAG = "Sprite";
	public int state;
	long stateTime;
	public int[] frameMorrendo;

	/*
	 * Cria um novo objeto Sprite com a animação especificada.
	 */
	public Sprite(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight);
		
		if(image == null) {
			throw new IllegalArgumentException("Sprite imagem is null");
		}
		
		this.image = image;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		defineReferencePixel(width / 2, height / 2);
	}

	/**
	 * Atualiza a Animação da Sprite e sua posição baseada na velocidade.
	 */
	public void update(long elapsedTime) {

		move(elapsedTime);

		if (right) {
			setTransform(javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
		} else {
			setTransform(javax.microedition.lcdui.game.Sprite.TRANS_NONE);
		}

		updateFrame(elapsedTime);
	}

	protected void updateFrame(long elapsedTime) {
		if (state == STATE_DYING) {
			if(frameMorrendo != null){
				setFrame(frameMorrendo);
			}
		}

		moveFrame(elapsedTime);

		// atualiza o estado de "morte"
		stateTime = stateTime + elapsedTime;
		if (state == STATE_DYING && stateTime >= TIME_STATE_DYING) {
			setState(STATE_DEAD);
		}
	}

	/**
	 * Configura a posição x atual da sprite.
	 */
	public void setX(float x) {
		setPosition((int) x, this.y);
	}

	/**
	 * Configura a posição x atual da sprite.
	 */
	public void setY(float y) {
		setPosition(this.x, (int) y);
	}

	/**
	 * Obtém a imagem atual da Sprite.
	 */
	public Bitmap getImage() {
		return image;
	}

	protected void move(long elapsedTime) {
		if (moveX < 0) {
			right = false;
		} else if (moveX > 0) {
			right = true;
		}
	}

	/**
	 * Desenha o sprite, respeitando o offset
	 * 
	 * @param g
	 * @param offsetX
	 * @param offsetY
	 */
	public void paint(Canvas canvas, int offsetX, int offsetY) {
		int x = Math.round(this.x) + offsetX;
		int y = Math.round(this.y) + offsetY;
		int xbck2 = this.x;
		int ybck2 = this.y;
		super.setPosition(x,y);
		super.paint(canvas);
		super.setPosition(xbck2, ybck2);
	}

	/**
	 * Proxima frame da animacao
	 * 
	 * @param elapsedTime
	 */
	protected void moveFrame(long elapsedTime) {
		try {
			totalTime += elapsedTime;
			boolean trocarFrame = false;

			if(state == STATE_DYING) {
				trocarFrame = totalTime > TIME_MOVE_DIE;			
			} else {
				trocarFrame = totalTime > TIME_MOVE;	
			}

			if (trocarFrame) {

				if(frame == null) {
					nextFrame();
				} else {
					idx++;
					if (idx >= frame.length) {
						idx = 0;
					}
					setFrame(frame[idx]);
				}

				// zera
				totalTime = 0;
			}
		} catch (Exception e) {
			Log.e(TAG, "Sprite.moveFrame: " + getClass().getSimpleName() + ": " + e.getMessage(), e);
		}
	}

	protected void setFrameMorrendo(int[] frame) {
		frameMorrendo = frame;
	}
	
	protected void setFrame(int[] frame) {
		try {
			boolean igual = true;
			if(this.frame != null && this.frame.length >= frame.length) {
				for (int i = 0; i < frame.length; i++) {
					if (this.frame[i] != frame[i]) {
						igual = false;
						break;
					}
				}
				if (igual) {
					return;
				}
			}
			idx = 0;
			this.frame = frame;
			setFrame(frame[idx]);
		} catch (Exception e) {
			Log.e(TAG, "Sprite.setFrame: " + getClass().getSimpleName() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * Configura o estado dessa criatura para STATE_NORMAL, STATE_DYING, ou
	 * STATE_DEAD.
	 */
	public void setState(int state) {
		if (this.state != state) {
			this.state = state;
			this.stateTime = 0;
			if (state == STATE_DYING) {
				moveX = 0;
				moveY = 0;
			}
		}
	}
	
	/**
	 * Clona a Sprite. Não clona a posição ou valocidade.
	 */
	public abstract Object clone();

	@Override
	public void setFrameSequence(int[] sequence) {
		super.setFrameSequence(sequence);
	}
	
	/**
	 * Util se tiver que comparar o sprite com um int para ser rapido
	 */
	public int getIcon() {
		return -1;
	}

	/**
	 * Precisa desenhar ?
	 * 
	 * @return
	 */
	public boolean isStateDesenhar() {
		boolean b = state == Personagem.STATE_ALIVE || state == Personagem.STATE_DYING;
		return b;
	}
	
}