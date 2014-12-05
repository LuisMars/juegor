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
import es.upv.luimafus.server.ServerScreen;

/**
 * Created by Luis on 26/11/2014.
 */
public class MultiplayerMenu implements Screen {
    Main j;
    Stage stage;
    Table table;
    Skin skin;

    Preferences preferences;
    public MultiplayerMenu(Main j) {
        this.j = j;

        preferences = Gdx.app.getPreferences("mp");
        stage = new Stage();
        stage.setViewport(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        table.setDebug(false);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));



        Label addressLabel = new Label("IP address:", skin);
        TextField address = new TextArea(preferences.getString("address"),skin);

        Label nameLabel = new Label("Name:", skin);
        TextField name = new TextArea(preferences.getString("name"),skin);

        TextButton back = new TextButton("Back", skin);
        TextButton connect = new TextButton("Connect", skin);

        TextButton createServer = new TextButton("Create server", skin);




        table.add(addressLabel).pad(10);
        table.add(address).pad(10).fillX();
        table.row();
        table.add(nameLabel).pad(10);
        table.add(name).pad(10).fillX();
        table.row();
        table.add(back).pad(10).prefSize(200, 40);
        table.add(connect).pad(10).prefSize(200, 40);
        table.row();
        table.add(createServer).pad(10).prefSize(200, 40);
        stage.addActor(table);

        address.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new MenuScreen(j));
            }
        });
        connect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    preferences.putString("address", address.getText());
                    preferences.putString("name", name.getText());
                    preferences.flush();
                    j.setScreen(new WaitingScreen(j,name.getText()));
                }
                catch (NumberFormatException e) {
                    name.setText("ERROR");
                }
            }
        });
        createServer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new ServerScreen(j));
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
        table.setSize(width,height);
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
