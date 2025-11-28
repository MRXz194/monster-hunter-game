package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

import entities.EnemyManager;
import entities.FlyingSheep;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameCompletedOverlay;
import ui.GameOverOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;
import effects.DialogueEffect;
import effects.Rain;
import entities.Enemy;

import levels.Level;

import static utilz.Constants.Environment.*;
import static utilz.Constants.Dialogue.*;

public class Playing extends State implements Statemethods {
    // Sheep
    private FlyingSheep sheep;
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private GameCompletedOverlay gameCompletedOverlay;
    // Remove LevelCompletedOverlay
    private Rain rain;

    private boolean paused = false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.40 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.60 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;

    private BufferedImage backgroundImg, backgroundImg2, backgroundImg3, bigCloud, smallCloud, shipImgs[];
    private BufferedImage[] questionImgs, exclamationImgs;
    private ArrayList<DialogueEffect> dialogEffects = new ArrayList<>();

    private int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver;
    // Remove lvlCompleted boolean
    private boolean gameCompleted;
    private boolean playerDying;

   private BufferedImage[] levelStartImage;
    private boolean showLevelStartImage = false;
    private int levelStartImageTimer = 0;
    private final int levelStartImageDuration = 5 * 60;  // 5 seconds at 60 FPS
    private boolean firstLevel = false;
    private int levelStartImageIndex; // Track the current level start image.


    private boolean drawRain;
    private int levelWithSheep = 4;
    private int lvlbackground1 = 4;
    private int lvlbackground2 = 9;
    private int lvlbackground3 = 14;

    // Ship will be decided to drawn here. It's just a cool addition to the game
    // for the first level. Hinting on that the player arrived with the boat.

    // If you would like to have it on more levels, add a value for objects when
    // creating the level from lvlImgs. Just like any other object.

    // Then play around with position values so it looks correct depending on where
    // you want
    // it.

    private boolean drawShip = true;
    private int shipAni, shipTick, shipDir = 1;
    private float shipHeightDelta, shipHeightChange = 0.05f * Game.SCALE;


