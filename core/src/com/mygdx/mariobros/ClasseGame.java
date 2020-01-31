package com.mygdx.mariobros;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.mariobros.Screens.PlayScreen;

public class ClasseGame extends Game {
	// Tamanho da tela virtual e Escala Box2D (pixels por metro)
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208; 
	public static final float PPM = 100;

	//Box2D Collision Bits
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;


	public SpriteBatch batch;

	public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("Musica/mario_music.ogg", Music.class);
		manager.load("Sons/breakblock.wav", Sound.class);
		manager.load("Sons/coin.wav", Sound.class);
		manager.load("Sons/bump.wav", Sound.class);
		manager.load("Sons/powerup_spawn.wav", Sound.class);
		manager.load("Sons/powerup.wav", Sound.class);
		manager.load("Sons/powerdown.wav", Sound.class);
		manager.load("Sons/stomp.wav", Sound.class);
		manager.load("Sons/mariodie.wav", Sound.class);
		manager.finishLoading();

		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
		manager.dispose();
		batch.dispose();

	}

}
