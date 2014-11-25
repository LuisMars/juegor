package es.upv.luimafus.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import es.upv.luimafus.JuegoRedes;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.useGL30 = false;
        config.width = 640;
        config.height = 640;
		new LwjglApplication(new JuegoRedes(), config);
	}
}