    //Fade effect variables
    private boolean levelTransitioning = false;
    private float fadeAlpha = 0f;
    private final float fadeSpeed = 0.02f;


    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        backgroundImg2 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG2);
        backgroundImg3 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG3);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
         loadLevelStartImages(); // Load all the level images.


        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));

        shipImgs = new BufferedImage[4];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SHIP);
        for (int i = 0; i < shipImgs.length; i++)
            shipImgs[i] = temp.getSubimage(i * 78, 0, 78, 72);

        loadDialogue();
        calcLvlOffset();
        loadStartLevel();
        setDrawRainBoolean();
    }
     private void loadLevelStartImages(){
        levelStartImage = new BufferedImage[15];
        levelStartImage[0] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL1);
        levelStartImage[1] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL2);
        levelStartImage[2] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL3);
         levelStartImage[3] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL4);
         levelStartImage[4] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL5);
         levelStartImage[5] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL6);
         levelStartImage[6] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL7);
         levelStartImage[7] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL8);
         levelStartImage[8] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL9);
         levelStartImage[9] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL10);
         levelStartImage[10] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL11);
         levelStartImage[11] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL12);
         levelStartImage[12] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL13);
         levelStartImage[13] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL14);
         levelStartImage[14] = LoadSave.GetSpriteAtlas(LoadSave.LEVEL15);
    }

    private void loadDialogue() {
        loadDialogueImgs();

        // Load dialogue array with premade objects, that gets activated when needed.
        // This is a simple
        // way of avoiding ConcurrentModificationException error. (Adding to a list that
        // is being looped through.

        for (int i = 0; i < 10; i++)
            dialogEffects.add(new DialogueEffect(0, 0, EXCLAMATION));
        for (int i = 0; i < 10; i++)
            dialogEffects.add(new DialogueEffect(0, 0, QUESTION));

        for (DialogueEffect de : dialogEffects)
            de.deactive();
    }

    private void loadDialogueImgs() {
        BufferedImage qtemp = LoadSave.GetSpriteAtlas(LoadSave.QUESTION_ATLAS);
        questionImgs = new BufferedImage[5];
        for (int i = 0; i < questionImgs.length; i++)
            questionImgs[i] = qtemp.getSubimage(i * 14, 0, 14, 12);

        BufferedImage etemp = LoadSave.GetSpriteAtlas(LoadSave.EXCLAMATION_ATLAS);
        exclamationImgs = new BufferedImage[5];
        for (int i = 0; i < exclamationImgs.length; i++)
            exclamationImgs[i] = etemp.getSubimage(i * 14, 0, 14, 12);
    }

    public void loadNextLevel() {
        levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        resetAll();
        drawShip = false;
		if(levelManager.getLevelIndex() < levelStartImage.length && !firstLevel){
			showLevelStartImage = true; // only show on level 1 start
            levelStartImageIndex = levelManager.getLevelIndex();
			firstLevel = true;
		}

        // Handle Sheep
        if (levelManager.getLevelIndex() >= levelWithSheep) {
			createFlyingSheep();
        } else {
			sheep = null; // If Sheep should not exist for this level it is null.
		}
    }
	private void createFlyingSheep(){
		 if (sheep == null) {
			sheep = new FlyingSheep(500, 300, 128, 128); // Initial spawn near player
		}
	}

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel());
		if (levelManager.getLevelIndex() < levelStartImage.length && !firstLevel){
			showLevelStartImage = true;
            levelStartImageIndex = levelManager.getLevelIndex();
			firstLevel = true;
		}
        //handle sheep on start

        if (levelManager.getLevelIndex() >= levelWithSheep) {
			createFlyingSheep();
        }else{
			sheep = null;
		}
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        player = new Player(200, 150, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        gameCompletedOverlay = new GameCompletedOverlay(this);
        // Removed LevelCompletedOverlay

        rain = new Rain();
    }

    @Override
    public void update() {
        if (paused)
            pauseOverlay.update();
        else if (gameCompleted)
            gameCompletedOverlay.update();
        else if (gameOver)
            gameOverOverlay.update();
        else if (playerDying)
            player.update();
        else if (levelTransitioning) {
            updateLevelTransition(); // Update fade effect
        }
		else {
            updateDialogue();
            if (drawRain)
                rain.update(xLvlOffset);
            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData());
            checkCloseToBorder();
            if (drawShip)
                updateShipAni();
            // Update Sheep only when it is initialized.
            if (sheep != null) {
                sheep.update(player, getAllActiveEnemies());
            }
			if (showLevelStartImage) { // Timer for level start image
				levelStartImageTimer++;
				if (levelStartImageTimer >= levelStartImageDuration) {
					showLevelStartImage = false;
					levelStartImageTimer = 0;
				}
			}
        }

    }
    
    private void updateLevelTransition() {
        if (fadeAlpha < 1f) {
            fadeAlpha += fadeSpeed;
        } else {
            loadNextLevel();
            fadeAlpha = 1f;
           // levelTransitioning = false;
            levelTransitioning = false;
        }
    }


    private void updateShipAni() {
        shipTick++;
        if (shipTick >= 35) {
            shipTick = 0;
            shipAni++;
            if (shipAni >= 4)
                shipAni = 0;
        }

        shipHeightDelta += shipHeightChange * shipDir;
        shipHeightDelta = Math.max(Math.min(10 * Game.SCALE, shipHeightDelta), 0);

        if (shipHeightDelta == 0)
            shipDir = 1;
        else if (shipHeightDelta == 10 * Game.SCALE)
            shipDir = -1;

    }

    private void updateDialogue() {
        for (DialogueEffect de : dialogEffects)
            if (de.isActive())
                de.update();
    }

    private void drawDialogue(Graphics g, int xLvlOffset) {
        for (DialogueEffect de : dialogEffects)
            if (de.isActive()) {
                if (de.getType() == QUESTION)
                    g.drawImage(questionImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
                else
                    g.drawImage(exclamationImgs[de.getAniIndex()], de.getX() - xLvlOffset, de.getY(), DIALOGUE_WIDTH, DIALOGUE_HEIGHT, null);
            }
    }

    public void addDialogue(int x, int y, int type) {
        // Not adding a new one, we are recycling. #ThinkGreen lol
        dialogEffects.add(new DialogueEffect(x, y - (int) (Game.SCALE * 15), type));
        for (DialogueEffect de : dialogEffects)
            if (!de.isActive())
                if (de.getType() == type) {
                    de.reset(x, -(int) (Game.SCALE * 15));
                    return;
                }
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        xLvlOffset = Math.max(Math.min(xLvlOffset, maxLvlOffsetX), 0);
    }

    @Override
    public void draw(Graphics g) {
		if(levelManager.getLevelIndex() <= lvlbackground1) {
			g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		}
		else if(levelManager.getLevelIndex() <= lvlbackground2 && levelManager.getLevelIndex() > lvlbackground1) {
			g.drawImage(backgroundImg2, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		}
		else if(levelManager.getLevelIndex() <= lvlbackground3 && levelManager.getLevelIndex() > lvlbackground2) {
			g.drawImage(backgroundImg3, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		}
		drawClouds(g);
        if (drawRain)
            rain.draw(g, xLvlOffset);

        if (drawShip)
            g.drawImage(shipImgs[shipAni], (int) (100 * Game.SCALE) - xLvlOffset, (int) ((288 * Game.SCALE) + shipHeightDelta), (int) (78 * Game.SCALE), (int) (72 * Game.SCALE), null);

        levelManager.draw(g, xLvlOffset);
        objectManager.draw(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
         // Draw Sheep only if it exists
        if (sheep != null) {
            sheep.render(g, xLvlOffset);
        }
        player.render(g, xLvlOffset);
        objectManager.drawBackgroundTrees(g, xLvlOffset);
        drawDialogue(g, xLvlOffset);
		// Draw level start image
		if (showLevelStartImage && levelStartImageIndex < levelStartImage.length) {
			drawLevelStartImage(g);
		}
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (gameCompleted)
            gameCompletedOverlay.draw(g);
           
         // Draw the fade effect
        if (levelTransitioning) {
            drawFadeEffect(g);
        }
    }
   private void drawLevelStartImage(Graphics g) {
       if (!showLevelStartImage || levelStartImageIndex >= levelStartImage.length || levelStartImage[levelStartImageIndex] == null)
           return;
        BufferedImage currentImage = levelStartImage[levelStartImageIndex];
        int imgWidth = currentImage.getWidth();
        int imgHeight = currentImage.getHeight();


        // Calculate scaling
        float scaleFactor = 0.25f;
        float maxScaleFactor = 0.75f; // Maximum scale factor we want to reach

        if (levelStartImageTimer < levelStartImageDuration / 2) {
            scaleFactor = 0.25f + (float) levelStartImageTimer / (float) (levelStartImageDuration/2) * (maxScaleFactor - 0.25f); // Scale up initially
            scaleFactor = Math.min(scaleFactor,maxScaleFactor); // Ensure scale does not pass the max scale
        }else{
            scaleFactor = maxScaleFactor - (float) (levelStartImageTimer - (levelStartImageDuration / 2)) / (float) (levelStartImageDuration/2) * (maxScaleFactor-0.25f); // Scale down
            scaleFactor = Math.max(scaleFactor,0.25f); // Ensure scale is not less than 0.25
        }

        int scaledWidth = (int) (imgWidth * scaleFactor);
        int scaledHeight = (int) (imgHeight * scaleFactor);


        int x = (Game.GAME_WIDTH - scaledWidth) / 2;
        int y = (Game.GAME_HEIGHT - scaledHeight) / 2;


        // Calculate fade alpha value
        float alpha = 1.0f;
        if (levelStartImageTimer < levelStartImageDuration / 4) {
            alpha = (float) levelStartImageTimer / (float) (levelStartImageDuration / 4); // Fade in
        }else if(levelStartImageTimer >= (levelStartImageDuration * 3)/ 4){
            alpha = 1f - (float)(levelStartImageTimer - (levelStartImageDuration * 3)/ 4) / (float)(levelStartImageDuration / 4); //Fade out
            alpha = Math.max(alpha,0);
        }


        // Apply the alpha to the Graphics object.
        java.awt.AlphaComposite alphaComposite = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha);
        ((java.awt.Graphics2D)g).setComposite(alphaComposite);


        g.drawImage(currentImage, x, y, scaledWidth, scaledHeight, null);

        ((java.awt.Graphics2D)g).setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f)); // Reset the alpha composite
    }
     private void drawFadeEffect(Graphics g) {
        g.setColor(new Color(0, 0, 0, Math.min(1f, fadeAlpha)));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 4; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) ((xLvlOffset)* 0.3), (int) ((204 * Game.SCALE)+130), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i]+50, SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
    }

    public void setGameCompleted() {
        gameCompleted = true;
    }

    public void resetGameCompleted() {
        gameCompleted = false;
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        // lvlCompleted = false; Remove lvlCompleted
        playerDying = false;
		showLevelStartImage = false;
		firstLevel = false;
        drawRain = false;
        fadeAlpha = 0; //Reset fade alpha
        levelTransitioning = false; // reset fade boolean
        setDrawRainBoolean();
        
        
        player.resetAll();
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();
        dialogEffects.clear();
		if (sheep != null && levelManager.getLevelIndex() >= levelWithSheep) {
			sheep = new FlyingSheep(500, 300, 128, 128);
		}

    }

    private void setDrawRainBoolean() {
        // This method makes it rain 20% of the time you load a level.
        if (rnd.nextFloat() >= 0.8f)
            drawRain = true;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void checkPotionTouched(Rectangle2D.Float hitbox) {
        objectManager.checkObjectTouched(hitbox);
    }

    public void checkSpikesTouched(Player p) {
        objectManager.checkSpikesTouched(p);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && !gameCompleted && !levelTransitioning) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_W:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
        }

		if (e.getKeyCode() == KeyEvent.VK_O) {
		     this.setLevelCompleted();
				if (levelManager.getLevelIndex() == 999){
					resetAll();
				}
        }


		if (!gameOver) {
			if (e.getKeyCode() == KeyEvent.VK_N)
				player.setAttacking(true);
			else if (e.getKeyCode() == KeyEvent.VK_M)
				player.powerAttack();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver && !gameCompleted && !levelTransitioning)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_W:
                    player.setJump(false);
                    break;

            }
		if (!gameOver){
			if (e.getKeyCode() == KeyEvent.VK_N)
				player.setAttacking(true);
			else if (e.getKeyCode() == KeyEvent.VK_M)
				player.powerAttack();
		}
    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver && !gameCompleted && !levelTransitioning)
            if (paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mousePressed(e);
        else if (paused)
            pauseOverlay.mousePressed(e);
        else if (gameCompleted)
            gameCompletedOverlay.mousePressed(e);
           

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseReleased(e);
        else if (paused)
            pauseOverlay.mouseReleased(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseMoved(e);
        else if (paused)
            pauseOverlay.mouseMoved(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseMoved(e);
    }

    public void setLevelCompleted() {
        game.getAudioPlayer().lvlCompleted();
        if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
            // No more levels
            gameCompleted = true;
            resetAll();
            return;
        }

        // Initiate fade effect
        levelTransitioning = true;
        fadeAlpha = 0f;
       
    }

    public void setMaxLvlOffset(int lvlOffset) {
        this.maxLvlOffsetX = lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }
    
    public ArrayList<Enemy> getAllActiveEnemies() {
		ArrayList<Enemy> enemies = new ArrayList<>();
		Level currentLevel = levelManager.getCurrentLevel();
	    
		enemies.addAll(currentLevel.getCrabs());
		enemies.addAll(currentLevel.getPinkstars());
		enemies.addAll(currentLevel.getSharks());
	    
		// Optionally, remove inactive enemies
		enemies.removeIf(enemy -> !enemy.isActive());
	    
		return enemies;
	    }
}