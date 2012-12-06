package br.android.tarta.game;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.lcdui.game.GameCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import br.android.tarta.JogoTarta;
import br.android.tarta.R;
import br.android.tarta.game.Controle.Seta;
import br.android.tarta.game.sensor.AccelerometerManager;
import br.android.tarta.game.sprites.Comida;
import br.android.tarta.game.sprites.Espinho;
import br.android.tarta.game.sprites.Personagem;
import br.android.tarta.game.sprites.Plataforma;
import br.android.tarta.game.sprites.Player;
import br.android.tarta.game.sprites.Sprite;
import br.android.tarta.game.sprites.Tiro;
import br.android.tarta.game.tile.TileMap;
import br.android.tarta.game.util.Pref;
import br.android.tarta.game.util.ResourceManager;
import br.android.tarta.game.util.SomUtil;
import br.android.tarta.game.util.SoundManager;
import br.android.ui.utils.ImageUtils;

/**
 * Classe que controla o game loop.
 * 
 * Trata o input e desenhos
 * 
 * @author ricardo
 *
 */
public class GameManager extends GameCanvas implements Runnable {
	
	private static final String TAG = "GameManager";
	private Paint paint = new Paint();
	private Paint paintBrancoGrande = new Paint();

	// quantidade máxima de fases (importante para a finalização)
	public static int QUANTIDADE_FASES = 10;

	/**
	 * Therad do GameLoop
	 */
	private boolean isAlive;

	public static final String TAG_FASES = "fases";

	// fase atual
	public int fase;

	// Numero do cenario atual
	private int cenarioAtual;

	// mapa atual
	private int currentMap;

	private JogoTarta jogoTarta;

	public GameManager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GameManager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameManager(Context context) {
		super(context);
	}

	public void setJogoTarta(JogoTarta jogoTarta) {
		this.jogoTarta = jogoTarta;

		GameCanvas.LOG_ON = true;
	}

	public static final float GRAVITY = 0.002f;

	public int state;
	private static final int LOADING 			= 1;
	private static final int RUN 				= 2;
	public static final int PAUSE 				= 3;
	private static final int FIM_FASE 			= 4;
	private static final int CARREGANDO_FASE 	= 5;
	private static final int GAME_OVER			= 6;
	private static final int FIM_JOGO			= 7;
	private static final int FECHAR_TELA		= 8;
	private static final int AGUARDA_FIM_JOGO	= 9;

	private static final boolean DEBUG_MEMORIA = false;

	private int STATE_NEXT;

	private long timeTransicao;

	// usado para colisão de tiles
	private Point pointCache = new Point();
	private TileMap map;
	private ResourceManager resourceManager;

	// contadores da interf
	private int quantidadeVidas;
	private int pontosTotal;
	private int pontosFase;
	private int countOvos;

	private Controle controle;

	private float trackX;
	private float trackY;

	long timeLoading;

	private long timeTransicaoAlvo;

	private SoundManager mSoundManager;

	private char[][] numerosCacheArray;
	
	private Bitmap interface_badguy;

	/**
	 * Inicia os recursos
	 */
	public void init() {
//		if (JogoTarta.LOG_ON) {
			JogoTarta.logFree("0 - GameManager.init() : total/free " + (Runtime.getRuntime().totalMemory()/1024) + "/" + (Runtime.getRuntime().freeMemory()/1024));
//		}
			
		LOG_ON = true;

		state = LOADING;

		// inicia o resource manager
		resourceManager = new ResourceManager(getContext());

//		if (JogoTarta.LOG_ON) {
			JogoTarta.logFree("1 - GameManager.init() : " + (Runtime.getRuntime().freeMemory()/1024));
//		}

		fase = Pref.getFase(context);
		if (JogoTarta.LOG_ON) {
			JogoTarta.log("GameManager.init() fase: " + fase);
		}

		// inicia os contadores do jogo
		quantidadeVidas = 2;
		countOvos 		= 0;
		pontosTotal 	= 0;
		pontosFase 		= 0;

		// Garante que o controle está inicializado
		controle = new Controle();
		controle.init(context);
		controle.registerListener();
		AccelerometerManager.DEBUG_ON = false;

		if (JogoTarta.LOG_ON) {
			JogoTarta.logFree("2 - GameManager.init(), antes loadNextFase: " + (Runtime.getRuntime().freeMemory()/1024));
		}

		// carrega o primeiro mapa
		map = loadNextFase();

		if (JogoTarta.LOG_ON) {
			JogoTarta.logFree("3 - GameManager.init() : " + (Runtime.getRuntime().freeMemory()/1024));
		}

		numerosCacheArray = new char[10000][];

		mSoundManager = new SoundManager();
        mSoundManager.initSounds(context);
        mSoundManager.addSound(1, R.raw.sound);

		// inicia a música
		playMusica();

		// Move para o estado RUN depois de 1 segundo
		setNextState(RUN, 1000);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		trackX += event.getX();
		trackY += event.getY();
		return true;//super.onTrackballEvent(event);
	}

