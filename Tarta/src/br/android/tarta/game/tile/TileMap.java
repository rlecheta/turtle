package br.android.tarta.game.tile;


import java.util.ArrayList;

import javax.microedition.lcdui.game.GameCanvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import br.android.tarta.game.sprites.Comida;
import br.android.tarta.game.sprites.Personagem;
import br.android.tarta.game.sprites.Player;
import br.android.tarta.game.sprites.Sprite;
import br.android.tarta.game.sprites.Tiro;
import br.android.tarta.game.util.Utils;

/**
 * A classe TileMap contém a informação para um pequeno mapa de figuras lado a
 * lado, incluindo Sprites. Cada pedaço é uma referência a uma imagem, sendo
 * essas imagens usadas múltiplas vezes no mesmo mapa.
 * 
 * @author rlecheta
 */
/**
 * A classe TileMapRenderer desenha um TileMap na tela. Ela desenha todos os
 * tiles, sprites, e o a imagem de fundo opcional, centralizados na posição do
 * jogador.
 * 
 * <p>
 * Se a largura da imagem de fundo por menor que a largura do mapa, a imagem de
 * fundo parecerá que esta se movendo devagar, criando o efeito de parallax.
 * 
 * <p>
 * Também, três métodos estáticos são fornecidos para converter pixels em
 * posições dos tiles e vice-versa.
 * 
 * <p>
 * Esse TileMapRender usa tiles com tamanho de 64.
 */
public class TileMap {

	private Bitmap[][] tiles;
	public ArrayList<Sprite> sprites;
	public Player player;
	public Personagem chefao;
	
	// id do cenario deste mapa
	private final int cenario;
	
	private static final int TILE_SIZE = 32;

	// o tamanho em bits do tile
	// Math.pow( 2, TILE_SIZE_BITS ) == TILE_SIZE
	private static final int TILE_SIZE_BITS = 5;
	private static final boolean LOG_ON = false;
	private static final String TAG = "TileMap";

	private Bitmap background;

	/**
	 * Cria um novo TileMap com a largura e altura especificada (em número de
	 * pedaços) do mapa.
	 * @param cenario 
	 */
	public TileMap(int width, int height, int cenario) {
		this.cenario = cenario;
		tiles = new Bitmap[width][height];
		sprites = new ArrayList<Sprite>();
	}

	/**
	 * Obtém a largura do TileMap (número de pedaços).
	 */
	public int getWidth() {
		return tiles.length;
	}

	/**
	 * Obtém a altura do TileMap (número de pedaços).
	 */
	public int getHeight() {
		return tiles[0].length;
	}

	/**
	 * Obtém o pedaço de uma localização espefífica. Retorna null is não haver
	 * nenhum pedaço na localização espeficada ou então se a localizaçãono for
	 * fora dos limites do mapa.
	 */
	public Bitmap getTile(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return null;
		} else {
			return tiles[x][y];
		}
	}

	/**
	 * Configura o pedaço no local especificado.
	 */
	public void setTile(int x, int y, Bitmap tile) {
		tiles[x][y] = tile;
	}
	
	/**
	 * Converte uma posição em pixel para a posição de um tile.
	 */
	public static int pixelsToTiles(float pixels) {
		return pixelsToTiles(Math.round(pixels));
	}

	/**
	 * Converte uma posição em pixel para a posição de um tile.
	 */
	public static int pixelsToTiles(int pixels) {
		// usa deslocamento para obter os valores corretos para pixels negativos
		return pixels >> TILE_SIZE_BITS;

		// ou, se o tamanho dos tiles não forem potência de dois, usa o método
		// floor():
		// return ( int ) Math.floor( ( float ) pixels / TILE_SIZE );
	}

	/**
	 * Converte a posição de um tile para a posição em pixel.
	 */
	public static int tilesToPixels(int numTiles) {
		// sem razão real para usar deslocamento aqui.
		// o seu uso é um pouco mais rápido, mas nos processadores modernos isso
		// quase não faz diferença
		return numTiles << TILE_SIZE_BITS;

		// se o tamanho dos tiles não forem potência de dois,
		// return numTiles * TILE_SIZE;
	}

	/**
	 * Desenha o TileMap especificado.
	 * 
	 * Desenha todo o cenario, background, tiles, o player e inimigos
	 * 
	 */
	public void draw(Canvas canvas,Paint paint) {
		if(canvas == null || paint == null) {
			return;
		}
		
		int screenWidth = GameCanvas.width;
		int screenHeight = GameCanvas.height;

		int mapWidth = tilesToPixels(tiles.length);
		int offsetX = getOffsetX(player.x);
		int offsetY = getOffsetY(player.y);

		// desenha um fundo preto se necessário
		if (background == null || screenHeight > background.getHeight()) {
			paint.setColor(Color.BLACK);
			canvas.drawRect(0, 0, screenWidth, screenHeight,paint);
		}

		// desenha a imagem de fundo usando parallax
		if (background != null) {
			int x = offsetX * (screenWidth - background.getWidth()) / (screenWidth - mapWidth);
			int y = screenHeight - background.getHeight();

//			canvas.drawBitmap(background, x, y, null);
			
			canvas.drawBitmap(background, x, 0, null);
		}

		// desenha os tiles visíveis
		int firstTileX = pixelsToTiles(-offsetX);
		int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = firstTileX; x <= lastTileX; x++) {
				Bitmap image = getTile(x, y);
				if (image != null) {
					canvas.drawBitmap(image, tilesToPixels(x) + offsetX, tilesToPixels(y) + offsetY,null);
				}
			}
		}

		// desenha o jogador
