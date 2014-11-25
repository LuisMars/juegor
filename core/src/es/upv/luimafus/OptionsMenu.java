package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Luis on 25/11/2014.
 */
public class OptionsMenu implements Screen {
    JuegoRedes j;
    Stage stage;
    Table table;
    Skin skin;

    Preferences preferences;
    public OptionsMenu (JuegoRedes j) {
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

        Label speedLabel = new Label("Game frequency:", skin);
        Label speed = new Label("", skin);

        Slider speedSlider = new Slider(30,100,1,false,skin);
        TextButton back = new TextButton("Back", skin);
        TextButton save = new TextButton("Save", skin);

        speed.setText("" + preferences.getInteger("speed"));
        speedSlider.setValue(preferences.getInteger("speed"));


        table.add(speedLabel).pad(10);
        table.add(speed).pad(10);
        table.row();
        table.add(speedSlider).colspan(2).pad(10).fillX();
        table.row();
        table.add(back).pad(10).prefSize(200, 40);
        table.add(save).pad(10).prefSize(200, 40);
        stage.addActor(table);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new MenuScreen(j));
            }
        });
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    preferences.putInteger("speed", Integer.parseInt(speed.getText().toString()));
                    preferences.flush();
                    j.setScreen(new MenuScreen(j));
                }
                catch (NumberFormatException e) {
                    speed.setText("ERROR");
                }
            }
        });
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                speed.setText((int)speedSlider.getValue()+"");
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

    }
}
