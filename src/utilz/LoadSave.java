package utilz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

public class LoadSave {

	public static final String SHEEP_BOUNCING = "HappySheep_Bouncing.png";
	public static final String SHEEP_IDLE = "HappySheep_Idle.png";

	public static final String LEVEL1  = "level_1.png";
	public static final String LEVEL2  = "level_2.png";
	public static final String LEVEL3  = "level_3.png";
	public static final String LEVEL4  = "level_4.png";
	public static final String LEVEL5  = "level_5.png";
	public static final String LEVEL6  = "level_6.png";
	public static final String LEVEL7  = "level_7.png";
	public static final String LEVEL8  = "level_8.png";
	public static final String LEVEL9  = "level_9.png";
	public static final String LEVEL10 = "level_10.png";
	public static final String LEVEL11 = "level_11.png";
	public static final String LEVEL12 = "level_12.png";
	public static final String LEVEL13 = "level_13.png";
	public static final String LEVEL14 = "level_14.png";
	public static final String LEVEL15 = "level_15.png";

	public static final String PLAYER_ATLAS = "player_sprites.png";
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String MENU_BACKGROUND = "menu_background.png";
	public static final String PAUSE_BACKGROUND = "pause_menu.png";
	public static final String SOUND_BUTTONS = "sound_button.png";
	public static final String URM_BUTTONS = "urm_buttons.png";
	public static final String VOLUME_BUTTONS = "volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG = "background_menu.png";
	public static final String PLAYING_BG_IMG = "playing_bg_img.png";
	public static final String PLAYING_BG_IMG2 = "playing_bg_img_2.png";
	public static final String PLAYING_BG_IMG3 = "playing_bg_img_3.png";
	public static final String BIG_CLOUDS = "big_clouds.png";
	public static final String SMALL_CLOUDS = "small_clouds.png";
	public static final String CRABBY_SPRITE = "crabby_sprite.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";
	public static final String POTION_ATLAS = "potions_sprites.png";
	public static final String CONTAINER_ATLAS = "objects_sprites.png";
	public static final String TRAP_ATLAS = "trap_atlas.png";
	public static final String CANNON_ATLAS = "cannon_atlas.png";
	public static final String CANNON_BALL = "ball.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTIONS_MENU = "options_background.png";
	public static final String PINKSTAR_ATLAS = "pinkstar_atlas.png";
	public static final String QUESTION_ATLAS = "question_atlas.png";
	public static final String EXCLAMATION_ATLAS = "exclamation_atlas.png";
	public static final String SHARK_ATLAS = "shark_atlas.png";
	public static final String CREDITS = "credits_list.png";
	public static final String GRASS_ATLAS = "grass_atlas.png";
	public static final String TREE_ONE_ATLAS = "tree_one_atlas.png";
	public static final String TREE_TWO_ATLAS = "tree_two_atlas.png";
	public static final String GAME_COMPLETED = "game_completed.png";
	public static final String RAIN_PARTICLE = "rain_particle.png";
	public static final String WATER_TOP = "water_atlas_animation.png";
	public static final String WATER_BOTTOM = "water.png";
	public static final String SHIP = "ship.png";


	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;

		// 1) Try classpath root
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
		// 2) Try under /res on classpath
		if (is == null) {
			is = LoadSave.class.getResourceAsStream("/res/" + fileName);
		}

		if (is != null) {
			try {
				img = ImageIO.read(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
			return img;
		}

		// 3) Fallback to filesystem ./res directory (useful during development when res/ isn't on classpath)
		File f = new File("res", fileName);
		if (f.exists()) {
			try {
				img = ImageIO.read(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return img;
		}

		throw new RuntimeException("Resource not found: " + fileName + " (looked in classpath /, /res and filesystem ./res)");
	}


	public static BufferedImage[] GetAllLevels() {
		// Try to locate the levels directory via classpath first
		URL url = LoadSave.class.getResource("/lvls");
		if (url == null) {
			url = LoadSave.class.getResource("/res/lvls");
		}

		File dir = null;
		if (url != null) {
			try {
				dir = new File(url.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		// Fallback to filesystem path
		if (dir == null) {
			dir = new File("res/lvls");
		}

		File[] files = (dir != null) ? dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png")) : null;
		if (files == null || files.length == 0) {
			return new BufferedImage[0];
		}

		File[] filesSorted = new File[files.length];
		for (int i = 0; i < filesSorted.length; i++) {
			for (File value : files) {
				if (value.getName().equals((i + 1) + ".png")) {
					filesSorted[i] = value;
					break;
				}
			}
		}

		BufferedImage[] imgs = new BufferedImage[filesSorted.length];
		for (int i = 0; i < imgs.length; i++) {
			try {
				imgs[i] = ImageIO.read(filesSorted[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return imgs;
	}

}