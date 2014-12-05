package es.upv.luimafus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.ArrayList;

/**
 * Created by Luis on 22/11/2014.
 */
public class AssetManager {


    public static Sound arrowSound = Gdx.audio.newSound(Gdx.files.internal("sounds/arrow2.wav"));
    public static Sound areaSound = Gdx.audio.newSound(Gdx.files.internal("sounds/area.wav"));
    public static Sound[] stepSound = { Gdx.audio.newSound(Gdx.files.internal("sounds/steps01.wav")),
                                        Gdx.audio.newSound(Gdx.files.internal("sounds/steps02.wav")),
                                        Gdx.audio.newSound(Gdx.files.internal("sounds/steps03.wav")),
                                        Gdx.audio.newSound(Gdx.files.internal("sounds/steps04.wav"))};
    public static Music ambient = Gdx.audio.newMusic(Gdx.files.internal("sounds/ambient.wav"));

    CellList floor = new CellList();
    ArrayList<String> s = new ArrayList<String>();
    AtlasRegion[][] player;
    AtlasRegion[] arrow;
    AtlasRegion[] area;

    TextureAtlas floorAtlas;
    TextureAtlas playerAtlas;
    TextureAtlas arrowAtlas;
    TextureAtlas areaAtlas;

    BitmapFont font;

    public AssetManager() {

        font = new BitmapFont(Gdx.files.internal("ui/goldbox.fnt"));
        font.setScale(0.25f);
        floorAtlas = new TextureAtlas("tiles/dungeon.atlas");
        loadFloorNames();
        for (String value : s)
            floor.add(new Cell(floorAtlas.findRegion(value), value.substring(0, 4), value.substring(4)));


        player = new AtlasRegion[5][4];
        for(int i = 0; i < 5; i++) {
            playerAtlas = new TextureAtlas("players/"+i+".atlas");
            for (int j = 0; j < player[0].length; j++)
                player[i][j] = playerAtlas.findRegion("" + j);
        }

        arrowAtlas = new TextureAtlas("attacks/arrow.atlas");
        arrow = new AtlasRegion[4];
        for(int i = 0; i < arrow.length; i++)
            arrow[i] = arrowAtlas.findRegion(""+i);



        areaAtlas = new TextureAtlas("attacks/area.atlas");
        area = new AtlasRegion[4];
        for(int i = 0; i < area.length; i++)
            area[i] = areaAtlas.findRegion("0",i);

    }

    private void loadFloorNames() {

        s.add("11111111");
        s.add("00000000");
        s.add("00000010");
        s.add("00000011");
        s.add("00000001");
        s.add("00000110");
        s.add("00001111");
        s.add("00001001");
        s.add("00000100");
        s.add("00001100");
        s.add("00001000");

        s.add("00000111");
        s.add("00001011");
        s.add("00001101");
        s.add("00001110");

        s.add("00000101");
        s.add("00001010");

        s.add("1000xx00");
        s.add("0001x00x");
        s.add("01000xx0");
        s.add("001000xx");
        s.add("1000xx10");
        s.add("1000xx11");
        s.add("1000xx01");
        s.add("0001x01x");
        s.add("01000xx1");
        s.add("0001x11x");
        s.add("01001xx1");
        s.add("0001x10x");
        s.add("01001xx0");
        s.add("001001xx");
        s.add("001011xx");
        s.add("001010xx");

        s.add("1001xx0x");
        s.add("1100xxx0");
        s.add("0011x0xx");
        s.add("01100xxx");
        s.add("1001xx1x");
        s.add("1100xxx1");
        s.add("0011x1xx");
        s.add("01101xxx");

        s.add("1010xxxx");
        s.add("0101xxxx");
        s.add("1101xxxx");
        s.add("0111xxxx");
        s.add("1111xxxx");
        s.add("1011xxxx");
        s.add("1110xxxx");
        s.add("topA");
        s.add("topB");

    }

}
