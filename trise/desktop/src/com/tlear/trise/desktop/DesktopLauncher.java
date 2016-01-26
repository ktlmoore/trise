package com.tlear.trise.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tlear.trise.TRISE;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "T.R.I.S.E";
		config.width = 1080;
		config.height = 920;
		new LwjglApplication(new TRISE(), config);
	}
}
