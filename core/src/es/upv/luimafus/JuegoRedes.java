package es.upv.luimafus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class JuegoRedes extends ApplicationAdapter {

    Map GameMap = new Map(25, 20, 0.75);

	SpriteBatch batch;
	Texture[] floor;
    Texture[] wall;
    Texture[] player;
    Texture[] arrow;
    Texture[] area;
    Texture[] altar;

    Texture cursor;
    int[][] altars;

    Texture[][] map;
    TextureRegion region;
    BitmapFont font;

    float time = 0;
    boolean turn = true;
    int width, height;
    OrthographicCamera camera;

    ShapeRenderer shapeRenderer;

    FPSLogger fpsLogger = new FPSLogger();
	@Override
	public void create () {


        Gdx.graphics.setDisplayMode(Map.getWidth()*32, Map.getHeight()*32,false);

        floor = new Texture[8];
        wall = new Texture[12];

        altar = new Texture[8];

        player = new Texture[8];

        arrow = new Texture[4];
        area = new Texture[4];

		batch = new SpriteBatch();

        map = new Texture[Map.getWidth()][Map.getHeight()];
        altars = new int[Map.getWidth()][Map.getHeight()];

		for(int i = 0; i < floor.length; i++)
            floor[i] = new Texture("dirt/grey_dirt"+(i)+".png");
        for(int i = 0; i < wall.length; i++)
            wall[i] = new Texture("stone_brick/stone_brick"+i+".png");
        for(int i = 0; i < altar.length; i++)
            altar[i] = new Texture("altar/dngn_altar_makhleb_flame"+i+".png");
        for(int i = 0; i < player.length; i++)
            player[i] = new Texture("player/"+i+".png");
        for(int i = 0; i < arrow.length; i++)
            arrow[i] = new Texture("arrow/"+i+".png");
        for(int i = 0; i < area.length; i++)
            area[i] = new Texture("area/cloud_magic_trail"+i+".png");

        cursor = new Texture("player/halo.png");

        for(int i = 0; i < Map.getWidth(); i++)
            for (int j = 0; j < Map.getHeight(); j++)
                if(Map.getCell(i,j) == 1) {
                    if(!Utils.hasNeighbours(i,j)) {
                        map[i][j] = floor[rand(floor.length)];
                        altars[i][j] = rand(altar.length);
                    }
                    else {
                        map[i][j] = wall[rand(wall.length)];
                        altars[i][j] = -1;
                    }
                }
                else {
                    map[i][j] = floor[rand(floor.length)];
                    altars[i][j] = -1;
                }

        font = new BitmapFont();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera(width,height);

        GameMap.addPlayer(new Player(false));
        for(int i = 0; i < 2; i++)
          GameMap.addPlayer(new Player(true));


    }

	@Override
	public void render () {

        getInput();

        time += Gdx.graphics.getDeltaTime();
        if (time >= 0.1) {
            //fpsLogger.log();
            time -= 0.1;
            if(turn) {
            GameMap.updateState();
            for (int i = 0; i < Map.getWidth(); i++)
                for (int j = 0; j < Map.getHeight(); j++)
                    if (altars[i][j] != -1)
                        altars[i][j] = (altars[i][j] + 1) % altar.length;
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
            for(int i = 0; i < Map.getWidth(); i++)
                for (int j = 0; j < Map.getHeight(); j++) {
                    batch.draw(map[i][j], i * 32, (Map.getHeight() - 1 - j) * 32);
                    if(altars[i][j] != -1)
                        batch.draw(altar[altars[i][j]],i * 32, (Map.getHeight() - 1 - j) * 32);
                }

            for(Player p : Map.getPlayers()) {

                batch.draw(player[p.getID()], p.getX() * 32, trueHeight() - (p.getY() * 32));
                if(!p.isBot()) {
                    batch.draw(cursor, p.getX() * 32, trueHeight() - (p.getY() * 32));
                }
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.rect(p.getX() * 32, trueHeight() - ((p.getY() - 1) * 32), 32, 4);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect( p.getX() * 32 +1, trueHeight() - 1 - ((p.getY()-1) * 32),31,3);
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect( p.getX() * 32 +1, trueHeight() - 1 - ((p.getY()-1) * 32),(int)(31*p.getHP()),3);



            }

            for(Attack a : GameMap.getAttacks())
                if(a.getDirection() != -1)
                    batch.draw(arrow[a.getDirection()],a.getX()*32, trueHeight() -(a.getY()*32));
                else
                    batch.draw(area[a.getTime(area.length)],a.getX()*32, trueHeight() -(a.getY()*32));
		batch.end();
        shapeRenderer.end();

	}

    private int trueHeight() {
        return ((Map.getHeight()-1)*32);
    }

    private void getInput() {
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

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
    }

    public static int rand(int j) {
        return (int)(Math.random()*j);
    }


}
