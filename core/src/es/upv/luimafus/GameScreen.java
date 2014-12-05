package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

    public AssetManager assets;
    int c = 32;
    long time = 0;
    boolean turn = true;
    boolean lastTurn = true;
    int width, height, speed;

    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;

    Main j;

    public GameScreen() {

    }

    public GameScreen(Main j, int w, int h, double d, int bots, float diff, int speed) {

        assets = new AssetManager();
        this.j = j;
        GameMap = new Map(this, w, h, d);
        Player.difficulty = diff;
        GameMap.addPlayer(new Player(GameMap,false));
        for(int i = 0; i < bots; i++)
            GameMap.addPlayer(new Player(GameMap,true));
        this.speed = speed;
        AssetManager.ambient.play();
        AssetManager.ambient.setLooping(true);

    }

    @Override
    public void render(float delta) {

        //teclas de entrada
        getInput();

        if(TimeUtils.timeSinceMillis(time) > speed) {
            // los ataques cada x
            GameMap.updateAttacks();
            if(turn) {
                //actualizar los jugadores cada 2x
                GameMap.updateState();
            }
            turn = !turn;
            time = TimeUtils.millis();
        }

        float offset = (TimeUtils.timeSinceMillis(time)/(2f*speed)) + (turn?0.5f:0);

        //cosas de la librería para repintar y actualizar la cámara
        Gdx.gl.glClearColor(0.0549019607843137f,0.0509803921568627f,0.06274509803921568627450980392157f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.begin();

        drawMap();

        drawAttacks();

        drawPlayers(offset);


        //si hay un ganador, vuelve a la pantalla principal
        //TODO: mostrar ganador!!!
        if(GameMap.haveAWinner()) {
            //j.setScreen(new MenuScreen(j));
            AssetManager.ambient.stop();
            j.setScreen(new GameFinishedScreen(j,GameMap.winner()));
        }
        batch.end();
        shapeRenderer.end();
    }

    private void drawMap() {
        for(int i = 0; i < GameMap.getWidth(); i++)
            for (int j = 0; j < GameMap.getHeight(); j++) {
                batch.setColor(1,1,1,1);
                //para oscurecer lo lejano
                float d = Utils.fDistance(i, j, GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY());
                if(d > 15)
                    continue;
                if(d > 5) {
                    batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);
                }
                if(assets.floor.getTile(GameMap.drawMap[i][j]) != null)

                batch.draw(assets.floor.getTile(GameMap.drawMap[i][j]),
                        (i * c)-0.25f/c, ((GameMap.getHeight() - 1 - j) * c)-0.25f/c,
                        c+(0.5f/c),c+(0.5f/c));
            }
    }

    private void drawPlayers(float offset) {
        for(Player p : GameMap.getPlayers()) {
            float xOffset = p.drawPosX(offset);
            float yOffset = p.drawPosY(offset);
            batch.setColor(1,1,1,1);
            //oscurecer los jugadores lejanos
            float d = Utils.fDistance(GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY(), p.getX(), p.getY());
            if(d > 15)
                continue;
            if(d > 5) {
                batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1);

            }
            //dibujar y animar cutremente
            batch.draw(assets.player[p.getID()%5][p.lastDir], xOffset*c, trueHeight() - yOffset*c + c/4);
            //if(!p.isBot())  batch.draw(cursor, p.getX() * 16, trueHeight() - (p.getY() * 16));
            if(p == GameMap.humanPlayer)
                System.out.println(p.drawPosX(offset) + "\t" + p.drawPosY(offset) + "\t" + offset);
            if(d > 5)
                continue;

            assets.font.draw(batch, p.name, xOffset*c, trueHeight() + c/2 - ((yOffset-1)*c));
            //la barrita de vida con el color correspondiente
            shapeRenderer.setColor(p.getHPColor());
            shapeRenderer.rect( xOffset*c, trueHeight() + c/2 - ((yOffset-1)*c),(int)((c-2)*p.getHP()),c/8);



        }
    }

    private void drawAttacks() {
        for(Attack a : GameMap.getAttacks()) {
            batch.setColor(1,1,1,1);
            //oscurecer los ataques lejanos
            float d = Utils.fDistance(a.getX(), a.getY(), GameMap.humanPlayer.getX(), GameMap.humanPlayer.getY());
            if(d > 15)
                continue;
            if(d > 5) {
                batch.setColor(1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f, 1 - (d-5)/ 10f);
            }
            //dibujar lo que corresponda en su posición
            if (a.getDirection() != -1)
                batch.draw(assets.arrow[a.getDirection()], a.getX() * c, trueHeight() - (a.getY() * c) + c/4);
            else
                batch.draw(assets.area[a.time], a.getX() * c, trueHeight() - (a.getY() * c));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        camera.position.set(GameMap.humanPlayer.getX()*c,trueHeight()-(GameMap.humanPlayer.getY()*c),0);
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
        float lerp = 2.5f;
        Vector3 position = camera.position;
        //para hacer que la cámara siga al jugador de forma suave
        position.x =  Math.round(position.x+(GameMap.humanPlayer.getX() * c - position.x) * lerp * Gdx.graphics.getDeltaTime());
        position.y =  Math.round(position.y+(trueHeight() - (GameMap.humanPlayer.getY() * c) - position.y) * lerp * Gdx.graphics.getDeltaTime());
    }

    private int trueHeight() {
        return ((GameMap.getHeight()-1)*c);
    }


    private void getInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.PERIOD))
            camera.zoom += 0.25;
        if(camera.zoom > 0.25 && Gdx.input.isKeyJustPressed(Input.Keys.MINUS))
            camera.zoom -= 0.25;
        if(Gdx.input.isTouched())
            camera.position.add(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
        //else
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
