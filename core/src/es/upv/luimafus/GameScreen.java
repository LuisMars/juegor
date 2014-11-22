package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Luis on 22/11/2014.
 */
public class GameScreen implements Screen {
    Map GameMap = new Map(20, 20, 0.75);

    SpriteBatch batch;

    AssetManager assets;

    float time = 0;
    boolean turn = true;
    int width, height;
    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;

    JuegoRedes j;

    public GameScreen(JuegoRedes J) {
        j = J;
    }

    @Override
    public void render(float delta) {
        getInput();
        camera.update();
        time += Gdx.graphics.getDeltaTime();
        if (time >= 0.1) {
            //fpsLogger.log();
            time -= 0.1;
            if(turn) {
                GameMap.updateState();
                for (int i = 0; i < GameMap.getWidth(); i++)
                    for (int j = 0; j < GameMap.getHeight(); j++)
                        if (assets.altars[i][j] != -1)
                            assets.altars[i][j] = (assets.altars[i][j] + 1) % assets.altar.length;
            }
            GameMap.updateAttacks();
            turn = !turn;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.begin();
        for(int i = 0; i < GameMap.getWidth(); i++)
            for (int j = 0; j < GameMap.getHeight(); j++) {
                batch.setColor(1,1,1,1);
                float d =Utils.fDistance(i, j, GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY());
                if(d > 15)
                    continue;
                if(d > 5) {
                    batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);
                }
                batch.draw(assets.map[i][j], i * 16, (GameMap.getHeight() - 1 - j) * 16);
                if(assets.altars[i][j] != -1)
                    batch.draw(assets.altar[assets.altars[i][j]],i * 16, (GameMap.getHeight() - 1 - j) * 16);
            }

        for(Player p : GameMap.getPlayers()) {
            batch.setColor(1,1,1,1);
            float d = Utils.fDistance(GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY(), p.getX(), p.getY());
            if(d > 15)
                continue;
            if(d > 5) {
                batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);

            }
            batch.draw(assets.player[p.getID()], p.getX() * 16, trueHeight() - (p.getY() * 16));
            //if(!p.isBot())  batch.draw(cursor, p.getX() * 16, trueHeight() - (p.getY() * 16));

            if(d > 5)
                continue;
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(p.getX() * 16, trueHeight() - ((p.getY() - 1) * 16), 16, 4);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(p.getX() * 16 + 1, trueHeight() - 1 - ((p.getY() - 1) * 16), 15, 3);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect( p.getX() * 16 +1, trueHeight() - 1 - ((p.getY()-1) * 16),(int)(15*p.getHP()),3);



        }

        for(Attack a : GameMap.getAttacks()) {
            batch.setColor(1,1,1,1);
            float d = Utils.fDistance(a.getX(),a.getY(),GameMap.humanPlayer.getX(),GameMap.humanPlayer.getY());
            if(d > 15)
                continue;
            if(d > 5) {
                batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);
            }
            if (a.getDirection() != -1)
                batch.draw(assets.arrow[a.getDirection()], a.getX() * 16, trueHeight() - (a.getY() * 16));
            else
                batch.draw(assets.area[a.getTime(assets.area.length)], a.getX() * 16, trueHeight() - (a.getY() * 16));
        }
        if(GameMap.haveAWinner()) {
            System.out.println(GameMap.winner());
            j.setScreen(new MenuScreen(j));
        }
        batch.end();
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

        camera.setToOrtho(false,width,height);
        camera.position.set(GameMap.humanPlayer.getX()*16,trueHeight()-(GameMap.humanPlayer.getY()*16),0);
    }

    @Override
    public void show() {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        assets = new AssetManager(GameMap);

        camera = new OrthographicCamera(width,height);

        GameMap.addPlayer(new Player(GameMap,false));
        for(int i = 0; i < 1; i++)
            GameMap.addPlayer(new Player(GameMap,true));

        camera.position.set(GameMap.humanPlayer.getX()*16,trueHeight()-(GameMap.humanPlayer.getY()*16),0);
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

    public void updateCameraPos() {
        float lerp = 1f;
        Vector3 position = camera.position;
        position.x += (GameMap.humanPlayer.getX()*16 - position.x) * lerp * Gdx.graphics.getDeltaTime();
        position.y += (trueHeight()-(GameMap.humanPlayer.getY()*16) - position.y) * lerp * Gdx.graphics.getDeltaTime();
    }

    private int trueHeight() {
        return ((GameMap.getHeight()-1)*16);
    }


    private void getInput() {
        if(Gdx.input.isTouched())
            camera.position.add(-Gdx.input.getDeltaX(),Gdx.input.getDeltaY(),0);
        else
            updateCameraPos();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            GameMap.act(Player.UP);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            GameMap.act(Player.DOWN);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            GameMap.act(Player.RIGHT);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            GameMap.act(Player.LEFT);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            GameMap.act(Player.AUP);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            GameMap.act(Player.ADOWN);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            GameMap.act(Player.ARIGHT);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            GameMap.act(Player.ALEFT);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            GameMap.act(Player.AREA);
        }
    }
}
