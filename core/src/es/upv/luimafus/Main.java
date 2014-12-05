package es.upv.luimafus;

import com.badlogic.gdx.*;

public class Main extends Game {


    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        getScreen().render(Gdx.graphics.getDeltaTime());
    }
}
