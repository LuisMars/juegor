package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

/**
 * Created by Luis on 22/11/2014.
 */
public class AssetManager {


    public static Sound arrowSound = Gdx.audio.newSound(Gdx.files.internal("swish_2.wav"));
    public static Sound areaSound = Gdx.audio.newSound(Gdx.files.internal("area.wav"));

    CellList floor = new CellList();
    ArrayList<String> s = new ArrayList<String>();
    AtlasRegion[] player;
    AtlasRegion[] arrow;
    AtlasRegion area;
    AtlasRegion altar;

    TextureAtlas atlas;

    public AssetManager() {

        atlas = new TextureAtlas("dungeon.atlas");
        loadFloorNames();

        for (String value : s)
            floor.add(new Cell(atlas.findRegion(value), value.substring(0, 4), value.substring(4)));


        altar = atlas.findRegion("altar");
        player = new AtlasRegion[2];
        for(int i = 0; i < 2; i++)
            player[i] = atlas.findRegion("player_red",i);
        arrow = new AtlasRegion[4];
        for(int i = 0; i < 4; i++)
            arrow[i] = atlas.findRegion("arrow",i);
        area = atlas.findRegion("area");

    }

    private void loadFloorNames() {
        //wood bridges
        s.add("01010000");
        s.add("10100000");

        //"centers"

        s.add("00000001");
        s.add("00000010");
        s.add("00000100");
        s.add("00001000");

        s.add("00001100");
        s.add("00000110");
        s.add("00000011");
        s.add("00001001");

        s.add("00001010");
        s.add("00000101");
        s.add("00000111");
        s.add("00001110");

        s.add("0000xxxx"); //center

        //normal
        s.add("1001xx0x"); //UL corner

        s.add("100010xx");
        s.add("100001xx"); //U Wall
        s.add("1000xxxx");

        s.add("1100xxx0"); //UR corner

        s.add("00011xx0");
        s.add("00010xx1"); //L wall
        s.add("0001xxxx");


        s.add("0100x01x");
        s.add("0100x10x"); //R wall
        s.add("0100xxxx");

        s.add("0011x0xx"); //DL corner

        s.add("0010xx10");
        s.add("0010xx01"); //D wall
        s.add("0010xxxx");

        s.add("01100xxx"); //DR corner
        //corridors
        s.add("1001xx1x");
        s.add("1010xxxx");
        s.add("1100xxx1");
        s.add("0101xxxx");
        s.add("11111111");
        s.add("0011x1xx");
        s.add("01101xxx");
        s.add("1101xxxx");
        s.add("0111xxxx");
        s.add("1011xxxx");
        s.add("1110xxxx");
        s.add("1111xxxx");

    }

}
