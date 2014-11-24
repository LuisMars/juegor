package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Luis on 23/11/2014.
 */
public class SinglePlayerMenu implements Screen {

    Stage stage;
    Table table;
    Skin skin;
    JuegoRedes j;

    public SinglePlayerMenu (JuegoRedes J) {
        j = J;

        stage = new Stage();
        stage.setViewport(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        table.setDebug(false);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label widthLabel = new Label("Width", skin);
        Label heightLabel = new Label("Height", skin);
        Label densityLabel = new Label("Density", skin);
        Label botLabel = new Label("Bots", skin);

        Slider widthSlider = new Slider(5,100,1,false,skin);
        Slider heightSlider = new Slider(5,100,1,false,skin);
        Slider densitySlider = new Slider(50,90,1,false,skin);
        Slider botSlider = new Slider(0,10,1,false,skin);

        TextArea width = new TextArea("", skin);
        TextArea height = new TextArea("", skin);
        TextArea density = new TextArea("", skin);
        TextArea bots = new TextArea("", skin);

        TextButton play = new TextButton("Play!", skin);
        TextButton back = new TextButton("Back", skin);
        table.add(widthLabel).colspan(2);
        table.row();
        table.add(width).pad(10).prefSize(30, 30);
        table.add(widthSlider).pad(10).prefSize(200, 30);
        table.row();
        table.add(heightLabel).colspan(2);
        table.row();
        table.add(height).pad(10).prefSize(30, 40);
        table.add(heightSlider).pad(10).prefSize(200, 30);
        table.row();
        table.add(densityLabel).colspan(2);
        table.row();
        table.add(density).pad(10).prefSize(30, 30);
        table.add(densitySlider).pad(10).prefSize(200, 30);
        table.row();
        table.add(botLabel).colspan(2);
        table.row();
        table.add(bots).pad(10).prefSize(30, 30);
        table.add(botSlider).pad(10).prefSize(200, 30);
        table.row();
        table.add(back).pad(10).prefSize(100, 40);
        table.add(play).pad(10).prefSize(100, 40);
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
                    j.setScreen(new GameScreen(j,
                            Integer.parseInt(width.getText()),
                            Integer.parseInt(height.getText()),
                            Double.parseDouble(density.getText()),
                            Integer.parseInt(bots.getText())));
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
