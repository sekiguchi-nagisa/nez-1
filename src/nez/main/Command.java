package nez.main;

import java.io.IOException;

import nez.util.ConsoleUtils;

public abstract class Command {
	public final static boolean ReleasePreview = true;
	public final static String ProgName = "Nez";
	public final static String CodeName = "yokohama";
	public final static int MajorVersion = 0;
	public final static int MinerVersion = 9;
	public final static int PatchLevel = Revision.REV;
	public final static String Version = "" + MajorVersion + "." + MinerVersion + "_" + PatchLevel;
	public final static String Copyright = "Copyright (c) 2014-2015, Nez project authors";
	public final static String License = "BSD-License Open Source";

	public final static void main(String[] args) {
		try {
			CommandContext c = new CommandContext();
			c.parseCommandOption(args);
			Command com = c.newCommand();
			com.exec(c);
		} catch (IOException e) {
			ConsoleUtils.println(e);
			System.exit(1);
		}
	}

	public abstract void exec(CommandContext config) throws IOException;

	public final static void displayVersion() {
		ConsoleUtils.println(ProgName + "-" + Version + " (" + CodeName + ") on Java JVM-" + System.getProperty("java.version"));
		ConsoleUtils.println(Copyright);
	}

}
