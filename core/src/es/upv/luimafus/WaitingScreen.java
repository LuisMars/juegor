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
 * Created by Luis on 26/11/2014.
 */
public class WaitingScreen implements Screen {
    Main j;
    GameScreen gameScreen;

    Stage stage;
    Table table;
    Skin skin;

    Preferences preferences;

    String name;
    TextArea chat;
    TextField input;
    ScrollPane scrollPane;
    Client client;


    public WaitingScreen(Main j, String name) {
        this.j = j;
        this.name = name;

        preferences = Gdx.app.getPreferences("mp");

        client = new Client(this, preferences.getString("address"));
        name = preferences.getString("name");

        stage = new Stage();
        stage.setViewport(new ScreenViewport());


        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        chat = new TextArea("", skin);


        scrollPane = new ScrollPane(chat, skin);
        scrollPane.setForceScroll(false, true);
        scrollPane.setFlickScroll(false);
        scrollPane.setOverscroll(false, true);


        input = new TextField("", skin);
        TextButton send = new TextButton("Send", skin);
        TextButton play = new TextButton("Play!", skin);
        TextButton disconnect = new TextButton("Disconnect", skin);

        chat.setDisabled(true);

        table.add(scrollPane).pad(10).colspan(6).prefSize(400, 400).fill();
        table.row();
        table.add(input).pad(10).colspan(5).fill().prefSize(350, 20).fillX();
        table.add(send).colspan(1).pad(10).prefSize(50, 20).fillX();
        table.row();
        table.add(disconnect).colspan(3).pad(10).prefSize(200, 40).fillX();
        table.add(play).pad(10).colspan(3).prefSize(200, 40).fillX();

        stage.addActor(table);

        input.setTextFieldListener((textField, key) -> {
            if ((key == '\r' || key == '\n'))
                sendMessage();
        });

        send.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendMessage();
            }


        });

        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendReady();
            }
        });


        disconnect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                j.setScreen(new MultiplayerMenu(j));
                //TODO: disconnect msg
                //user.disconnect();
            }
        });


        stage.setKeyboardFocus(input);


    }

    public void initializeMap(int[][] map, int speed) {
        gameScreen = new GameScreen(j, map, speed);
        Client.iHaveMap();
    }

    public void addPlayer(byte id, String player, byte x, byte y, int chp, boolean controllable) {

        if (controllable) {
            gameScreen.GameMap.addHumanPlayer(new Player(gameScreen.GameMap, id, player, x, y, chp));
        } else {
            gameScreen.GameMap.addPlayer(new Player(gameScreen.GameMap, id, player, x, y, chp));
        }
    }

    public void setPlayer(byte id, byte x, byte y, byte lastdir, byte chp, byte att) {
        gameScreen.GameMap.getPlayers().stream().filter(
                p -> p.getID() == id).forEach(p -> p.updateState(x, y, lastdir, chp, att));
    }

    public void resetPlayer(byte id) {
        gameScreen.GameMap.getPlayers().stream().filter(
                p -> p.getID() == id).forEach(p -> p.setcHP(p.HP));
    }

    public void startGame() {
        j.setScreen(gameScreen);
    }


    public void endGame() {
        print("You died.");
        j.setScreen(this);
    }
    public void print(String msg) {
        chat.setText(chat.getText() + "\n" + msg);
        chat.setPrefRows(chat.getText().split("\n").length);
        scrollPane.layout();
        scrollPane.setScrollPercentY(1);
    }

    private void sendReady() {
        client.requestMap();
    }

    private void sendMessage() {
        if (!input.getText().trim().isEmpty()) {
            String msg = input.getText().trim();
            //print(msg);
            client.sendChatMsg(msg);
        }
        input.setText("");
    }

    @Override
    public void render(float delta) {
        if (j.getScreen() == this) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.draw();
            stage.act(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.setSize(width, height);
    }

    @Override
    public void show() {
        input.setText("");
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
