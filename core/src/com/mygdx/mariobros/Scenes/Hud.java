package com.mygdx.mariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.ClasseGame;

public class Hud implements Disposable {
	//Scene2D.ui Stage e sua própria viewport para HUD
	public Stage stage;
	private Viewport viewport;


	// Mario score / time Tracking Variables
	private Integer worldTimer;
	private boolean timeUp; 
	private float timeCount;
	private static Integer score;

	// Widgets  Scene2D
	private Label countdownLabel;
	private static Label scoreLabel;
	private Label timeLabel;
	private Label levelLabel;
	private Label worldLabel;
	private Label marioLabel;

	public Hud(SpriteBatch sb) {
		// define nossas variáveis de rastreamento
		worldTimer = 300;
		timeCount = 0;
		score = 0;

		// configura a viewport do HUD usando uma nova câmera separada da nossa gamecam
		// define nosso estágio usando essa viewport e nossos jogos spritebatch
		viewport = new FitViewport(ClasseGame.V_WIDTH, ClasseGame.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, sb);

		// define uma tabela usada para organizar os rótulos do nosso hud
		Table table = new Table();
		// Tabela de alinhamento superior
		table.top();
		// faz a mesa preencher todo o estágio
		table.setFillParent(true);


		// define nossos rótulos usando o String e um estilo Label que consiste em uma fonte e cor
		countdownLabel = new Label(String.format("%03d", worldTimer),new Label.LabelStyle(new BitmapFont(), Color.WHITE)); 
		scoreLabel = new Label(String.format("%06d", score),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel = new Label("TIME" ,new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		levelLabel = new Label("1-1",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		worldLabel = new Label("WORLD",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		marioLabel = new Label("MARIO",new Label.LabelStyle(new BitmapFont(), Color.WHITE));


		// adiciona nossos rótulos à nossa tabela, preenche a parte superior e dá a todos largura igual com expandX
		table.add(marioLabel).expandX().padTop(10);
		table.add(worldLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);


		// adiciona uma segunda linha na nossa mesa
		table.row();
		table.add(scoreLabel).expandX();
		table.add(levelLabel).expandX();
		table.add(countdownLabel).expandX();

		// adiciona nossa mesa ao palco
		stage.addActor(table);
	}

	public void update(float dt) {
		timeCount += dt;
		if(timeCount >= 1){
			if (worldTimer > 0) {
				worldTimer--;
			} else {
				timeUp = true;
			}
			countdownLabel.setText(String.format("%03d", worldTimer));
			timeCount = 0;
		}
	}

	public static void	addScore(int value) {
		score += value;
		scoreLabel.setText(String.format("%06d", score));
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public boolean isTimeUp() {
		return timeUp; 
	}

}
