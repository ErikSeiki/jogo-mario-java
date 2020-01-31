package com.mygdx.mariobros.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.ClasseGame;

public class GameOverScreen implements Screen  {
	private Viewport viewport;
	private Stage stage;

	private Game game;

	ClasseGame game2;
	Texture img;
	
	public GameOverScreen(Game game) {
		this.game = game;
		game2 = new ClasseGame();
		viewport = new FitViewport(ClasseGame.V_WIDTH, ClasseGame.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, ((ClasseGame) game).batch);
		
		img = new Texture("badlogic.jpg");
		
		Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
		
		Table table = new Table();
		table.center();
		table.setFillParent(true);

		Label gameOverLabel = new Label("GAME OVER",font);
		Label playAgainLabel = new Label("Click or Press Enter to Play Again",font);
		
		table.add(gameOverLabel).expandX();
		table.row();
		table.add(playAgainLabel).expandX().padTop(10f);
		
		stage.addActor(table);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if(Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			
			game.setScreen(new PlayScreen((ClasseGame) game));
			dispose();
		}
		game2.batch.draw(img, 0, 0);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {

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
		stage.dispose();
	}

}