	private void checkInput() {

		boolean exit = false;// getKeyStates();
		if (exit) {
			stopThread();
		}

		int keyStates = getKeyStates();

		// Checa as teclas
		boolean keyLeft 	= isLeft(keyStates) || (controle != null && controle.isLeft());
		boolean keyRight 	= isRight(keyStates) || (controle != null && controle.isRight());
		boolean keyDown 	= isDown(keyStates) || (controle != null && controle.isDown());
		boolean keyUp 		= isUp(keyStates) || (controle != null && controle.isUp());
		boolean keyFire 	= isFire(keyStates) || controle != null && controle.isFire();
//		boolean space = isSpace(keyStates);

		Player player = map.player;

		if (player.state == Personagem.STATE_ALIVE) {

			float moveX = 0;

			if(trackX != 0) {
				moveX += Math.min(trackX, player.getMaxSpeed());
				
				trackX = 0;
			}

			if (!player.down) {
				if (keyLeft) {
					moveX -= player.getMaxSpeed();
				}
				if (keyRight) {
					moveX += player.getMaxSpeed();
				}
			}

			if (keyDown) {
				player.down = true;
			} else {
				player.down = false;
			}

			if(keyFire) {
				atirar(player);
			}
			else if (keyUp) {

				// toca apenas se o jogador não estiver pulando
				if (!player.pulando) {
					// soundManager.play(jumpSound);
					 SomUtil.play(context, R.raw.jump);
				}

				player.jump(false);
			}

			// verifica se é para correr
//			if (keyFire) {
//				player.setMaxSpeed(0.3f);
//			} else {
//				player.setMaxSpeed(0.2f);
//			}

			// velocidade para mover para o lado x
			player.moveX = moveX;
		}
	}

	private void atirar(Player player) {
		if(player.qtdeTiros > 0) {
			
			Tiro tiro = resourceManager.tiro;

			if(!tiro.isStateDesenhar()) {
				tiro.moveX = player.right ? 0.25f : -0.25f;
				tiro.setState(Sprite.STATE_ALIVE);

				player.qtdeTiros --;

				//if(!tiro.visible) {
//					Log.i(TAG,"fire!");
					tiro.visible = true;
					tiro.x = player.x + 10;
					tiro.y = player.y;
				//}
			}
		}
	}

	/**
	 * Toca a musica da fase
	 */
	private void playMusica() {
		int musica = R.raw.music0;
		switch (fase) {
			case 0:
				musica = R.raw.music0;
				break;
			case 1:
				musica = R.raw.music1;
				break;
			case 2:
				musica = R.raw.music2;
				break;
			case 3:
				musica = R.raw.music3;
				break;
		}
		SomUtil.play(context, musica, true);		
	}

	public void draw(Canvas canvas, long elapsedTime) {

		// Loading
		if (map == null || state == CARREGANDO_FASE || state == LOADING /*&& (state != RUN)*/ ){
			drawText(canvas,R.string.loading,true,false);
			return;
		}

		// Desenha o mapa e o background
		map.draw(canvas,paint);

		if(AccelerometerManager.debugString != null) {
			paint.setColor(Color.WHITE);
			canvas.drawText(AccelerometerManager.debugString, 20, GameCanvas.height/2, paint);
		}

		if(state == RUN) {
			drawDebugMemoria(canvas);
			if(isControleOn()){
				controle.draw(canvas);
			}
		}
		
		drawHeaderPontos(canvas);
		
		drawFire(canvas);

		// desenha tela de fim de jogo
		if (state == GAME_OVER) {
			// para a música
			SomUtil.stop();

			SomUtil.play(context, R.raw.game_over);

			drawText(canvas,R.string.game_over,true,false);

//			stopThread();
		} else if (state == FIM_JOGO) {
			drawText(canvas,R.string.game_you_win,false,true);
		} else if (state == FIM_FASE) {
			drawText(canvas,R.string.level_completed,false,true);
		} 

		if(state == PAUSE) {
			drawText(canvas, R.string.menu_pause,false,false);
		}
	}

