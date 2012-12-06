package br.android.tarta.game.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import br.android.tarta.JogoTarta;
import br.android.tarta.R;
import br.android.tarta.TelaListaFase;
import br.android.tarta.game.sprites.Abelha;
import br.android.tarta.game.sprites.Aranha;
import br.android.tarta.game.sprites.Chefao;
import br.android.tarta.game.sprites.Comida;
import br.android.tarta.game.sprites.Minhoca;
import br.android.tarta.game.sprites.Mosquito;
import br.android.tarta.game.sprites.Plataforma;
import br.android.tarta.game.sprites.Player;
import br.android.tarta.game.sprites.Sprite;
import br.android.tarta.game.sprites.Tiro;
import br.android.tarta.game.tile.TileMap;
import br.android.ui.utils.ImageUtils;

/**
 * Gerencia os recursos (imagens, mapas, sons) do jogo
 * 
 * @author ricardo
 *
 */
public class ResourceManager {

	private ArrayList<Bitmap> tiles;

	// sprites hospedeiras usadas na clonagem
	private Player player;
	private Sprite mushroomSprite;
	private Sprite ovoSprite;
	private Sprite poderTiroSprite;
	private Sprite oneUpSprite;
	private Sprite estrelaSprite;
	private Sprite minhocaSprite;
	private Aranha aranhaSprite;
	private Aranha aranha1Sprite;
	private Chefao chefeSprite;
	private Mosquito mosquitoSprite;
	private Sprite abelhaSprite;
	private final Context context;

//	private Fogo espinhoSprite;

	private int cenarioAtual;

	// Imagens para desenhar no fim da fase
	public Bitmap interfaceX;
	public Bitmap interface0;
	public Bitmap interface1;
	public Bitmap interface2;
	public Bitmap interface3;
	public Bitmap interface4;
	public Bitmap interface5;
	public Bitmap interface6;
	public Bitmap interface7;
	public Bitmap interface8;
	public Bitmap interface9;
	public Bitmap interface_fase;
	public Bitmap interface_ovinho;
	public Bitmap interface_vidas;
	public Bitmap interface_tiro;

	public Tiro tiro;

	private Plataforma plataforma;

	/**
	 * Cria um novo ResourceManager com o GraphicsConfiguration especificado e a
	 * letra do último tile.
	 */
	public ResourceManager(Context context) {
		this.context = context;

		if (JogoTarta.LOG_ON) {
			JogoTarta.log(JogoTarta.TAG,"1 - ResourceManager() : " + (Runtime.getRuntime().freeMemory()/1024));
		}
		loadSprites();
		loadImages();
		if (JogoTarta.LOG_ON) {
			JogoTarta.log(JogoTarta.TAG,"2 - ResourceManager() : " + (Runtime.getRuntime().freeMemory()/1024));
		}
	}

	private void loadImages() {
		interfaceX = ImageUtils.getBitmap(context, R.drawable.interface_x);
		interface_fase = ImageUtils.getBitmap(context, R.drawable.interface_star);
		interface_ovinho = ImageUtils.getBitmap(context, R.drawable.interface_coin);
		interface_vidas = ImageUtils.getBitmap(context, R.drawable.tarta_icon_50);
		interface_tiro = ImageUtils.getBitmap(context, R.drawable.tiro);

		interface0 = ImageUtils.getBitmap(context, R.drawable.interface0);
		interface1 = ImageUtils.getBitmap(context, R.drawable.interface1);
		interface2 = ImageUtils.getBitmap(context, R.drawable.interface2);
		interface3 = ImageUtils.getBitmap(context, R.drawable.interface3);
		interface4 = ImageUtils.getBitmap(context, R.drawable.interface4);
		interface5 = ImageUtils.getBitmap(context, R.drawable.interface5);
		interface6 = ImageUtils.getBitmap(context, R.drawable.interface6);
		interface7 = ImageUtils.getBitmap(context, R.drawable.interface7);
		interface8 = ImageUtils.getBitmap(context, R.drawable.interface8);
		interface9 = ImageUtils.getBitmap(context, R.drawable.interface9);			
	}

	/**
	 * Obtem uma imagem do diretório /recursos/imagens/
	 */
	public Bitmap loadImage(int id) {
		Bitmap bitmap = ImageUtils.getBitmap(context, id);
		return bitmap;
	}

	/**
	 * Obtem uma imagem do diretório /recursos/imagens/
	 */
	public Bitmap loadImage(String filename) {
//		String filename = "/imagens/" + name;
		Bitmap bitmap = ImageUtils.createBitmap(filename);
		return bitmap;
	}

