package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Luis on 22/11/2014.
 */
public class GameScreen implements Screen {
    Map GameMap;

    SpriteBatch batch;

    AssetManager assets;

    long time = 0;
    boolean turn = true;
    boolean lastTurn = true;
    int width, height;
    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;

    JuegoRedes j;

    public GameScreen(JuegoRedes J) {
        j = J;
    }

    public GameScreen(JuegoRedes j, int w, int h, double d, int bots) {

        assets = new AssetManager();
        this.j = j;
        GameMap = new Map(this, w, h, d);
        GameMap.addPlayer(new Player(GameMap,false));
        for(int i = 0; i < bots; i++)
            GameMap.addPlayer(new Player(GameMap,true));
    }

    @Override
    public void render(float delta) {
        getInput();
        camera.update();
        if(TimeUtils.timeSinceMillis(time) > 85) {
            if(turn) {
                GameMap.updateState();
            }
            GameMap.updateAttacks();
            turn = !turn;
            time = TimeUtils.millis();
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
                if(assets.floor.getTile(GameMap.drawMap[i][j]) != null)
                batch.draw(assets.floor.getTile(GameMap.drawMap[i][j]), i * 16, (GameMap.getHeight() - 1 - j) * 16);
                //if(assets.altars[i][j] != -1)
                //    batch.draw(assets.altar,i * 16, (GameMap.getHeight() - 1 - j) * 16);
            }

        for(Player p : GameMap.getPlayers()) {
            batch.setColor(1,1,1,1);
            float d = Utils.fDistance(GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY(), p.getX(), p.getY());
            if(d > 15)
                continue;
            if(d > 5) {
                batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);

            }
            batch.draw(assets.player[turn?0:1], p.getX() * 16, trueHeight() - (p.getY() * 16));
            //if(!p.isBot())  batch.draw(cursor, p.getX() * 16, trueHeight() - (p.getY() * 16));

            if(d > 5)
                continue;

            shapeRenderer.setColor(p.getHPColor());
            shapeRenderer.rect( p.getX() * 16, trueHeight() + 5 - ((p.getY()-1) * 16),(int)(14*p.getHP()),2);



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
                batch.draw(assets.area, a.getX() * 16, trueHeight() - (a.getY() * 16));
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


        camera = new OrthographicCamera(width,height);
        camera.zoom = 0.5f;


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
        if(!position.isZero(0.25f)) {
            position.x += (GameMap.humanPlayer.getX() * 16 - position.x) * lerp * Gdx.graphics.getDeltaTime();
            position.y += (trueHeight() - (GameMap.humanPlayer.getY() * 16) - position.y) * lerp * Gdx.graphics.getDeltaTime();
        }
    }

    private int trueHeight() {
        return ((GameMap.getHeight()-1)*16);
    }


    private void getInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.PLUS))
            camera.zoom += 0.06125;
        if(Gdx.input.isKeyPressed(Input.Keys.MINUS))
            camera.zoom -= 0.06125;
        if(Gdx.input.isTouched())
            camera.position.add(-Gdx.input.getDeltaX(),Gdx.input.getDeltaY(),0);
        else
            updateCameraPos();

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
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
        else if(Gdx.input.isKeyPressed(Input.Keys.W)) {
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
    }
}
