package es.upv.luimafus;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.ArrayList;

public class CellList extends ArrayList<Cell> {

    public AtlasRegion getTile(String s) {
        String a = s.substring(0,4);
        String b = s.substring(4);

        for(Cell c: this)
            if (c.a.contains(a))
                if (b.contains(c.b) ||c.canBe(b))
                    return c.r;
        return null;
    }

    public AtlasRegion getTile(int i) {
        return get(i).r;
    }

    public int getIndex(String s) {
        String a = s.substring(0,4);
        String b = s.substring(4);

        for(Cell c: this)
            if (c.a.equals(a) && c.canBe(b))
                    return indexOf(c);
        return 13;
    }
}
