package es.upv.luimafus;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Luis on 23/11/2014.
 */
public class Cell {
    TextureRegion r;
    String a;
    String b;

    public Cell(TextureRegion r, String a, String b) {
        this.r = r;
        this.a = a;
        this.b = b;
    }

    public boolean canBe(String B) {
        for (int i = 0; i < b.length(); i++)
            if (b.charAt(i) != B.charAt(i) && b.charAt(i) != 'x')
                return false;
        return true;
    }

}
