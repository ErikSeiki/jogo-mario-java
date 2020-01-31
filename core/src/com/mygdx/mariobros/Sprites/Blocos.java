package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Blocos extends ObjetosTilesInterativos{

	public Blocos(PlayScreen screen, MapObject object) {
		super(screen, object);
		fixture.setUserData(this);
		setCategoryFilter(ClasseGame.BRICK_BIT);
	}

	@Override
	public void onHeadHit(Mario mario) {
		if(mario.isBig()) {
			setCategoryFilter(ClasseGame.DESTROYED_BIT);
			getCell().setTile(null);
			Hud.addScore(200);
			ClasseGame.manager.get("Sons/breakblock.wav", Sound.class).play();
		}
		ClasseGame.manager.get("Sons/bump.wav", Sound.class).play();
	}

}
