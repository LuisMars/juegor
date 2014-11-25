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
        Label densityLabel = new Label("Walkable area:", skin);
        Label botLabel = new Label("Bots:", skin);
        Label difficultyLabel = new Label("Difficulty:", skin);

        Slider widthSlider = new Slider(5,100,1,false,skin);
        Slider heightSlider = new Slider(5,100,1,false,skin);
        Slider densitySlider = new Slider(0.5f,0.9f,0.01f,false,skin);
        Slider botSlider = new Slider(1,10,1,false,skin);
        Slider difficultySlider = new Slider(0,1,0.01f,false,skin);

        Label width = new Label("", skin);
        Label height = new Label("", skin);
        Label density = new Label("", skin);
        Label bots = new Label("", skin);
        Label difficulty = new Label ("", skin);

        TextButton play = new TextButton("Play!", skin);
        TextButton back = new TextButton("Back", skin);

        width.setText(""+preferences.getInteger("width",50));
        widthSlider.setValue(preferences.getInteger("width",50));
        height.setText("" + preferences.getInteger("height",50));
        heightSlider.setValue(preferences.getInteger("height",50));
        density.setText(preferences.getFloat("density",0.75f)*100 + "%");
        densitySlider.setValue(preferences.getFloat("density",0.75f));
        bots.setText("" + preferences.getInteger("bots",5));
        botSlider.setValue(preferences.getInteger("bots",5));
        difficulty.setText(preferences.getFloat("difficulty", 0.5f) + "%");
        difficultySlider.setValue(preferences.getFloat("difficulty", 0.5f));

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
        table.add(difficultyLabel);
        table.add(difficulty).pad(10);
        table.row();
        table.add(difficultySlider).colspan(2).pad(10).fillX();
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
                    preferences.putInteger("width",(int)widthSlider.getValue());
                    preferences.putInteger("height",(int)heightSlider.getValue());
                    preferences.putFloat("density",densitySlider.getValue());
                    preferences.putInteger("bots",(int)botSlider.getValue());
                    preferences.putFloat("difficulty",difficultySlider.getValue());
                    preferences.flush();
                    j.setScreen(new GameScreen(j,
                            preferences.getInteger("width"),
                            preferences.getInteger("height"),
                            preferences.getFloat("density"),
                            preferences.getInteger("bots"),
                            preferences.getFloat("difficulty"),
                            preferences.getInteger("speed")));
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
                density.setText((int)(densitySlider.getValue() * 100)+"%");
            }
        });
        botSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bots.setText(""+(int)botSlider.getValue());
            }
        });
        difficultySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                difficulty.setText((int)(difficultySlider.getValue()*100)+"%");
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
        stage.dispose();
    }
}