	/**
	 * Carrega um mapa do diretório /recursos/mapas/
	 * 
	 * Cada mapa possui um cenario. Carrega o cenario de /recursos/cenarioX
	 * 
	 * @param renderer 
	 */
	public TileMap loadMap(int numero) throws IOException {

		// Adiciona o jogador no mapa
		Player p = (Player) player.clone();
		p.reset();
		
		String name = "map" + numero + ".txt";

		String filename = "/mapas/" + name;

		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		BufferedReader reader = null;
		try {

			InputStream in = null;
			
			if(TelaListaFase.FASE_STR != null) {
				in = new ByteArrayInputStream(TelaListaFase.FASE_STR.getBytes());
			}
			
			if(in == null) {
				in = getClass().getResourceAsStream(filename);
			}
			
			if(in == null) {
				throw new RuntimeException("Mapa ["+name+"] nao foi carregado");
			}
			reader = new BufferedReader(new InputStreamReader(in),8);

			// Carrega o cenario
			cenarioAtual = loadMapCenario(name, reader);
			if(cenarioAtual == -1) {
				throw new RuntimeException("Cenario do Mapa ["+name+"] nao definido.");
			}
			
			while (true) {
				String line = reader.readLine();
				// sem mais linhas para ler
				if (line == null) {
					close(reader);
					close(in);
					break;
				}
				
				// adiciona toda linha menos os comentários
				else if (!line.startsWith("#")) {
					lines.add(line);
					width = Math.max(width, line.length());
				}
			}

			// parseia as linhas para criar uma TileEngine
			height = lines.size();
			TileMap newMap = new TileMap(width, height,cenarioAtual);

			int tilesSize = tiles.size();
			
			for (int y = 0; y < height; y++) {

				String line = (String) lines.get(y);
				for (int x = 0; x < line.length(); x++) {
					char ch = line.charAt(x);

					// Verifica o tile que o caracter atual representa
					int tile = ch - 'A';
					if (tile >= 0 && tile < tilesSize) {
						if (ch == 'P') {
							addSprite(newMap, plataforma, x, y);
						} else {
							newMap.setTile(x, y, (Bitmap) tiles.get(tile));
						}
						
					}

					// Verifica se o caracter representa umaa sprite
					else if (ch == 'o') {
						// Ovinho para comer
						addSprite(newMap, ovoSprite, x, y);
					}else if (ch == 'f') {
						// FireFlower para atirar
						addSprite(newMap, poderTiroSprite, x, y);
					} else if (ch == '*') {
						// Estrela do Fim do Jogo
						addSprite(newMap, estrelaSprite, x, y);
					} else if (ch == '!') {
						addSprite(newMap, mushroomSprite, x, y);
					} else if (ch == 'f') {
						// Fogo!
//						addSprite(newMap, espinhoSprite, x, y);
					} else if (ch == 'u') {
						addSprite(newMap, oneUpSprite, x, y);
					}  else if (ch == '1') {
						addSprite(newMap, minhocaSprite, x, y);
					} else if (ch == '2') {
						addSprite(newMap, aranhaSprite, x, y);
					} else if (ch == '3') {
						addSprite(newMap, mosquitoSprite, x, y);
					} else if (ch == '4') {
						addSprite(newMap, aranha1Sprite, x, y);
					} else if (ch == '5') {
						// vago
					} else if (ch == '6') {
						
					} else if (ch == '7') {
						
					} else if (ch == '8') {
						addSprite(newMap, abelhaSprite, x, y);
					} else if (ch == '9') {
					} else if (ch == '@') {
						addSprite(newMap, chefeSprite, x, y);
					} else if (ch == '$') {
						
					} else if (ch == '%') {
						
					} else if (ch == '#') {
						// Configura a posicao do player
						setSpriteTileXY(p, x, y);
					}
				}
			}

			addInvisibleSprite(newMap, tiro, 0, 0);

			newMap.player = p;
			return newMap;

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void close(Closeable reader) {
		try {
			reader.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private int loadMapCenario(String name, BufferedReader reader) throws IOException {
		String line = reader.readLine();
		int idx = line.indexOf("cenario:");
		int icenario = -1;
		if(idx != -1) {
			try {
				// informação do cenario
				String cenario = line.substring(8).trim();
				
				icenario  = Integer.parseInt(cenario);

				// Carrega os tiles do proximo cenario
				loadTilesCenario(icenario);
			} catch (Exception e) {
				JogoTarta.logError("Erro ao ler cenario: " + e.getMessage(),e);
			}
		}
		return icenario;
	}

	private void addInvisibleSprite(TileMap map, Sprite hostSprite, int tileX, int tileY) {
		addSprite(map, hostSprite, tileX, tileY);
		hostSprite.state = Sprite.STATE_GONE;
	}

	/**
	 * Adiciona uma Sprite em uma mapa.
	 */
	private void addSprite(TileMap map, Sprite hostSprite, int tileX, int tileY) {

		if (hostSprite != null) {
			// clona a sprite usando a hospedeira
			Sprite sprite = (Sprite) hostSprite.clone();

			setSpriteTileXY(sprite,tileX, tileY);

			// adiciona no mapa
			map.addSprite(sprite);
		}
	}

	private void setSpriteTileXY(Sprite sprite,int tileX, int tileY) {
		// centraliza a sprite
		int x = TileMap.tilesToPixels(tileX) + (TileMap.tilesToPixels(1) - sprite.width) / 2;
		sprite.setX(x);

		// alinha a sprite no chão
		int y = TileMap.tilesToPixels(tileY + 1) - sprite.height;
		sprite.setY(y);
	}

	/**
	 * Carrega as imagens dos tiles para compor o cenario
	 * 
	 * @param cenario 
	 */
	public void loadTilesCenario(int cenario) {
		if(cenario == this.cenarioAtual) {
			return;
		}

		this.cenarioAtual = cenario;

		if(tiles != null) {
			// limpa memoria
			tiles.clear();
			tiles = null;
		}

		tiles = new ArrayList<Bitmap>();

		char max = 'Z';

		char ch = 'A';

//		JogoTarta.log(Fases.TAG,"loadTileImages cenario: " + cenario);
		
		while (ch <= max) {

			Bitmap img = null;

			img = loadImage("/cenario" + cenario +  "/tile_" + String.valueOf(ch).toLowerCase() + ".png");

//			switch (ch) {
//				case 'A':
//					img = loadImage(R.drawable.tile_a);
//					break;
//				case 'B':
//					img = loadImage(R.drawable.tile_b);
//					break;
//				case 'C':
//					img = loadImage(R.drawable.tile_c);
//					break;
//				case 'D':
//					img = loadImage(R.drawable.tile_d);
//					break;
//				case 'E':
//					img = loadImage(R.drawable.tile_e);
//					break;
//				case 'F':
//					img = loadImage(R.drawable.tile_f);
//					break;
//				case 'G':
//					img = loadImage(R.drawable.tile_g);
//					break;
//				case 'H':
//					img = loadImage("R.drawable.tile_h");
//					break;
//				case 'I':
//					img = loadImage(R.drawable.tile_i);
//					break;
//				case 'J':
//					img = loadImage(R.drawable.tile_j);
//					break;
//				case 'K':
//					img = loadImage(R.drawable.tile_k);
//					break;
//				case 'L':
//					img = loadImage(R.drawable.tile_l);
//					break;
//				case 'M':
//					img = loadImage(R.drawable.tile_m);
//					break;
//				case 'N':
//					img = loadImage(R.drawable.tile_n);
//					break;
//				case 'O':
//					img = loadImage(R.drawable.tile_o);
//					break;
//				case 'P':
//					img = loadImage(R.drawable.tile_p);
//					break;
//				case 'Q':
//					img = loadImage(R.drawable.tile_q);
//					break;
//				case 'R':
//					img = loadImage(R.drawable.tile_r);
//					break;
//				case 'S':
//					img = loadImage(R.drawable.tile_s);
//					break;
//				case 'T':
//					img = loadImage(R.drawable.tile_t);
//					break;
//				case 'U':
//					img = loadImage(R.drawable.tile_u);
//					break;
//				case 'V':
//					img = loadImage(R.drawable.tile_v);
//					break;
//				case 'W':
//					img = loadImage(R.drawable.tile_w);
//					break;
//				case 'X':
//					img = loadImage(R.drawable.tile_x);
//					break;
//				case 'Y':
//					img = loadImage(R.drawable.tile_y);
//					break;
//				case 'Z':
//					img = loadImage(R.drawable.tile_z);
//					break;
//				default:
//					break;
//			}

			tiles.add(img);
			ch++;
		}
	}

	/**
	 * Carrega os sprites
	 */
	public void loadSprites() {
		player = new Player(ImageUtils.getBitmap(context, R.drawable.tarta), 32, 48);
		
		estrelaSprite = new Comida.Estrela(ImageUtils.getBitmap(context, R.drawable.star1), 50, 50);

		ovoSprite = new Comida.Ovo(ImageUtils.getBitmap(context, R.drawable.ovofull), 25, 32);

		poderTiroSprite = new Comida.FireFlower(ImageUtils.getBitmap(context, R.drawable.fire), 25, 25);

		// cria a sprite do cogumelo
		mushroomSprite = new Comida.Ovao(ImageUtils.getBitmap(context, R.drawable.mushroom), 32, 36);

		// cria a sprite de vida
		oneUpSprite = new Comida.Vida(ImageUtils.getBitmap(context, R.drawable.oneup), 32, 32);

		// Fogo
		//espinhoSprite = new Espinho.Fogo(ImageUtils.getBitmap(context, R.drawable.fire), 32, 32);

		// -- Inimigos --
		minhocaSprite = new Minhoca(ImageUtils.getBitmap(context, R.drawable.minhoca), 32, 30);

		aranhaSprite = new Aranha(ImageUtils.getBitmap(context, R.drawable.aranha), 40, 20,1);
		aranha1Sprite = new Aranha(ImageUtils.getBitmap(context, R.drawable.aranha1), 32, 30,1);
		chefeSprite = new Chefao(ImageUtils.getBitmap(context, R.drawable.aranha_chefe), 80, 40,10);

		mosquitoSprite = new Mosquito(ImageUtils.getBitmap(context, R.drawable.mosquito), 32, 30);

		abelhaSprite = new Abelha(ImageUtils.getBitmap(context, R.drawable.abelha), 32, 32);

		tiro = new Tiro(ImageUtils.getBitmap(context, R.drawable.fire), 25, 25);

		plataforma = new Plataforma(ImageUtils.getBitmap(context, R.drawable.plataforma), 32, 32);
	}
}