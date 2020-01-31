package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.ClasseGame;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Mario extends Sprite {
	public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}
	public State currentState;
	public State previousState;

	public World world;
	public Body b2body;

	private TextureRegion marioStand;
	private Animation marioRun;
	private TextureRegion marioJump;
	private TextureRegion marioDead;

	private TextureRegion bigMarioStand;
	private Animation bigMarioRun;
	private TextureRegion bigMarioJump;

	private Animation growMario;

	private float stateTimer;
	private boolean runningRight;
	private boolean marioIsBig;
	private boolean runGrowAnimation;
	private boolean timeToDefineBigMario;
	private boolean timeToRedefineMario;
	private boolean marioIsDead;

	private PlayScreen screen;

	private Array<Fireball> fireballs;

	public Mario(PlayScreen screen) {
		//super(screen.getAtlas().findRegion("Mario Pequeno"));
		this.screen = screen;
		this.world = screen.getWorld();
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;

		Array<TextureRegion> frames = new Array<TextureRegion>();

		for(int i = 1 ; i < 4 ; i ++ ) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Pequeno"), i * 16, 0, 16 , 16 ));
		}
		marioRun = new Animation(0.1f, frames);

		frames.clear();

		for(int i = 1 ; i < 4 ; i ++ ) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Grande"), i * 16, 0, 16 , 32 ));
		}
		bigMarioRun = new Animation(0.1f, frames);

		frames.clear();

		frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Grande"), 240,0,16,32 ));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Grande"), 0,0,16,32 ));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Grande"), 240,0,16,32 ));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario Grande"), 0,0,16,32 ));
		growMario = new Animation(0.2f, frames);

		marioJump = new TextureRegion(screen.getAtlas().findRegion("Mario Pequeno"),80,0,16,16);
		bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("Mario Grande"),80,0,16,32);

		marioStand = new TextureRegion(screen.getAtlas().findRegion("Mario Pequeno"),0,0,16,16);
		bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("Mario Grande"),0,0,16,32);

		marioDead = new TextureRegion(screen.getAtlas().findRegion("Mario Pequeno"), 96, 0, 16, 16);

		defineMario();		

		setBounds(0, 0, 16/ClasseGame.PPM , 16/ClasseGame.PPM );
		setRegion(marioStand);

		fireballs = new Array<Fireball>();
	}	

	public void update(float dt ) {

		if ((screen.getHud().isTimeUp() && !isDead()) || b2body.getPosition().y < 0 ) {
			die();
		}

		if(marioIsBig) {
			setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2 - 6/ClasseGame.PPM);
		}
		else {
			setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
		}

		setRegion(getFrame(dt));
		if(timeToDefineBigMario) {
			defineBigMario();
		}
		if(timeToRedefineMario) {
			redefineMario();		
		}

		for(Fireball  ball : fireballs) {
			ball.update(dt);
			if(ball.isDestroyed())
				fireballs.removeValue(ball, true);
		}
	}


	public TextureRegion getFrame(float dt) {
		currentState = getState();

		TextureRegion region;

		switch(currentState) {
		case DEAD:
			region = (TextureRegion) marioDead;
			break;
		case GROWING: 
			region = (TextureRegion) growMario.getKeyFrame(stateTimer);
			if(growMario.isAnimationFinished(stateTimer)) {
				runGrowAnimation = false;
			}
			break;
		case JUMPING:
			region = marioIsBig ? (TextureRegion) bigMarioJump :(TextureRegion) marioJump;
			break;
		case RUNNING:
			region = marioIsBig ?(TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
			break;
		case FALLING:
		case STANDING:
		default:
			region = marioIsBig ? (TextureRegion) bigMarioStand : (TextureRegion)marioStand;
			break;

		}

		if((b2body.getLinearVelocity().x < 0 || !runningRight ) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
		else if((b2body.getLinearVelocity().x > 0 || runningRight ) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}

		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}

	public State getState() {
		if(marioIsDead) {
			return State.DEAD;
		}
		else if(runGrowAnimation) {
			return State.GROWING;
		}	
		else if (b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y  < 0 && previousState == State.JUMPING ){
			return State.JUMPING;
		}
		else if(b2body.getLinearVelocity().y  < 0) {
			return State.FALLING;
		}
		else if (b2body.getLinearVelocity().x != 0) {
			return State.RUNNING;
		}
		else {
			return State.STANDING;
		}

	}


	public void grow() {
		if( !isBig() ) {
			runGrowAnimation = true;
			marioIsBig = true;
			timeToDefineBigMario = true;
			setBounds(getX(), getY(), getWidth(), getHeight() * 2);
			ClasseGame.manager.get("Sons/powerup.wav", Sound.class).play();
		}
	}

	public void die() {
		if (!isDead()) {
			ClasseGame.manager.get("Musica/mario_music.ogg", Music.class).stop();
			ClasseGame.manager.get("Sons/mariodie.wav",Sound.class).play();
			marioIsDead = true;
			Filter filter = new Filter();
			filter.maskBits = ClasseGame.NOTHING_BIT;

			for(Fixture fixture : b2body.getFixtureList()) {
				fixture.setFilterData(filter);
			}

			b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
		}
	}	


	public boolean isDead() {
		return marioIsDead;
	}

	public float getStateTimer() {
		return stateTimer;
	}

	public boolean isBig(){
		return marioIsBig;
	}

	public void jump(){
		if ( currentState != State.JUMPING ) {
			b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
			currentState = State.JUMPING;
		}
	}

	public void hit(Enemy enemy) {
		if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
			((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
		}
		else {
			if(marioIsBig) {
				marioIsBig = false;
				timeToRedefineMario = true;
				setBounds(getX(), getY(), getWidth(), getHeight()/2);
				ClasseGame.manager.get("Sons/powerdown.wav", Sound.class).play();
			}
			else {
				die();
			}
		}
	}

	public void redefineMario() {
		Vector2 position = b2body.getPosition();
		world.destroyBody(b2body);

		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		FixtureDef  fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / ClasseGame.PPM);
		fdef.filter.categoryBits = ClasseGame.MARIO_BIT;
		fdef.filter.maskBits = ClasseGame.GROUND_BIT |
				ClasseGame.COIN_BIT |
				ClasseGame.BRICK_BIT|
				ClasseGame.ENEMY_BIT|
				ClasseGame.OBJECT_BIT|
				ClasseGame.ENEMY_HEAD_BIT|
				ClasseGame.ITEM_BIT;

		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);;

		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/ClasseGame.PPM, 6/ClasseGame.PPM), new Vector2(2/ClasseGame.PPM, 6/ClasseGame.PPM));
		fdef.filter.categoryBits = ClasseGame.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;

		b2body.createFixture(fdef).setUserData(this);

		timeToRedefineMario = false;
	}

	public void defineBigMario() {
		Vector2 currentPosition = b2body.getPosition();
		world.destroyBody(b2body);

		BodyDef bdef = new BodyDef();
		bdef.position.set(currentPosition.add(0,10 /ClasseGame.PPM));
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		FixtureDef  fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / ClasseGame.PPM);
		fdef.filter.categoryBits = ClasseGame.MARIO_BIT;
		fdef.filter.maskBits = ClasseGame.GROUND_BIT |
				ClasseGame.COIN_BIT |
				ClasseGame.BRICK_BIT|
				ClasseGame.ENEMY_BIT|
				ClasseGame.OBJECT_BIT|
				ClasseGame.ENEMY_HEAD_BIT|
				ClasseGame.ITEM_BIT;

		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		shape.setPosition(new Vector2(0, -14/ClasseGame.PPM));
		b2body.createFixture(fdef).setUserData(this);

		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/ClasseGame.PPM, 6/ClasseGame.PPM), new Vector2(2/ClasseGame.PPM, 6/ClasseGame.PPM));
		fdef.filter.categoryBits = ClasseGame.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;

		b2body.createFixture(fdef).setUserData(this);
		timeToDefineBigMario = false;

	}

	public void defineMario() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(32 / ClasseGame.PPM, 32 / ClasseGame.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		FixtureDef  fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / ClasseGame.PPM);
		fdef.filter.categoryBits = ClasseGame.MARIO_BIT;
		fdef.filter.maskBits = ClasseGame.GROUND_BIT |
				ClasseGame.COIN_BIT |
				ClasseGame.BRICK_BIT|
				ClasseGame.ENEMY_BIT|
				ClasseGame.OBJECT_BIT|
				ClasseGame.ENEMY_HEAD_BIT|
				ClasseGame.ITEM_BIT;

		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);;

		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/ClasseGame.PPM, 6/ClasseGame.PPM), new Vector2(2/ClasseGame.PPM, 6/ClasseGame.PPM));
		fdef.filter.categoryBits = ClasseGame.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;

		b2body.createFixture(fdef).setUserData(this);
	}
	
	public void fire(){
		fireballs.add(new Fireball(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
	}

	public void draw(Batch batch){
		super.draw(batch);
		for(Fireball ball : fireballs)
			ball.draw(batch);
	}
	public void correr() {
		b2body.applyLinearImpulse( new Vector2(0.1f, 0), b2body.getWorldCenter(), true );
	}
}
