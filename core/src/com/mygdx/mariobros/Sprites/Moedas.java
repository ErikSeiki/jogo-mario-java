package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Scenes.Hud;
import com.mygdx.mariobros.Screens.PlayScreen;


public class Moedas extends ObjetosTilesInterativos{
	private static TiledMapTileSet tileSet;
	private final int BLANK_COIN = 28;
	
	public Moedas(PlayScreen screen, MapObject object) {
		super(screen, object);
		tileSet = map.getTileSets().getTileSet("tileset_gutter");
		fixture.setUserData(this);
		setCategoryFilter(ClasseGame.COIN_BIT );
	}

	@Override
	public void onHeadHit(Mario mario) {
		if(getCell().getTile().getId() == BLANK_COIN) {
			ClasseGame.manager.get("Sons/bump.wav", Sound.class).play();
		}
		else {
			if(object.getProperties().containsKey("mushroom")) {
				screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y+16/ClasseGame.PPM),
						Mushroom.class));
				Hud.addScore(100);
				ClasseGame.manager.get("Sons/powerup_spawn.wav", Sound.class).play();
				
			}
			else {
				ClasseGame.manager.get("Sons/coin.wav", Sound.class).play();
			}
		}
		getCell().setTile(tileSet.getTile(BLANK_COIN));
	}
}
