package es.upv.luimafus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Created by Luis on 22/11/2014.
 */
public class MenuScreen implements Screen {

    Stage stage;
    Table table;
    Skin skin;
    JuegoRedes j;

    public MenuScreen(JuegoRedes J) {
        j = J;
        stage = new Stage();

        table = new Table();
        table.setFillParent(true);

        skin = new Skin(Gdx.files.internal("ui/ui-gray.json"));

        TextButton SP = new TextButton("Single Player", skin);
        TextButton MP = new TextButton("Multiplayer", skin);
        TextButton Options = new TextButton("Options", skin);

        TextButton Exit = new TextButton("Exit", skin);

        table.add(SP).pad(10).prefSize(200,40);
        table.row();
        table.add(MP).pad(20).prefSize(200, 40);
        table.row();
        table.add(Options).pad(20).prefSize(200, 40);
        table.row();
        table.add(Exit).pad(20).prefSize(200, 40);

        SP.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new GameScreen(j));
            }
        });

        Exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.setFillParent(true);
        table.setDebug(false);
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
