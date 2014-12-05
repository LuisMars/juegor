package es.upv.luimafus.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import es.upv.luimafus.Main;

import java.io.IOException;

/**
 * Created by Luis on 26/11/2014.
 */
public class ServerScreen implements Screen {
    Stage stage;
    Table table;
    Skin skin;
    Main j;
    Preferences preferences;
    Server server;
    TextField port;
    TextArea log;
    TextButton connectButton;
    TextButton disconnectButton;
    public  ServerScreen(Main main) {
        j = main;
        stage = new Stage();

        table = new Table();
        table.setFillParent(true);
        table.setFillParent(true);
        table.setDebug(false);
        stage.setViewport(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Gdx.input.setInputProcessor(stage);


        port = new TextField("",skin);
        connectButton = new TextButton("Create",skin);
        disconnectButton = new TextButton("Disconnect",skin);
        log = new TextArea("",skin);
        disconnectButton.setDisabled(true);

        table.add(port);
        table.add(connectButton);
        table.add(disconnectButton);
        table.row();
        table.add(log).colspan(3).fill().prefSize(300,400);


        connectButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startServer();
            }
        });
        disconnectButton.addCaptureListener( new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {


                try {
                    //server.disconnect();
                    disconnectButton.setDisabled(true);
                    connectButton.setDisabled(false);
                } catch (Exception e) {
                    print("Server closed");
                }
            }
        });


        stage.addActor(table);
    }
    public void startServer() {
        try {
            server = new Server(this,Integer.parseInt(port.getText()));
            //server.start();

            connectButton.setDisabled(true);
            disconnectButton.setDisabled(false);
        }
        catch (NumberFormatException e) {
            print("Invalid server address");
        }
    }
    public void print(String msg) {
        log.setText(log.getText() + "\n" + msg);
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

    }
}
