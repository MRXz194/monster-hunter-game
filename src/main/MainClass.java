package main;
import levels.*;
import java.nio.file.Paths;


public class MainClass {

	public static void main(String[] args) {
		String outputDir = Paths.get("res","lvls").toString();
		LevelGenerator.generateLevels(10, outputDir);
		new Game();
	}

}

