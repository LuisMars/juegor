package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Luis on 23/11/2014.
 */
public class SinglePlayerMenu implements Screen {

    Stage stage;
    Table table;
    Skin skin;
    JuegoRedes j;

    Preferences preferences;

    public SinglePlayerMenu (JuegoRedes J) {
        j = J;

        preferences = Gdx.app.getPreferences("sp");



        stage = new Stage();
        stage.setViewport(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        table.setDebug(false);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label widthLabel = new Label("Width:", skin);
        Label heightLabel = new Label("Height:", skin);
        Label densityLabel = new Label("Density:", skin);
        Label botLabel = new Label("Bots:", skin);

        Slider widthSlider = new Slider(5,100,1,false,skin);
        Slider heightSlider = new Slider(5,100,1,false,skin);
        Slider densitySlider = new Slider(50,90,1,false,skin);
        Slider botSlider = new Slider(0,10,1,false,skin);

        Label width = new Label("", skin);
        Label height = new Label("", skin);
        Label density = new Label("", skin);
        Label bots = new Label("", skin);

        TextButton play = new TextButton("Play!", skin);
        TextButton back = new TextButton("Back", skin);

        width.setText(""+preferences.getInteger("width"));
        widthSlider.setValue(preferences.getInteger("width"));
        height.setText("" + preferences.getInteger("height"));
        heightSlider.setValue(preferences.getInteger("height"));
        density.setText("" + preferences.getFloat("density"));
        densitySlider.setValue(preferences.getFloat("density") * 100);
        bots.setText("" + preferences.getInteger("bots"));
        botSlider.setValue(preferences.getInteger("bots"));

        table.add(widthLabel);
        table.add(width).pad(10);
        table.row();
        table.add(widthSlider).colspan(2).pad(10).fillX();
        table.row();
        table.add(heightLabel);
        table.add(height).pad(10);
        table.row();
        table.add(heightSlider).colspan(2).pad(10).fillX();
        table.row();
        table.add(densityLabel);
        table.add(density).pad(10);
        table.row();
        table.add(densitySlider).colspan(2).pad(10).fillX();
        table.row();
        table.add(botLabel);
        table.add(bots).pad(10);
        table.row();
        table.add(botSlider).colspan(2).pad(10).fillX();
        table.row();
        table.add(back).pad(10).prefSize(200,40);
        table.add(play).pad(10).prefSize(200,40);
        table.row();



        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new MenuScreen(j));
            }
        });

        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    preferences.putInteger("width",Integer.parseInt(width.getText().toString()));
                    preferences.putInteger("height",Integer.parseInt(height.getText().toString()));
                    preferences.putFloat("density",Float.parseFloat(density.getText().toString()));
                    preferences.putInteger("bots",Integer.parseInt(bots.getText().toString()));
                    preferences.flush();
                    j.setScreen(new GameScreen(j,
                            Integer.parseInt(width.getText().toString()),
                            Integer.parseInt(height.getText().toString()),
                            Double.parseDouble(density.getText().toString()),
                            Integer.parseInt(bots.getText().toString())));
                }
                catch (NumberFormatException e) {
                    width.setText("ERROR");
                }
            }
        });

        widthSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                width.setText(""+(int)widthSlider.getValue());
            }
        });
        heightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                height.setText("" + (int) heightSlider.getValue());
            }
        });
        densitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                density.setText(""+densitySlider.getValue()/100);
            }
        });
        botSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bots.setText(""+(int)botSlider.getValue());
            }
        });
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
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
