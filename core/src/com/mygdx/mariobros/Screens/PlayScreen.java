package com.mygdx.mariobros.Screens;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.Sprites.Enemy;
import com.mygdx.mariobros.Sprites.Goomba;
import com.mygdx.mariobros.Sprites.Item;
import com.mygdx.mariobros.Sprites.ItemDef;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Sprites.Mushroom;
import com.mygdx.mariobros.Tools.B2WorldCreator;
import com.mygdx.mariobros.Tools.WorldContactListener;


public class PlayScreen implements Screen {

	// Referência ao nosso jogo, usada para definir as telas
	private ClasseGame game;
	private TextureAtlas atlas;
	public static boolean alreadyDestroyed = false;

	// Variáveis básicas do playscreen
	private OrthographicCamera gameCam; 
	private Viewport gamePort;
	private Hud hud;

	// Variáveis do mapa Tiled
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	// Variáveis do Box2d
	private World world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;

	// Sprites
	private Mario player;

	private Music music;

	private Array<Item> items;
	private LinkedBlockingQueue<ItemDef> itemsToSpawn;

	public PlayScreen(ClasseGame game) {
		atlas = new TextureAtlas("Mario/Mario_e_Inimigos.pack");

		this.game = game;

		// Cria camera usada para seguir mario pelo mundo da camera
		gameCam = new OrthographicCamera();

		// Cria um FitViewport para manter a proporção virtual apesar do tamanho da tela
		gamePort = new FitViewport(ClasseGame.V_WIDTH / ClasseGame.PPM, ClasseGame.V_HEIGHT / ClasseGame.PPM, gameCam);

		// cria seu jogo HUD para pontuações / timers / level info
		hud = new Hud(game.batch);

		// Carrega nosso mapa e configura nosso renderizador de mapa
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Tiled/Tela.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / ClasseGame.PPM);

		// inicialmente definimos nossa gamcam para ser centralizada corretamente no começo do mapa
		gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

		// cria nosso mundo Box2D, configurando sem gravidade em X, -10 gravidade em Y e permitindo que corpos durmam
		world = new World(new Vector2(0,-10), true);

		// permite linhas de depuração do nosso mundo box2d.
		b2dr = new Box2DDebugRenderer();

		creator = new B2WorldCreator(this);

		// cria mario no nosso mundo de jogo
		player = new Mario(this);

		world.setContactListener(new WorldContactListener());

		music = ClasseGame.manager.get("Musica/mario_music.ogg", Music.class);
		music.setLooping(true);
		music.setVolume(0.3f);
		music.play();

		items = new Array<Item>();
		itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
	}

	public void spawnItem(ItemDef idef) {
		itemsToSpawn.add(idef);
	}

	public void handleSpawningItems(){
		if(!itemsToSpawn.isEmpty()) {
			ItemDef idef = itemsToSpawn.poll();
			if(idef.type == Mushroom.class) {
				items.add(new Mushroom(this, idef.position.x, idef.position.y));
			}
		}
	}

	public TextureAtlas getAtlas(){
		return atlas;
	}

	@Override
	public void show() {

	}

	public void handleInput(float dt) {
		if(player.currentState != Mario.State.DEAD) {
			// controla o nosso jogador usando impulsos imediatos
			if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
				player.jump();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2) {
				player.correr();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2) {
				player.b2body.applyLinearImpulse( new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true );
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				player.fire();
			}
		}
	}


	public void update(float dt){
		// lida com a entrada do usuário primeiro
		handleInput(dt);
		handleSpawningItems();

		// leva 1 passo na simulação física (60 vezes por segundo)
		world.step(1/60f, 6, 2);

		player.update(dt);
		for(Enemy enemy : creator.getEnemies()) {
			enemy.update(dt);
			if(enemy.getX() < player.getX() + 224/ ClasseGame.PPM) {
				enemy.b2body.setActive(true);
			}
		}

		for(Item item : items) {
			item.update(dt);
		}

		hud.update(dt);

		if(player.currentState != Mario.State.DEAD) {
			gameCam.position.x = player.b2body.getPosition().x;
		}

		gameCam.update();

		renderer.setView(gameCam);

	}

	@Override
	public void render(float delta) {
		// separa nossa lógica de atualização do render
		update(delta);

		// Limpar a tela do jogo com preto
		Gdx.gl.glClearColor(0,0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// renderiza nosso mapa do jogo
		renderer.render();

		// renderizador nosso Box2DDebugLines
		b2dr.render(world, gameCam.combined);

		game.batch.setProjectionMatrix(gameCam.combined);
		game.batch.begin();
		player.draw(game.batch);
		for(Enemy enemy : creator.getEnemies()) {
			enemy.draw(game.batch);
		}
		for(Item item:items) {
			item.draw(game.batch);
		}
		game.batch.end();


		// Defina nosso lote para agora desenhar o que a câmera Hud vê.
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();

		if(gameOver()) {
			game.setScreen(new GameOverScreen(game));
			dispose();
		}

	}

	public boolean gameOver() {
		if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
			return true;
		}
		return false;
	}


	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
	}

	public TiledMap getMap() {
		return map;
	}

	public World getWorld() {
		return world;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();

	}

	public Hud getHud(){ 
		return hud;
	}

}
