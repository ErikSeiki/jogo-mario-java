package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Mario;

public class Mushroom extends Item{
	public Mushroom(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		setRegion(screen.getAtlas().findRegion("Cougulemo"),0,0,16,16);
		velocity = new Vector2(0.7f,0);
	}

	@Override
	public void defineItem() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bdef);

		FixtureDef  fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / ClasseGame.PPM);
		fdef.filter.categoryBits = ClasseGame.ITEM_BIT;
		fdef.filter.maskBits = ClasseGame.GROUND_BIT|
				ClasseGame.OBJECT_BIT|
				ClasseGame.MARIO_BIT|
				ClasseGame.COIN_BIT|
				ClasseGame.BRICK_BIT;

		fdef.shape = shape;
		body.createFixture(fdef).setUserData(this);
	}

	@Override
	public void use(Mario mario) {
		destroy();
		mario.grow();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
		velocity.y = body.getLinearVelocity().y;
		body.setLinearVelocity(velocity);
	}


}
