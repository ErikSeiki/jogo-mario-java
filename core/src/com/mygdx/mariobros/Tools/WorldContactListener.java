package com.mygdx.mariobros.Tools;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Sprites.Enemy;
import com.mygdx.mariobros.Sprites.Fireball;
import com.mygdx.mariobros.Sprites.Item;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Sprites.ObjetosTilesInterativos;

public class WorldContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

		switch(cDef) {
		case ClasseGame.MARIO_HEAD_BIT |  ClasseGame.BRICK_BIT:
		case ClasseGame.MARIO_HEAD_BIT |  ClasseGame.COIN_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.MARIO_HEAD_BIT){
				((ObjetosTilesInterativos) fixB.getUserData()).onHeadHit((Mario)fixA.getUserData());
			}
			else{
				((ObjetosTilesInterativos) fixA.getUserData()).onHeadHit((Mario)fixB.getUserData());
			}
			break;
		case ClasseGame.ENEMY_HEAD_BIT |  ClasseGame.MARIO_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.ENEMY_HEAD_BIT){
				((Enemy) fixA.getUserData()).hitOnHead((Mario)fixB.getUserData());
			}
			else{
				((Enemy) fixB.getUserData()).hitOnHead((Mario)fixA.getUserData());
			}
			break;
		case ClasseGame.ENEMY_BIT | ClasseGame.OBJECT_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.ENEMY_BIT){
				((Enemy) fixA.getUserData()).reverseVelocity(true,false);
			}
			else{
				((Enemy) fixB.getUserData()).reverseVelocity(true,false);
			}
			break;
		case ClasseGame.MARIO_BIT | ClasseGame.ENEMY_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.MARIO_BIT){
				((Mario)fixA.getUserData()).hit((Enemy)fixB.getUserData());
			}
			else{
				((Mario)fixB.getUserData()).hit((Enemy)fixA.getUserData());
			}
			break;
		case ClasseGame.ENEMY_BIT | ClasseGame.ENEMY_BIT:
			((Enemy) fixA.getUserData()).hitByEnemy((Enemy) fixB.getUserData());
			((Enemy) fixB.getUserData()).hitByEnemy((Enemy) fixA.getUserData());
			break;
		case ClasseGame.ITEM_BIT | ClasseGame.OBJECT_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.ITEM_BIT){
				((Item) fixA.getUserData()).reverseVelocity(true,false);
			}
			else{
				((Item) fixB.getUserData()).reverseVelocity(true,false);
			}
			break;
		case ClasseGame.ITEM_BIT | ClasseGame.MARIO_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.ITEM_BIT){
				((Item) fixA.getUserData()).use((Mario)fixB.getUserData());
			}
			else{
				((Item) fixB.getUserData()).use((Mario)fixA.getUserData());
			}
			break;
		case ClasseGame.FIREBALL_BIT | ClasseGame.OBJECT_BIT:
			if(fixA.getFilterData().categoryBits == ClasseGame.FIREBALL_BIT) {
				((Fireball)fixA.getUserData()).setToDestroy();
			}
			else {
				((Fireball)fixB.getUserData()).setToDestroy();
			}
			break;
		case ClasseGame.ENEMY_HEAD_BIT | ClasseGame.FIREBALL_BIT:
		case ClasseGame.ENEMY_BIT | ClasseGame.FIREBALL_BIT:
			if(fixA.getFilterData().categoryBits != ClasseGame.FIREBALL_BIT) {
				((Enemy)fixA.getUserData()).hitForFireball((Fireball)fixB.getUserData());
			}
			else {
				((Enemy)fixB.getUserData()).hitForFireball((Fireball)fixA.getUserData());
			}
			break;
		}

	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
