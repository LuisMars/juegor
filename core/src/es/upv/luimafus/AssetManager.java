package es.upv.luimafus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Luis on 22/11/2014.
 */
public class AssetManager {

    Texture[] floor;
    Texture[] wall;
    Texture[] player;
    Texture[] arrow;
    Texture[] area;
    Texture[] altar;

    int[][] altars;

    Texture[][] map;

    TextureAtlas atlas;
    Skin skin;
    Map GameMap;
    public AssetManager(Map GameMap) {

        atlas = new TextureAtlas("dungeon.atlas");

        this.GameMap = GameMap;
        floor = new Texture[10];
        wall = new Texture[10];

        altar = new Texture[8];

        player = new Texture[8];

        arrow = new Texture[4];
        area = new Texture[4];


        map = new Texture[GameMap.getWidth()][GameMap.getHeight()];
        altars = new int[GameMap.getWidth()][GameMap.getHeight()];

        for(int i = 0; i < floor.length; i++)
            floor[i] = atlas.findRegion("floor",-1).getTexture();//new Texture("sandstone/sandstone_floor"+(i)+".png");
        for(int i = 0; i < wall.length; i++)
            wall[i] = atlas.findRegion("MC",-1).getTexture();//new Texture("sandstone_wall/sandstone_wall"+i+".png");
        for(int i = 0; i < altar.length; i++)
            altar[i] = new Texture("altar/dngn_altar_makhleb_flame"+i+".png");
        for(int i = 0; i < player.length; i++)
            player[i] = new Texture("player/"+i+".png");
        for(int i = 0; i < arrow.length; i++)
            arrow[i] = new Texture("arrow/"+i+".png");
        for(int i = 0; i < area.length; i++)
            area[i] = new Texture("area/cloud_magic_trail"+i+".png");


        populateMap();


    }

    private void populateMap() {
        for(int i = 0; i < GameMap.getWidth(); i++)
            for (int j = 0; j < GameMap.getHeight(); j++)
                if(GameMap.getCell(i,j) == 1) {
                    if(!Utils.hasNeighbours(GameMap, i, j)) {
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
    }

    public static int rand(int j) {
        return (int)(Math.random()*j);
    }
}
