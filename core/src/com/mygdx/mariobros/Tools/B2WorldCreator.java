package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Blocos;
import com.mygdx.mariobros.Sprites.Enemy;
import com.mygdx.mariobros.Sprites.Goomba;
import com.mygdx.mariobros.Sprites.Moedas;
import com.mygdx.mariobros.Sprites.Turtle;

public class B2WorldCreator {
	private Array<Goomba> goombas;
	private Array<Turtle> turtle;
	
	public B2WorldCreator(PlayScreen screen ){
		World world = screen.getWorld();
		TiledMap map = screen.getMap();

		//cria variáveis ​de corpo e fixacao
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;

		//Chão
		for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() /2) / ClasseGame.PPM, (rect.getY() + rect.getHeight()/2)/ ClasseGame.PPM );

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth()/2 / ClasseGame.PPM, rect.getHeight()/2 / ClasseGame.PPM);
			fdef.shape = shape;
			body.createFixture(fdef );
		}

		// Tubo
		for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() /2) / ClasseGame.PPM, (rect.getY() + rect.getHeight()/2)/ ClasseGame.PPM );

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth()/2 / ClasseGame.PPM, rect.getHeight()/2 / ClasseGame.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = ClasseGame.OBJECT_BIT;
			body.createFixture(fdef );
		}

		//Moedas
		for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			new Moedas(screen , object);
		}

		//Blocos
		for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			new Blocos(screen, object);
		}

		goombas = new Array<Goomba>();
		for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			goombas.add(new Goomba(screen, rect.getX()/ ClasseGame.PPM, rect.getY()/ClasseGame.PPM));
		}
		
		turtle = new Array<Turtle>();
		for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			turtle.add(new Turtle(screen, rect.getX()/ ClasseGame.PPM, rect.getY()/ClasseGame.PPM));
		}
	}
	public Array<Goomba> getGoombas() {
		return goombas;
	}

	public Array<Enemy> getEnemies() {
		Array<Enemy> enemies = new Array<Enemy>();
		enemies.addAll(goombas);
		enemies.addAll(turtle);
		return enemies;
	}
}
