package levels;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageUtils {

    private static final Random random = new Random();

    // 1. Create a new image with specific size and background color
    public static BufferedImage createBackground(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(ColorPalette.BACKGROUND);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    public static void drawWater(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int waterHeight = random.nextInt(3) + 1;

         Graphics2D g2d = img.createGraphics();
        g2d.setColor(ColorPalette.WATER);
        g2d.fillRect(0, h - waterHeight, w, waterHeight);
        g2d.dispose();
    }

    // 2. Draw a random rectangle (platform) on the image above
    public static void drawPlatformAbove(BufferedImage img, int topLeftX, int bottomRightX, List<Integer> islandAboveY) {
        int w = img.getWidth();
        int h = img.getHeight();

        int rectWidth = random.nextInt(Math.max(1, w / 2 - 2)) + 3; // Ensure rectWidth is at least 3
        int rectHeight = random.nextInt(Math.max(1, h / 3 - 1)); // Ensure rectHeight is at least 1 and < h/3
        int topLeftY = 0;
        int bottomRightY = topLeftY + rectHeight;

        bottomRightX = Math.min(bottomRightX, w);
        bottomRightY = Math.min(bottomRightY, h);

         Graphics2D g2d = img.createGraphics();
        g2d.setColor(ColorPalette.FLOOR);
        g2d.fillRect(topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY - topLeftY);
         g2d.setColor(ColorPalette.STONE);
        g2d.fillRect(topLeftX, bottomRightY, bottomRightX - topLeftX, 1); // just for the top of the stone
        g2d.dispose();


        islandAboveY.add(bottomRightY);
    }


   // 3. Draw a random rectangle (platform) on the image below
   public static void drawPlatformBelow(BufferedImage img, int topLeftX, int bottomRightX, List<Integer> islandBelowX, List<Integer> islandBelowY) {
       int w = img.getWidth();
       int h = img.getHeight();

       int rectWidth = random.nextInt(Math.max(1, w / 2 - 2)) + 3; // Ensure rectWidth is at least 3
       int rectHeight = random.nextInt(Math.max(1, h / 2 - 2)) + 3; // Ensure rectHeight is at least 3
       int topLeftY = h - rectHeight;
       int bottomRightY = topLeftY + rectHeight;

       bottomRightX = Math.min(bottomRightX, w);
       bottomRightY = Math.min(bottomRightY, h);

       Graphics2D g2d = img.createGraphics();
       g2d.setColor(ColorPalette.FLOOR);
       g2d.fillRect(topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY- topLeftY); // Draw floor
       g2d.setColor(ColorPalette.GRASS);
       g2d.fillRect(topLeftX, topLeftY, bottomRightX - topLeftX, 1); // Draw grass
       g2d.setColor(ColorPalette.LEFT_EDGE);
       g2d.fillRect(topLeftX, topLeftY, 1, bottomRightY - topLeftY); // Draw left edge
        g2d.setColor(ColorPalette.RIGHT_EDGE);
       g2d.fillRect(bottomRightX, topLeftY, 1, bottomRightY-topLeftY); // Draw right edge

       g2d.setColor(ColorPalette.LEFT_SQUARE);
        g2d.fillRect(topLeftX, topLeftY, 1, 1);
         g2d.setColor(ColorPalette.RIGHT_SQUARE);
        g2d.fillRect(bottomRightX, topLeftY, 1, 1);
       g2d.dispose();


        islandBelowX.add(topLeftX);
       islandBelowY.add(topLeftY);

   }

    public static void drawPlatforms(BufferedImage img, int width, int height, List<Integer> islandAboveY, List<Integer> islandBelowX, List<Integer> islandBelowY) {
        int split;
        if (40 <= width && width < 60) {
            split = width / 2;
        } else if (60 <= width && width < 80) {
            split = width / 3;
        } else {
            split = width / 4;
        }
         for (int x = 5; x < width - 5; x += split) {
            int randomOffset1 = (int) (Math.random() * 9) - 4;
            int randomOffset2 = (int) (Math.random() * 8) - 7;
            int randomOffset3 = (int) (Math.random() * 4);
            int randomOffset4 = (int) (Math.random() * 7) - 6;

            drawPlatformAbove(img, x + randomOffset1, x + split + randomOffset2, islandAboveY);
             drawPlatformBelow(img, x + randomOffset3, x + split + randomOffset4, islandBelowX, islandBelowY);
        }

    }


    // Helper function to check if a position is valid
     public static boolean isValidPos(BufferedImage img, int topLeftX, int topLeftY, Color color) {
         int width = img.getWidth();
          int height = img.getHeight();
          if (topLeftX + 2 >= width || topLeftY + 1 >= height) return false;

         Color[] colors = new Color[6];

          colors[0] = new Color(img.getRGB(topLeftX, topLeftY), true);
          colors[1] = new Color(img.getRGB(topLeftX+1, topLeftY), true);
         colors[2] = new Color(img.getRGB(topLeftX+2, topLeftY), true);
         colors[3] = new Color(img.getRGB(topLeftX, topLeftY+1), true);
          colors[4] = new Color(img.getRGB(topLeftX+1, topLeftY+1), true);
          colors[5] = new Color(img.getRGB(topLeftX+2, topLeftY+1), true);

         if (colors[0].equals(ColorPalette.BACKGROUND) &&
             colors[1].equals(ColorPalette.BACKGROUND) &&
             colors[2].equals(ColorPalette.BACKGROUND) &&
            colors[3].equals(ColorPalette.GRASS) &&
            colors[4].equals(ColorPalette.GRASS) &&
            colors[5].equals(ColorPalette.GRASS)) {
             img.setRGB(topLeftX, topLeftY, color.getRGB());
             return true;
           }
            else{
                return false;
            }
     }

   public static void spawnPlayer(BufferedImage img) {
         int w = img.getWidth();
        int h = img.getHeight();
        boolean boolSpawnPoint = false;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                boolSpawnPoint = isValidPos(img, x, y, ColorPalette.PLAYER);
                if(boolSpawnPoint){
                    break;
               }
            }
            if(boolSpawnPoint){
               break;
            }
       }
   }


  public static void spawnEnemy(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int chance = random.nextInt(101);
                if (chance <= 20) {
                     int monsterType = random.nextInt(101);
                    if (monsterType <= 30) {
                        isValidPos(img, x, y, ColorPalette.STAR_FISH);
                    } else if (monsterType <= 60) {
                        isValidPos(img, x, y, ColorPalette.SHARK);
                    } else {
                       isValidPos(img, x, y, ColorPalette.CRAB);
                   }
                 }
            }
         }
  }


    public static boolean posFurniture(BufferedImage img, int topLeftX, int topLeftY, Color color){
        int width = img.getWidth();
        int height = img.getHeight();
        if(topLeftX + 1 >= width || topLeftY + 1 >= height) return false;


      Color[] colors = new Color[4];
       colors[0] = new Color(img.getRGB(topLeftX, topLeftY), true);
       colors[1] = new Color(img.getRGB(topLeftX+1, topLeftY), true);
      colors[2] = new Color(img.getRGB(topLeftX, topLeftY+1), true);
       colors[3] = new Color(img.getRGB(topLeftX+1, topLeftY+1), true);

        if (colors[0].equals(ColorPalette.BACKGROUND) &&
             colors[1].equals(ColorPalette.BACKGROUND) &&
             colors[2].equals(ColorPalette.GRASS) &&
             colors[3].equals(ColorPalette.GRASS)){
            img.setRGB(topLeftX, topLeftY, color.getRGB());
          return true;
        }
        else {
            return false;
       }
  }


   public static boolean posStair(BufferedImage img, int topLeftX, int topLeftY){
        int width = img.getWidth();
       int height = img.getHeight();
        List<Color> colors = new ArrayList<>();
       int cnt = 0;

        for (int dy = 0; dy < 6; dy++) {
           for (int dx = 0; dx < 6; dx++) {
              int x = topLeftX + dx;
              int y = topLeftY + dy;
              if (x < width && y < height) {
                  colors.add(new Color(img.getRGB(x, y), true));
                 if (new Color(img.getRGB(x,y), true).equals(ColorPalette.BACKGROUND)) {
                       cnt++;
                  }
              } else {
                 colors.add(null);
               }
            }
        }

        boolean validHeight = false;
      for (int i = 0; i < 3; i++) {
            int x = topLeftX + 4 + i;
            int y = topLeftY;
           if (x < width && y < height) {
                if (new Color(img.getRGB(x, y), true).equals(ColorPalette.GRASS) ||
                     new Color(img.getRGB(x, y+2), true).equals(ColorPalette.GRASS)) {
                   validHeight = true;
                   break;
                }
           }
       }
      if(cnt == 36){
          int chance = random.nextInt(101);
           int length = random.nextInt(3) + 3;
           int enemyChance = random.nextInt(101);
            int boxChance = random.nextInt(101);
           int treeChance = random.nextInt(101);
           int tPos = random.nextInt(length -1) + 1;
          int x = topLeftX;
           int y = topLeftY;
           Color stairColor1 = new Color(39, 145, 154);
           Color stairColor2 = new Color(36, 122, 154);
           Color stairColor3 = new Color(38, 122, 154);
            Color stairColor4 = new Color(37, 122, 154);
           Color stairColor5 = new Color(36, 240, 154);
          Color stairColor6 = new Color(3, 122, 154);
          Color stairColor7 = new Color(27, 122, 154);


           if (chance < 25) {
               // 1
               img.setRGB(x+1, y+4, stairColor1.getRGB());
               img.setRGB(x+3, y+2, stairColor1.getRGB());

               for (int i = 0; i < length; i++) {
                 img.setRGB(x+5+i, y+1, stairColor4.getRGB());
                }
               img.setRGB(x+5, y+1, stairColor5.getRGB());
                img.setRGB(x+5+length, y+1, stairColor3.getRGB());

                if (enemyChance <= 30) {
                    int monsterType = random.nextInt(101);
                    Color monsterColor;
                    if (monsterType <= 20) {
                        monsterColor = ColorPalette.STAR_FISH;
                    } else if (monsterType <= 60) {
                        monsterColor = ColorPalette.SHARK;
                    } else {
                        monsterColor = ColorPalette.CRAB;
                    }
                    img.setRGB(x + 5 + tPos, y, monsterColor.getRGB());
               }
                if(boxChance <= 30){
                    img.setRGB(x+5+tPos+1, y, ColorPalette.BOX.getRGB());
                }
               if(treeChance <= 30){
                   img.setRGB(x+5+tPos-1, y, ColorPalette.TREE.getRGB());
                }

           } else if (chance < 50) {
                // 2
               img.setRGB(x, y+4, stairColor2.getRGB());
                img.setRGB(x+1, y+4, stairColor3.getRGB());

                img.setRGB(x+2, y+2, stairColor2.getRGB());
                img.setRGB(x+3, y+2, stairColor3.getRGB());

               for (int i = 0; i < length; i++) {
                  img.setRGB(x+5+i, y+1, stairColor4.getRGB());
                }
                img.setRGB(x+5, y+1, stairColor5.getRGB());
               img.setRGB(x+5+length, y+1, stairColor3.getRGB());

                if (enemyChance <= 30) {
                    int monsterType = random.nextInt(101);
                    Color monsterColor;
                  if (monsterType <= 20) {
                       monsterColor = ColorPalette.STAR_FISH;
                   } else if (monsterType <= 60) {
                        monsterColor = ColorPalette.SHARK;
                   } else {
                      monsterColor = ColorPalette.CRAB;
                  }
                    img.setRGB(x + 5 + tPos, y, monsterColor.getRGB());
               }
               if(boxChance <= 30){
                    img.setRGB(x+5+tPos+1, y, ColorPalette.BOX.getRGB());
                }
               if(treeChance <= 30){
                   img.setRGB(x+5+tPos-1, y, ColorPalette.TREE.getRGB());
                }
            } else if (chance < 75) {
               // 3
               img.setRGB(x+1, y+4, stairColor6.getRGB());
               img.setRGB(x+1, y+5, stairColor7.getRGB());

                img.setRGB(x+3, y+2, stairColor6.getRGB());
                img.setRGB(x+3, y+3, stairColor7.getRGB());

               for (int i = 0; i < length; i++) {
                   img.setRGB(x+5+i, y+1, stairColor4.getRGB());
                 }
                img.setRGB(x+5, y+1, stairColor5.getRGB());
               img.setRGB(x+5+length, y+1, stairColor3.getRGB());

                if (enemyChance <= 30) {
                    int monsterType = random.nextInt(101);
                    Color monsterColor;
                    if (monsterType <= 20) {
                       monsterColor = ColorPalette.STAR_FISH;
                    } else if (monsterType <= 60) {
                       monsterColor = ColorPalette.SHARK;
                    } else {
                       monsterColor = ColorPalette.CRAB;
                   }
                    img.setRGB(x + 5 + tPos, y, monsterColor.getRGB());
                }
                if(boxChance <= 30){
                  img.setRGB(x+5+tPos+1, y, ColorPalette.BOX.getRGB());
               }
                if(treeChance <= 30){
                   img.setRGB(x+5+tPos-1, y, ColorPalette.TREE.getRGB());
               }
            } else {
                // 4
               img.setRGB(x, y+4, stairColor2.getRGB());
                img.setRGB(x+1, y+4, stairColor6.getRGB());
                img.setRGB(x+1, y+5, stairColor7.getRGB());

               img.setRGB(x+2, y+2, stairColor2.getRGB());
                img.setRGB(x+3, y+2, stairColor6.getRGB());
               img.setRGB(x+3, y+3, stairColor7.getRGB());

                for (int i = 0; i < length; i++) {
                    img.setRGB(x+5+i, y+1, stairColor4.getRGB());
                 }
                img.setRGB(x+5, y+1, stairColor5.getRGB());
                img.setRGB(x+5+length, y+1, stairColor3.getRGB());

                 if (enemyChance <= 30) {
                    int monsterType = random.nextInt(101);
                     Color monsterColor;
                     if (monsterType <= 20) {
                       monsterColor = ColorPalette.STAR_FISH;
                   } else if (monsterType <= 60) {
                       monsterColor = ColorPalette.SHARK;
                    } else {
                      monsterColor = ColorPalette.CRAB;
                   }
                    img.setRGB(x + 5 + tPos, y, monsterColor.getRGB());
                }
                if(boxChance <= 30){
                   img.setRGB(x+5+tPos+1, y, ColorPalette.BOX.getRGB());
                }
                if(treeChance <= 30){
                    img.setRGB(x+5+tPos-1, y, ColorPalette.TREE.getRGB());
                }
            }
          return true;
       }
       else {
           return false;
       }
   }

   public static boolean posHole(BufferedImage img, int topLeftX, int topLeftY){
       int width = img.getWidth();
       int height = img.getHeight();
       List<int[]> positions = new ArrayList<>();
       int cnt = 0;

       for(int dy = 0; dy < 3; dy++) {
           for (int dx = 0; dx < 2; dx++) {
                int x = topLeftX + dx;
                int y = topLeftY + dy;

               if (x < width && y < height) {
                   positions.add(new int[]{x,y});
                    if (!new Color(img.getRGB(x, y), true).equals(ColorPalette.BACKGROUND)) {
                        cnt++;
                   }
               }
           }
        }
       if(cnt >= 4){
            for(int[] pos : positions){
              img.setRGB(pos[0], pos[1], ColorPalette.BACKGROUND.getRGB());
           }
          img.setRGB(positions.get(positions.size() - 1)[0], positions.get(positions.size() -1)[1], ColorPalette.SPIKE.getRGB());
          img.setRGB(positions.get(positions.size() - 2)[0], positions.get(positions.size() -2)[1], ColorPalette.SPIKE.getRGB());

            for (int t = 0; t < 3; t++) {
              int x = positions.get(positions.size() - 1)[0];
                int y = positions.get(positions.size() - 1)[1];
                if (x + 1 < width && y - t >= 0) {
                    if (new Color(img.getRGB(x + 1, y - t), true).equals(ColorPalette.FLOOR)) {
                        img.setRGB(x + 1, y - t, ColorPalette.LEFT_EDGE.getRGB());
                    }
                    if (new Color(img.getRGB(x + 1, y - t), true).equals(ColorPalette.GRASS)) {
                      img.setRGB(x + 1, y - t, ColorPalette.LEFT_SQUARE.getRGB());
                    }
               }
                if(x - 2 >=0 && y - t >= 0){
                    if (new Color(img.getRGB(x - 2, y - t), true).equals(ColorPalette.FLOOR)) {
                       img.setRGB(x - 2, y - t, ColorPalette.RIGHT_EDGE.getRGB());
                    }
                    if (new Color(img.getRGB(x - 2, y - t), true).equals(ColorPalette.GRASS)) {
                       img.setRGB(x - 2, y - t, ColorPalette.RIGHT_SQUARE.getRGB());
                   }
                }
           }
            return true;
       } else {
          return false;
       }
    }

   public static void spawnHole(BufferedImage img, List<Integer> islandBelowX) {
        int w = img.getWidth();
        int h = img.getHeight();

       for (int x = 10; x < w - 5; x += 2) {
           if (!islandBelowX.contains(x + 1) &&
                 !islandBelowX.contains(x + 2) &&
                 !islandBelowX.contains(x + 3)) {
               int chance = random.nextInt(101);
                for (int y = 3; y < h - 4; y++) {
                  if(chance <= 10){
                      posHole(img, x,y);
                  }
              }
          }
      }
  }


   public static void spawnTree(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        for (int x = 0; x < w; x++) {
          for (int y = 0; y < h; y++) {
                int chance = random.nextInt(101);
               if (chance <= 20) {
                    posFurniture(img, x, y, ColorPalette.TREE);
                }
            }
       }
    }

   public static void spawnBoxAndCanon(BufferedImage img) {
      int w = img.getWidth();
       int h = img.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                 int chance = random.nextInt(101);
                if (chance <= 15) {
                    if (chance <= 3) {
                        if (chance % 2 == 0) {
                          posFurniture(img, x, y, ColorPalette.LEFT_CANON);
                        } else {
                            posFurniture(img, x, y, ColorPalette.RIGHT_CANON);
                       }
                  } else {
                        posFurniture(img, x, y, ColorPalette.BOX);
                    }
                }
            }
        }
    }


    public static void spawnStair(BufferedImage img, List<Integer> islandAboveY, List<Integer> islandBelowX, List<Integer> islandBelowY) {
         int w = img.getWidth();
        int h = img.getHeight();

       int minIslandBelowX = islandBelowX.stream().min(Integer::compareTo).orElse(0);
        int maxIslandAboveY = islandAboveY.stream().max(Integer::compareTo).orElse(0);
        int minIslandBelowY = islandBelowY.stream().min(Integer::compareTo).orElse(0);

      for (int x = minIslandBelowX + 5; x < w - 10; x++) {
          for (int y = maxIslandAboveY + 2; y < minIslandBelowY + 1; y++) {
                int chance = random.nextInt(101);
                if(chance <= 20){
                     posStair(img, x, y);
               }
           }
      }
   }
}