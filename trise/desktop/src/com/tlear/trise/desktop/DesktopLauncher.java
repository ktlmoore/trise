package com.tlear.trise.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tlear.trise.TRISE;

//
public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// config.width = 640;
		// config.height = 500;
		new LwjglApplication(new TRISE(), config);
	}
}
