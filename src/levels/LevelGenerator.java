package levels;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.file.Path;
import java.nio.file.Paths;


public class LevelGenerator {

    public static void generateLevels(int numberOfLevels, String outputDirectory) {

        Random random = new Random();
        for (int i = 1; i <= numberOfLevels; i++) {
            int width = 60;
            int height = 14;
             List<Integer> islandAboveY = new ArrayList<>();
            List<Integer> islandBelowX = new ArrayList<>();
            List<Integer> islandBelowY = new ArrayList<>();
            BufferedImage img = ImageUtils.createBackground(width, height);
            ImageUtils.drawWater(img);
            ImageUtils.drawPlatforms(img, width, height, islandAboveY, islandBelowX, islandBelowY);
            ImageUtils.spawnHole(img, islandBelowX);
            ImageUtils.spawnPlayer(img);
            ImageUtils.spawnEnemy(img);
            ImageUtils.spawnBoxAndCanon(img);
            ImageUtils.spawnTree(img);
            ImageUtils.spawnStair(img, islandAboveY, islandBelowX, islandBelowY);
            saveImage(img, outputDirectory, i);
        }
    }

    private static void saveImage(BufferedImage img, String outputDirectory, int filename) {
        try {
            Path path = Paths.get(outputDirectory, (filename+5) + ".png");
            File outputFile = path.toFile();
            outputFile.getParentFile().mkdirs(); // Create parent directories if they don't exist
            ImageIO.write(img, "png", outputFile);
            System.out.println("Saved level image to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }


}