	private boolean isControleOn() {
		return Controle.ON && controle != null;
	}

	private void drawFire(Canvas canvas) {
		/*if(tiro != null && tiro.visible) {
			int dx = map.getOffsetX(tiro.x);
			int dy = map.getOffsetY(tiro.y);

			Log.i(TAG,"tiro " + tiro.x + "/" +tiro.y + " > " + dx + "/" +dy);
			tiro.paint(canvas,dx,dy);
		}*/
	}
	
	private void drawText(Canvas canvas,int resId, boolean background, boolean pontuacao) {
		String s = context.getString(resId);
		drawText(canvas, s, background, pontuacao);
	}

	private void drawText(Canvas canvas,String s, boolean background, boolean pontuacao) {
		if(canvas == null || paint == null) {
			return;
		}
		if (background) {
			Bitmap bitmap = ImageUtils.getBitmap(context, R.drawable.background_h);
			Rect r1 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Rect r2 = new Rect(0, 0, GameCanvas.width, GameCanvas.height);
			canvas.drawBitmap(bitmap, r1, r2, paint);
		}
		
		// Paint Loading 
        paintBrancoGrande.setTextSize(34);
//        paintBrancoGrande.setStyle(Style.FILL_AND_STROKE);
        paintBrancoGrande.setTypeface(Typeface.DEFAULT_BOLD); 
		paintBrancoGrande.setColor(Color.WHITE);

		int altura = GameManager.height / 2;
		if(pontuacao) {
			altura -= 50;
		}

		
		int metadeTela = GameManager.width / 2;
		float metadeTexto = paintBrancoGrande.measureText(s) / 2;
		canvas.drawText(s, metadeTela - metadeTexto, altura, paintBrancoGrande);

		if(pontuacao) {
			paintBrancoGrande.setTypeface(Typeface.DEFAULT);
			paintBrancoGrande.setTextSize(22);

			s = context.getString(R.string.points);
			canvas.drawText(s + " : " + pontosFase, metadeTela - metadeTexto, altura + 80, paintBrancoGrande);

//			s = context.getString(R.string.points_total);
//			canvas.drawText(s + " : " + pontosTotal, metadeTela - metadeTexto, altura + 80, paintBrancoGrande);
		}
	}

	/**
	 * Obtém o tile que a Sprite colide. Somente X ou Y da Sprite deve ser
	 * mudado não ambos. Retorna null se nenhuma colisão for detectada.
	 */
	public Point getTileCollision(Sprite sprite, float newX, float newY) {

		float fromX = Math.min(sprite.x, newX);
		float fromY = Math.min(sprite.y, newY);
		float toX = Math.max(sprite.x, newX);
		float toY = Math.max(sprite.y, newY);

		// obtem a localização do tile
		int fromTileX = TileMap.pixelsToTiles(fromX);
		int fromTileY = TileMap.pixelsToTiles(fromY);
		int toTileX = TileMap.pixelsToTiles(toX + sprite.width - 1);
		int toTileY = TileMap.pixelsToTiles(toY + sprite.height - 1);

		// checa cada tile para verificar a colisão
		for (int x = fromTileX; x <= toTileX; x++) {

			for (int y = fromTileY; y <= toTileY; y++) {

				if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
					// colisão achada, retorna o tile
					// pointCache.setLocation( x, y );
					pointCache.set(x, y);
					// if (true) {
					// throw new RuntimeException("Verificar colisao aqui");
					// }
					return pointCache;
				}
			}
		}

