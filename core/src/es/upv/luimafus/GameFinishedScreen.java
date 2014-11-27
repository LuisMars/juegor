package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Luis on 25/11/2014.
 */
public class GameFinishedScreen implements Screen {
    Main j;
    Stage stage;
    Table table;
    Skin skin;

    Preferences preferences;
    public GameFinishedScreen (Main j, String winner) {
        this.j = j;

        preferences = Gdx.app.getPreferences("sp");
        stage = new Stage();
        stage.setViewport(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        table.setDebug(false);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage.addActor(table);

        Label winnerLabel = new Label(winner,skin);

        TextButton back = new TextButton("Back", skin);
        TextButton replay = new TextButton("Play again", skin);

        table.add(winnerLabel).colspan(2).pad(10);
        table.row();
        table.add(back).pad(10).prefSize(200,40);;
        table.add(replay).pad(10).prefSize(200,40);;

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new MenuScreen(j));
            }
        });

        replay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    j.setScreen(new GameScreen(j,
                            preferences.getInteger("width"),
                            preferences.getInteger("height"),
                            preferences.getFloat("density"),
                            preferences.getInteger("bots"),
                            preferences.getFloat("difficulty"),
                            preferences.getInteger("speed")));
            }
        });





    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
