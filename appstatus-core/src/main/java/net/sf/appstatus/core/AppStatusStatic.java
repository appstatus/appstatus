package net.sf.appstatus.core;

public class AppStatusStatic {
	static AppStatus instance = new AppStatus();

	public static AppStatus getInstance() {
		return instance;
	}
}