		// nenhuma colisão achada
		return null;

	}

	/**
	 * Obtém a Sprite que colide com uma Sprite específica, ou null se nenhum
	 * Sprite colide com a Sprite especificada.
	 * @param canKill 
	 */
	public Sprite getSpriteCollision(Sprite player, boolean canKill) {
		ArrayList<Sprite> sprites = map.sprites;

		int size = sprites.size();
		for (int i = 0; i < size; i++) {
			Sprite otherSprite = sprites.get(i);
			
			if(Tiro.ICON == otherSprite.getIcon()) {
				// Tiro não colide com jogador
				continue;
			}
			
			// Precisa estar vivo para colidir
			if(otherSprite.state == Sprite.STATE_ALIVE) {

				// verifica se as bordas das sprites se interceptam
				boolean bateuY = player.y < otherSprite.y + otherSprite.height && otherSprite.y < player.y + player.height;
				boolean bateuX = player.x < otherSprite.x + otherSprite.width && otherSprite.x < player.x + player.width;
				boolean bateu =  bateuY && bateuX;
				if(bateu && canKill) {
					return otherSprite;
				}else if(bateu) {
				}
				
				// Bate no Y com toda a borda. Mas bate com pixel level no eixo X
				bateu = player.collidesWith(otherSprite, true);
				if (bateu) {
					// colisão encontrada, retorna a sprite
					return otherSprite;
				}
			}
		}
		// sem colisão
		return null;
	}

	/**
	 * Atualiza a animação, posição e velocidade de todas as sprites do mapa
	 * atual.
	 */
	public void update(long elapsedTime) {
		// Estado do jogo
		boolean b = state == RUN;
		if(!b) {
			return;
		}

		Player player = map.player;

		// jogador está morto, reinicia o mapa
		if (player.state == Personagem.STATE_DEAD) {

			// se não há mais vidas, mostra a tela de fim de jogo e termina
			if (quantidadeVidas == 0) {
				this.state = GAME_OVER;
				return;
			} else {

				// recarrega o mapa atual
				map = reloadMap();

				// reinicia o som
				playMusica();

				// reseta os contadores de pontuação
				countOvos = 0;
				pontosFase = 0;

				return;
			}
		}

		// verifica a entrada do teclado/mouse
		checkInput();

		// verifica se está pausado
		if (!(state==PAUSE) && !(state == FIM_FASE) && STATE_NEXT == 0) {

//			if(tiro != null){
//				//tiro.moveX = 1;
//			}
			
			// atualiza o jogador
			move(player, elapsedTime);
			player.update(elapsedTime);
			
			if(player.state == Personagem.STATE_DEAD) {
				// O player morreu, para tudo
				return;
			}

			ArrayList<Sprite> sprites = map.sprites;
			int size = sprites.size();
			for (int i = 0; i < size; i++) {
				Sprite sprite = sprites.get(i);
				
				// Se esta vivo na tela...
				if(sprite.isStateDesenhar()) {

					if (sprite instanceof Personagem) {

						Personagem creature = (Personagem) sprite;

						// Move verificando colisoes
						move(creature, elapsedTime);
					}
					
					// Atualiza e move
					sprite.update(elapsedTime);
				}
			}
		}
	}

	/**
	 * Atualiza as criaturas, usando gravidade para as criaturas que não estão
	 * voando e verifica colisão.
	 */
	private void move(Personagem p, long elapsedTime) {
		// usa gravidade
		if (!p.isFlying()) {
			p.moveY = p.moveY + GRAVITY * elapsedTime;
		}

		// Verifica se vai bater no X
		float dx = p.moveX;
		float oldX = p.x;
		float newX = oldX + dx * elapsedTime;
		Point tile = getTileCollision(p, newX, p.y);

		if (tile == null) { // Nao bateu seta o x
			p.setX(newX);
		} else {

			// alinha com a borda do tile
			if (dx > 0) {
				p.setX(TileMap.tilesToPixels(tile.x) - p.width);
			} else if (dx < 0) {
				p.setX(TileMap.tilesToPixels(tile.x + 1));
			}
			boolean continuar = p.collideHorizontal();
			if(!continuar) {
				if (Tiro.ICON == p.getIcon()) {
					Tiro tiro = (Tiro) p;
					tiro.setState(Tiro.STATE_DYING);
				}
				// O tiro ao bater ta fora nao precisa continuar
				return;
			}
		}

		if (p instanceof Player) {
			checkPlayerCollision((Player) p, false);
		} else if (Tiro.ICON == p.getIcon()) {
			checkTiroCollision((Tiro) p);
		}

		// Verifica se o jogo está rodando
		// Pode ser que o Player venceu ou morreu, evita processamento
		if(state == RUN) {
			// Verifica se vai bater no Y
			float dy = p.moveY;
			float oldY = p.y;
			float newY = oldY + dy * elapsedTime;
			tile = getTileCollision(p, p.x, newY);

			if (tile == null) {
				p.setY(newY);
			} else {
				// alinha com a borda do tile
				if (dy > 0) {
					p.setY(TileMap.tilesToPixels(tile.y) - p.height);
				} else if (dy < 0) {
					p.setY(TileMap.tilesToPixels(tile.y + 1));
				}
				p.collideVertical();
			}

			if (p instanceof Player) {

				Player player = (Player) p;

				// Pode matar se estiver acima do outro sprite
				boolean canKill = (oldY < p.y);
				checkPlayerCollision(player, canKill);

				boolean vivo = p.state == Personagem.STATE_ALIVE;
				if(vivo) {
					// se o jogador cai (y muito alto), tira vida e reinicia
					// se o jogador está além do pixel 2000 de altura, morre
					if (p.y > 1000) {
						morreu(player);
					}
				}
			}
		}
	}

	/**
	 * Verifica colisão entre o jogador e outras sprites. If canKill é true, a
	 * colisão com as criaturas irá matá-las.
	 */
	public void checkPlayerCollision(Player player, boolean canKill) {

		if (!(player.state == Personagem.STATE_ALIVE)) {
			return;
		}

		// verifica a colisão do jogador com outras Sprites
		Sprite collisionSprite = getSpriteCollision(player, canKill);

		if (collisionSprite instanceof Comida) {

			comeComida(player, (Comida) collisionSprite);

		} else if (collisionSprite instanceof Espinho) {
			morreu(player);

		} else if (collisionSprite instanceof Plataforma) {
			if(canKill) {
				player.setY(collisionSprite.y-player.height);
				if(player.moveX == 0) {
					player.setX(collisionSprite.x + (collisionSprite.width/2));
				}	
			} else {
				if(collisionSprite.right && collisionSprite.x < player.x) {
					player.setX(collisionSprite.x+player.width);
					player.moveX=0.1f;
				} else if(!collisionSprite.right && collisionSprite.x > player.x) {
					player.setX(collisionSprite.x-player.width);
					player.moveX=-0.1f;
				}
			}
			
		} else if (collisionSprite instanceof Personagem) {

			Personagem badguy = (Personagem) collisionSprite;

			if(badguy.state == Personagem.STATE_ALIVE) {
				
				// Pode matar se está acima do inimigo
				if (canKill) {

					if(badguy.state == Personagem.STATE_ALIVE) {
						mata(player, badguy);
					}
				} else {
					morreu(player);
				}
			}
		}
	}
	
	/**
	 * Verifica colisão entre o Tiro e outras sprites.
	 */
	public void checkTiroCollision(Tiro tiro) {

		if (!(tiro.state == Personagem.STATE_ALIVE)) {
			return;
		}

		// verifica a colisão do jogador com outras Sprites
		Sprite collisionSprite = getSpriteCollision(tiro, true);

		if (collisionSprite instanceof Personagem) {

			Personagem badguy = (Personagem) collisionSprite;

			if(badguy.state == Personagem.STATE_ALIVE) {
				
				if(badguy.state == Personagem.STATE_ALIVE) {
					mata(tiro, badguy);
				}
			}
		}
	}
	
	/**
	 * Mata o inimigo com o tiro
	 * 
	 * @param player
	 * @param badguy
	 */
	private void mata(Tiro tiro, Personagem badguy) {
		if(badguy.TIRO_CAN_KILL) {
			SomUtil.play(context, R.raw.matou);
			tiro.setState(Tiro.STATE_DYING);
//			map.removeSprite(tiro);
			mataInimigo(badguy);
		}
	}

	/**
	 * Mata o inimigo
	 * 
	 * @param player
	 * @param badguy
	 */
	private void mata(Player player, Personagem badguy) {
		SomUtil.play(context, R.raw.matou);

		// Faz o jogador pular
		player.setY(badguy.y - player.height);
		player.jump(true);

		mataInimigo(badguy);
	}

	private void mataInimigo(Personagem badguy) {
		// Tira a vida do inimigo e verifica se morreu
		badguy.vidas--;
		if(badguy.vidas == 0) {
			badguy.setState(Personagem.STATE_DYING);
			pontosFase += 100;
			interface_badguy = ImageUtils.getBitmap(context, badguy.getIcon());

//			if(badguy.chefao) {
//				// FIXME tarta matar bad guy
//				Log.i(JogoTarta.TAG, "bad guy fim");
//				setNextState(AGUARDA_FIM_JOGO, 3000);
//			}
		}
	}

	/**
	 * Jogador foi morto por um bandido ou bateu em um espinho/fogo
	 * 
	 * @param player
	 */
	private void morreu(Player player) {
		if (quantidadeVidas == 0) {
			return;
		}
		
		// jogador morre
		player.setState(Personagem.STATE_DYING);

		// perdeu uma vida
		quantidadeVidas--;

		// pára a música
		SomUtil.stop();
		
		SomUtil.play(context, R.raw.musica_morreu);

		trackX = 0;

		JogoTarta.gc();
	}

	/**
	 * Come o ovinho, ou as vidas
	 * @param player 
	 */
	public void comeComida(Player player, Comida c) {

		// remove do mapa
		map.removeSprite(c);

		if (c instanceof Comida.Ovo) {

			// soma 10 pontos
//			pontosFase += 1;

			// soma um coin
			countOvos += 1;

			// reproduz o som
			//SomUtil.play(context, R.raw.smw_coin);

			// se tiver uma quantidade múltipla de 100
			if (countOvos % 100 == 0) {

				// ganha uma vida
				quantidadeVidas++;

				SomUtil.play(context, R.raw.one_up);
			}

		} else if (c instanceof Comida.Ovao) {

			// soma 1000 pontos
			countOvos += 10;

			SomUtil.play(context, R.raw.powerup);

		} else if (c instanceof Comida.FireFlower) {

			// soma 1000 pontos
			countOvos += 10;

			player.qtdeTiros ++;

		} else if (c instanceof Comida.Vida) {

			// soma 1 vida
			quantidadeVidas++;

			// reproduz o som
			SomUtil.play(context, R.raw.one_up);

		} else if (c instanceof Comida.Estrela) {

			if(isFimJogo()) {
				fimJogo();
			} else {
				fimFase();
			}
		}
	}

	private void fimJogo() {
		fase++;
		SomUtil.stop();

		this.state = FIM_JOGO;

		pontosTotal += pontosFase;
		pontosTotal += countOvos * 10;
		
		SomUtil.play(context, R.raw.game_over);

		setNextState(FECHAR_TELA,4000);
	}

	private void fimFase() {
		fase++;
		SomUtil.stop();
		this.state = FIM_FASE;

		pontosFase += countOvos * 10;
		pontosTotal += pontosFase;

		int numero = fase + 1;
		Pref.setFase(context, numero);

		SomUtil.play(context, R.raw.sound);

		setNextState(FECHAR_TELA,3000);
	}

	/**
	 * Seta o proximo estado, e o tempo para transiçao
	 * 
	 * @param nextState
	 * @param timeTransicao
	 */
	private void setNextState(int nextState, long timeTransicao) {
		this.STATE_NEXT = nextState;
		this.timeTransicao=0;
		this.timeTransicaoAlvo=timeTransicao;
	}

	/**
	 * Método para desenhar os pontos e vidas.
	 */
	private void drawHeaderPontos(Canvas canvas) {
		if(resourceManager == null){
			return;
		}

		int y = 10;

		canvas.drawBitmap(resourceManager.interface_vidas, 7, 5, null);
		canvas.drawBitmap(resourceManager.interfaceX, 30, y, null);
		drawNumero(canvas, quantidadeVidas, 50, y);

		canvas.drawBitmap(resourceManager.interface_fase, 150, y, null);
		canvas.drawBitmap(resourceManager.interfaceX, 170, y, null);
		drawNumero(canvas, fase, 190, y);

		canvas.drawBitmap(resourceManager.interface_ovinho, 250, y, null);
		canvas.drawBitmap(resourceManager.interfaceX, 270, y, null);
		drawNumero(canvas, countOvos, 290, y);

		//canvas.drawBitmap(resourceManager.pontuacao, 410, 22, null);
		canvas.drawBitmap(interface_badguy != null ? interface_badguy : resourceManager.interface_ovinho, 350, y, null);
		canvas.drawBitmap(resourceManager.interfaceX, 370, y, null);
		drawNumero(canvas, pontosFase, 390, y);

		Player player = map.player;
		if(player.qtdeTiros > 0) {
			if (controle != null) {
				Seta setaFire = controle.getImgSetaFire();
				int x = setaFire.x + (setaFire.width / 2) - resourceManager.interface1.getWidth()/2;
				y = setaFire.y + (setaFire.height / 2) - resourceManager.interface1.getHeight()/2;
				drawNumero(canvas, player.qtdeTiros, x, y);
			}
		}
	}

	public void drawDebugMemoria(Canvas canvas) {
		if (DEBUG_MEMORIA) {
			long free = Runtime.getRuntime().freeMemory() / 1024;

			//if(free < 800) {
				paint.setColor(Color.WHITE);
				
				// Memoria Total
//				long total = Runtime.getRuntime().totalMemory() / 1024;

				//canvas.drawText(total + "/" + free, 100, GameManager.height -5, paint);
				drawNumero(canvas, (int) free, 100, GameManager.height - 18);
			//}
		}
	}
	
	public void drawAnyTexct(Canvas canvas, String s) {
		if (DEBUG_MEMORIA) {
				paint.setColor(Color.WHITE);

				//canvas.drawText(total + "/" + free, 100, GameManager.height -5, paint);
				//drawText(canvas, s, 100, true, false);
			//}
		}
	}

	/**
	 * Prepara o jogo para o próximo nível 
	 */
	private void loadNextLevel() {
		timeLoading = 0;
		if(JogoTarta.LOG_ON) {
			JogoTarta.log("loadNextLevel > " + fase);
		}

		timeLoading = 0;

		Thread t = new Thread(){
			@Override
			public void run() {
				if(JogoTarta.LOG_ON) {
					JogoTarta.log("loadNextLevel > " + fase + " GO!");
				}

				// zera os pontos
//				pontosFase = 0;
				countOvos = 0;

				// carrega o próximo mapa
				map = loadNextFase();

				// coloca a música para rodar de novo
				playMusica();

				// sinaliza para ir para a próxima fase
				setNextState(RUN, 1000);
			}
		};
		t.start();
	}

	/**
	 * Unregister the accelerometer sensor otherwise it will continue to operate
	 * and report values.
	 */
	public void unregisterListener() {
		if(controle != null){
			controle.unregisterListener();
		}
	}

	/**
	 * Executa o game loop até que stop() seja chamado.
	 */
	public void run() {
		gameLoop();
	}

	/**
	 * GameLoop do jogo
	 */
	private void gameLoop() {
		long startTime = System.currentTimeMillis();
		long currTime = startTime;

		long sleep = 0;
		
//		long timeMonitorFrameRate = 0;
//		int count=0;

		while (isAlive) {
			long elapsedTime = System.currentTimeMillis() - currTime;
			currTime += elapsedTime;

			// Existe uma transição de estados
			updateState(elapsedTime);

			// atualiza
			if (isAlive && state == RUN) {
				update(elapsedTime);
			}

			// desenha
			if (isAlive) {
				Canvas canvas = getLockedCanvas();

				switch (state) {
					case LOADING:
					case CARREGANDO_FASE:
						sleep = 1000;
						break;
					case PAUSE:
						sleep = 2000;
						break;
				}

				draw(canvas,elapsedTime);
			}

			try {
				// Garante que fez flush
				flushGraphics();
			} catch (Exception e) {
				Log.e("GameAPI", e.getMessage(),e);
			}

			// Dorme um pouco se precisar
			if(isAlive && sleep != 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage(), e);
				} finally {
					sleep = 0;
				}
			}
			
			/*timeMonitorFrameRate += elapsedTime;
			if(timeMonitorFrameRate >= 1000 ) {
				Log.i(TAG, "Frame/seg: " + count);
				timeMonitorFrameRate = 0;
				count = 0;
			} else {
				count++;
			}*/
		}
	}

	/**
	 * Faz a transição do estado atual para o próximo no tempo especificado
	 * 
	 * @param elapsedTime
	 */
	private void updateState(long elapsedTime) {
		if(STATE_NEXT != 0 && timeTransicaoAlvo != 0) {

			timeTransicao += elapsedTime;

			if(timeTransicao >= timeTransicaoAlvo ) {

				// Para: Carregando Fase
				switch (state) {
					case LOADING:
						state = STATE_NEXT;
						break;
					case CARREGANDO_FASE:
						loadNextLevel();
						break;
					case FIM_FASE:
						stopThread();
						fechaTela();
						break;
					case FIM_JOGO:
						stopThread();
						fechaTela();
						break;
					case PAUSE:
						playMusica();
						state = RUN;
						elapsedTime = 0;
						break;
					case AGUARDA_FIM_JOGO:
						// FIXME tarta
//						Log.i(JogoTarta.TAG, "AGUARDA_FIM_JOGO; fimjogo");
						fimJogo();
						break;
					default:
						break;
				}

				timeTransicao 		= 0;
				timeTransicaoAlvo 	= 0;
				STATE_NEXT 			= 0;
			}
		}
	}

	/**
	 * Carrega o mapa da proxima fase.
	 * 
	 * Verifica qual cenario o mapa precisa utilizar e carrega
	 * 
	 * @param renderer
	 * @return
	 */
	public TileMap loadNextFase() {

		TileMap map = null;
		while (map == null) {
			try {
				// Carrega o mapa (faz parser do arquivo txt)
				// Os sprites e imagens já estão em memoria
				map = resourceManager.loadMap(fase);
				
				// O mapa possui um cenario
				// Carrega a imagem de fundo
				int cenario = map.getCenario();
				loadCenarioBackground(map, cenario);

				this.currentMap = fase;
			} catch (IOException ex) {
				throw new RuntimeException("Erro ao ler o mapa: " + fase);
			}
		}

		return map;	
	}

	/**
	 * Recarrega o mapa atual.
	 */
	public TileMap reloadMap() {
		try {
			Bitmap background = map != null ? map.getBackground() : null;
			TileMap map = resourceManager.loadMap(currentMap);
			if(map.getBackground() == null && background != null) {
				map.setBackground(background);
			}
			return map;
		} catch (IOException ex) {
			JogoTarta.logError("reloadMap",ex);
			return null;
		}
	}

	public boolean isFimJogo() {
		return fase == QUANTIDADE_FASES;
	}


	/**
	 * Carrega o cenario atual.
	 * 
	 * Cada cenario tem um fundo especifico e as imagens dos tiles.
	 * 
	 * Um cenario pode ser reaproveitado entre fases
	 * 
	 * @param renderer
	 */
	public void loadCenarioBackground(TileMap map,int cenario) {
		if(cenario == this.cenarioAtual) {
			return;
		}

		this.cenarioAtual = cenario;

		// Fundo
		String filename = "/cenario" + cenario + "/background.png";
		Bitmap background = resourceManager.loadImage(filename);

		if(background != null) {
			map.setBackground(background);
		}
	}

	@Override
	public void startThread(int largura, int altura) {
		isAlive = true;

		Thread t = new Thread(this,"TartaThread");
		t.start();

		new Thread(){
			public void run() {
				init();
			};
		}.start();
	}

	@Override
	public void stopThread() {
		if (isAlive) {
			isAlive = false;
		}
		SomUtil.stop();
	}

	public void fechaTela() {
		if(jogoTarta != null) {
			jogoTarta.finish();
		}
		JogoTarta.gc();
	}

	private void drawNumero(Canvas canvas, int numero, int x, int y) {
		char[] c = numerosCacheArray[numero];
		if(c == null) {
			c = String.valueOf(numero).toCharArray();
			numerosCacheArray[numero] = c;
		}

		for (int i = 0; i < c.length; i++) {

			switch (c[i]) {
			case '0':
				canvas.drawBitmap(resourceManager.interface0, x, y, null);
				x += resourceManager.interface0.getWidth();
				break;
			case '1':
				canvas.drawBitmap(resourceManager.interface1, x, y, null);
				x += resourceManager.interface1.getWidth();
				break;
			case '2':
				canvas.drawBitmap(resourceManager.interface2, x, y, null);
				x += resourceManager.interface2.getWidth();
				break;
			case '3':
				canvas.drawBitmap(resourceManager.interface3, x, y, null);
				x += resourceManager.interface3.getWidth();
				break;
			case '4':
				canvas.drawBitmap(resourceManager.interface4, x, y, null);
				x += resourceManager.interface4.getWidth();
				break;
			case '5':
				canvas.drawBitmap(resourceManager.interface5, x, y, null);
				x += resourceManager.interface5.getWidth();
				break;
			case '6':
				if(resourceManager == null) {
					JogoTarta.logError("resourceManager null");
				}
				else if(resourceManager.interface6 == null) {
					JogoTarta.logError("resourceManager.interface6 null");
				}
				canvas.drawBitmap(resourceManager.interface6, x, y, null);
				x += resourceManager.interface6.getWidth();
				break;
			case '7':
				canvas.drawBitmap(resourceManager.interface7, x, y, null);
				x += resourceManager.interface7.getWidth();
				break;
			case '8':
				canvas.drawBitmap(resourceManager.interface8, x, y, null);
				x += resourceManager.interface8.getWidth();
				break;
			case '9':
				canvas.drawBitmap(resourceManager.interface9, x, y, null);
				x += resourceManager.interface9.getWidth();
				break;
			}
		}
	}

	public void restartLevel() {
		SomUtil.stop();

		map = reloadMap();
		// reinicia o som
		playMusica();

		// reseta os contadores de pontuação
		countOvos = 0;
		pontosFase = 0;
	}

	public void pause() {
		state = PAUSE;
		SomUtil.stop();
	}

	public void resume() {
		setNextState(RUN, 500);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Controle.ON = true;
		if (controle != null) {
			controle.onTouchEvent(event);
			return true;
		}
		if(AccelerometerManager.ON) {
			// Tocou na tela
		}
		return super.onTouchEvent(event);
	
		/*dumpEvent(event);
		if(multiTouchController != null) {
			// Pass the event on to the controller
			return multiTouchController.onTouchEvent(event);			
		}
		return super.onTouchEvent(event);*/
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = super.onKeyUp(keyCode, event);
		if(b) {
			// tem teclado desliga controle
			Controle.ON = false;
		}
		return b;
	}
}