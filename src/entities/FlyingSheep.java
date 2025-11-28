package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Game;
import utilz.LoadSave;

public class FlyingSheep extends Entity {

    public enum SheepState {
        IDLE,
        FOLLOWING,
        ATTACKING
    }

    private BufferedImage[] bouncingSprites;
    private BufferedImage[] idleSprites;
    private int aniTick, aniIndex;
    private static final int ANI_SPEED = 25;
    private boolean movingLeft = true;
    private SheepState currentState = SheepState.IDLE;
    private Enemy targetEnemy;
    private static final float FIND_ENEMY_RADIUS = 400f;
    private static final float ATTACK_RANGE = 60f;
    private static final float FOLLOW_DISTANCE = 400f; 
    private static final float MOVE_SPEED = 0.6f * Game.SCALE;
    private static final float ATTACK_COOLDOWN = 160;
    private int attackCooldownTick = 0;

    public FlyingSheep(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(20, 20);
    }

    private void loadAnimations() {
        BufferedImage bouncingSheet = LoadSave.GetSpriteAtlas(LoadSave.SHEEP_BOUNCING);
        BufferedImage idleSheet = LoadSave.GetSpriteAtlas(LoadSave.SHEEP_IDLE);
        bouncingSprites = new BufferedImage[6];
        idleSprites = new BufferedImage[8];

        for (int i = 0; i < bouncingSprites.length; i++) {
            bouncingSprites[i] = bouncingSheet.getSubimage(i * width, 0, width, height);
        }

        for (int i = 0; i < idleSprites.length; i++) {
            idleSprites[i] = idleSheet.getSubimage(i * width, 0, width, height);
        }
    }

    public void update(Player player, ArrayList<Enemy> enemies) {
        handleState(player, enemies);
        handleAnimation();
    }

    private void handleState(Player player, ArrayList<Enemy> enemies) {
       targetEnemy = findNearestEnemy(enemies, FIND_ENEMY_RADIUS);

        switch (currentState) {
            case IDLE:
                if (targetEnemy != null) {
                    changeState(SheepState.ATTACKING);
                 } else if (playerIsWithinDistance(player, FOLLOW_DISTANCE)) {
                    changeState(SheepState.FOLLOWING);
                }
                break;

           case FOLLOWING:
              if (targetEnemy != null) {
                    changeState(SheepState.ATTACKING);
               } else if (!playerIsWithinDistance(player, FOLLOW_DISTANCE)) {
                    changeState(SheepState.IDLE);
                 }else {
                  followPlayer(player);
                }
                break;

             case ATTACKING:
                if (targetEnemy != null && targetEnemy.isActive()) {
                    float distanceToEnemy = distanceTo(targetEnemy);
                    if (distanceToEnemy <= ATTACK_RANGE) {
                        attackEnemy();
                     } else if (distanceToEnemy <= FIND_ENEMY_RADIUS) {
                       followTarget(targetEnemy);
                    } else {
                        targetEnemy = null;
                        changeState(SheepState.IDLE);
                    }
                } else {
                     targetEnemy = null;
                    changeState(SheepState.IDLE); // Go to idle if target is not valid
                 }
                break;
        }
    }



    private void handleAnimation() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            BufferedImage[] currentSprites = getCurrentSpriteArray();
            if (aniIndex >= currentSprites.length) {
                aniIndex = 0;
            }
        }
    }

    private void changeState(SheepState newState) {
        if (currentState != newState) {
            currentState = newState;
            aniTick = 0;
            aniIndex = 0;
        }
    }

    private boolean playerIsWithinDistance(Player player, float distance) {
        float dx = player.getHitbox().x - hitbox.x;
        float dy = player.getHitbox().y - hitbox.y;
        return Math.hypot(dx, dy) < distance;
    }


    private void followPlayer(Player player) {
        float dx = player.getHitbox().x - hitbox.x + 50;
        float dy = player.getHitbox().y - hitbox.y - 100;
        moveTowards(dx, dy);
    }

    private void followTarget(Enemy enemy) {
        float dx = enemy.getHitbox().x - hitbox.x;
        float dy = enemy.getHitbox().y - hitbox.y;
        moveTowards(dx, dy);
    }

    private void moveTowards(float dx, float dy) {
        float distance = (float) Math.hypot(dx, dy);
        if (Math.abs(dx) > 5) {
            movingLeft = dx < 0;
        }

        if (distance > 0) {
            float angle = (float) Math.atan2(dy, dx);
            float deltaX = (float) Math.cos(angle) * MOVE_SPEED;
            float deltaY = (float) Math.sin(angle) * MOVE_SPEED;

            hitbox.x += deltaX;
            hitbox.y += deltaY;
        }
    }


    private Enemy findNearestEnemy(ArrayList<Enemy> enemies, float range) {
        Enemy nearest = null;
        float minDistance = Float.MAX_VALUE;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                float distance = distanceTo(enemy);
                if (distance < minDistance && distance <= range) {
                    minDistance = distance;
                    nearest = enemy;
                }
            }
        }
        return nearest;
    }

    private float distanceTo(Enemy enemy) {
        float dx = enemy.getHitbox().x - hitbox.x;
        float dy = enemy.getHitbox().y - hitbox.y;
        return (float) Math.hypot(dx, dy);
    }

   private void attackEnemy() {
        if (attackCooldownTick > 0) {
            attackCooldownTick--;
            return;
        }

        if (targetEnemy != null && targetEnemy.isActive()) {
            targetEnemy.hurt(7);
            attackCooldownTick = (int) ATTACK_COOLDOWN;
        }
    }


    private BufferedImage[] getCurrentSpriteArray() {
        switch (currentState) {
            case IDLE:
                return idleSprites;
            case FOLLOWING:
            case ATTACKING:
                return bouncingSprites;
            default:
                return idleSprites;
        }
    }


    public void render(Graphics g, int lvlOffset) {
        BufferedImage currentSprite = getCurrentSpriteArray()[aniIndex];
        int drawX = (int) hitbox.x - lvlOffset;
        int drawY = (int) hitbox.y;

        if (movingLeft) {
            g.drawImage(currentSprite, drawX + width - 75, drawY - 50, -width, height, null);
        } else {
            g.drawImage(currentSprite, drawX - 75, drawY - 50, width, height, null);
        }
    }
}