//		if(LOG_ON) {
//			Log.i(TAG, "Tarta.x/y " + offsetX+"/"+offsetY);
//		}
		player.paint(canvas,offsetX,offsetY);

		// Desenha todos os sprites/inimigos
		int size = sprites.size();
		for (int i = 0; i < size; i++) {
			Sprite sprite = sprites.get(i);

			if(sprite.getIcon() == Tiro.ICON) {
				if(LOG_ON) {
					log("Tiro paint " + sprite.state);
				}
			}

			// Verifica se precisa desenhar
			if(sprite.state == Personagem.STATE_ALIVE || sprite.state == Personagem.STATE_DYING) {
				
				boolean acordado = sprite.moveX > 0 || sprite.moveY > 0;

				// X real desconsiderando o offset do scroll da tela
				int x = Math.round(sprite.x) + offsetX;

				boolean visivel = x >= 0 && x < screenWidth;
				
				// TODO esta desenhando todos, somente desenhar quem esta visivel e acordado
				if(sprite instanceof Personagem) {
					Personagem p = ((Personagem) sprite);
					// Acorda a critura quando a mesma estiver na tela

					if(visivel) {
						// wakeup no draw?
						p.wakeUp(player);
						acordado = true;

						sprite.paint(canvas,offsetX,offsetY);
					} else {
						if(sprite.getIcon() == Tiro.ICON) {
							log("remove tiro ");
							removeSprite(sprite);
						}
					}
				}
				else if(sprite instanceof Comida) {
					sprite.paint(canvas,offsetX,offsetY);
				}
			}
		}
	}

	private void log(String string) {
		if(LOG_ON) {
			Log.d(TAG, string);
		}
	}

	public int getOffsetX(int playerX) {
		int screenWidth = GameCanvas.width;

		// Sprite
		int mapWidth = tilesToPixels(tiles.length);

		// obtém a posição de scrolling do mapa, baseado na posição do jogador
		int offsetX = screenWidth / 2 - Math.round(playerX) - TILE_SIZE;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, screenWidth - mapWidth);
		
		return offsetX;
	}
	
	public int getOffsetY(int playerY) {
		int screenHeight = GameCanvas.height;

		// Sprite
		int mapHeight = tilesToPixels(getHeight());

		// obtém o offset de y para desenhar todas as sprites e tiles
		int offsetY = screenHeight - tilesToPixels(getHeight());

		offsetY = screenHeight / 2 - Math.round(playerY) - TILE_SIZE;
		offsetY = Math.min(offsetY, 0);
		offsetY = Math.max(offsetY, screenHeight - mapHeight);
		
		return offsetY;
	}

	/**
	 * Adiciona a Sprite no mapa.
	 */
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

	/**
	 * Remove a Sprite do mapa.
	 */
	public void removeSprite(Sprite sprite) {
		// Nao remove para nao rodar o GC
//		sprites.remove(sprite);
		sprite.state = Sprite.STATE_GONE;
	}

	/**
	 * Retorna o id do cenario deste mapa
	 * 
	 * @return
	 */
	public int getCenario() {
		return cenario;
	}
	
	public Bitmap getBackground() {
		return background;
	}
	
	public void setBackground(Bitmap background) {
		this.background = background;
		if(background != null) {
			int h = background.getHeight();
			int screenHeight = GameCanvas.height;
			if(h < screenHeight) {
				Bitmap bitmap = Utils.resizeFullHeight(background, GameCanvas.height);
				this.background = bitmap;
			}
		}
	}
}