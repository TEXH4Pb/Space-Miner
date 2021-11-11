package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		config.title = "Space Miner";
		config.width = screen.width;
		config.height = screen.height;
		config.fullscreen = true;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
