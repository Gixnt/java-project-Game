package characters;

import city.cs.engine.*;
import game.Projectile;
import org.jbox2d.common.Vec2;

public class Scott extends Walker implements StepListener {
    private static final Shape shape = new BoxShape(1, 2.9f);
    private static final BodyImage idle = new BodyImage("data/ScottPilgrimIdle.gif", 6f);
    private static final BodyImage walkRight = new BodyImage("data/ScottRunning.gif", 6f);
    private static final BodyImage walkLeft = new BodyImage("data/ScottRunningLeft.gif", 6f);
    private static final BodyImage kickRight = new BodyImage("data/Scottrightkick.gif", 6f);
    private static final BodyImage kickLeft = new BodyImage("data/ScottLeftKick.gif", 6f);
    private static final BodyImage blockImg = new BodyImage("data/ScottBlock.gif", 6f);
    private static final BodyImage blockLeftImg = new BodyImage("data/ScottBlockLeft.gif", 6f);


    private static SoundClip jumpSound;
    private static SoundClip kickSound;
    private static SoundClip shootSound;
    private static SoundClip hitSound;


    //Using a static block for the SoundClips means the game only loads the audio files
    //into memory once, rather than every single time Scott respawns. This stops the game from lagging.
    static {
        try {
            jumpSound = new SoundClip("data/Playerjump.wav");
            kickSound = new SoundClip("data/ScottKick.wav");
            shootSound = new SoundClip("data/BulletShot.wav");
            hitSound = new SoundClip("data/LostLife.wav");

            jumpSound.setVolume(0.8);
            kickSound.setVolume(0.9);
            shootSound.setVolume(0.9);
            hitSound.setVolume(1.0);
        } catch (Exception e) {
            System.out.println("Error loading Scott sounds: " + e);
        }
    }

    private int lives = 3;
    private int maxLives = 3;
    private boolean grounded = true;

    private float speedBoostTimer = 0f;
    private float invincibleTimer = 0f;
    private int traps = 0;

    private boolean facingRight = true;
    private int ammo = 0;
    private boolean hasGun = false;

    // fighting and blocking states
    private boolean isBlocking = false;
    private float attackTimer = 0f;
    private float kickCooldown = 0f;
    private float blockCooldown = 0f;

    private String currentState = "IDLE";

    public Scott(World world) {
        super(world, shape);
        addImage(idle);
        setGravityScale(3f);
        world.addStepListener(this);

        this.addCollisionListener(e -> {
            if (e.getOtherBody().getPosition().y < getPosition().y) grounded = true;
        });
    }

    public void setMaxLives(int max) { this.maxLives = max; this.lives = max; }
    public int getLives() { return lives; }
    public int getMaxLives() { return maxLives; }

    public void decrementLives() { takeDamage(1); }

    // If he gets hit while blocking, it breaks his block and puts it on a 2 sec cooldown
    public void takeDamage(int amount) {
        if (isInvincible()) return;

        if (isBlocking && blockCooldown <= 0) {
            isBlocking = false;
            blockCooldown = 2.0f;
            System.out.println("Scott's block was broken!");
            return;
        }

        lives -= amount;
        if (hitSound != null) hitSound.play();

        if (lives < 0) lives = 0;
    }

    public void resetLives(){ lives = maxLives; }
    public void incrementLives() { if (lives < maxLives) lives++; }

    public void setBlocking(boolean b) { if (blockCooldown <= 0) this.isBlocking = b; }
    public boolean isBlocking() { return isBlocking; }

    public boolean canKick() { return attackTimer <= 0 && kickCooldown <= 0 && !isBlocking; }

    public void kick() {
        attackTimer = 0.4f;
        kickCooldown = 1.0f;
        if (kickSound != null) kickSound.play();
    }

    public boolean isFacingRight() { return facingRight; }

    public void addTrap() { traps++; }
    public boolean useTrap() { if (traps > 0) { traps--; return true; } return false; }
    public int getTraps() { return traps; }
    public void activateSpeedBoost() { speedBoostTimer = 5f; }
    public void activateInvincibility() { invincibleTimer = 5f; }
    public boolean isInvincible() { return invincibleTimer > 0; }
    public float getWalkSpeed() { return speedBoostTimer > 0 ? 16f : 8f; }
    public float getJumpSpeed() { return 25f; }

    public boolean hasGun() { return hasGun; }
    public void setHasGun(boolean hasGun) { this.hasGun = hasGun; }
    public int getAmmo() { return ammo; }
    public void addAmmo(int amount) { ammo += amount; }
    public void consumeAmmo() { if (ammo > 0) ammo--; }

    public void shoot(World world) {
        // Can only shoot if he has the gun, has ammo, isn't currently kicking, and isn't blocking
        if (hasGun && ammo > 0 && attackTimer <= 0 && !isBlocking) {
            consumeAmmo();
            if (shootSound != null) shootSound.play();

            // Spawns the laser bullet
            Projectile blast = new Projectile(world);

            // Checks which way he's facing so the bullet spawns slightly in front of him instead of inside him
            float offset = facingRight ? 2.0f : -2.0f;
            blast.setPosition(new Vec2(this.getPosition().x + offset, this.getPosition().y));
            blast.setLinearVelocity(new Vec2(facingRight ? 50f : -50f, 0));
        }
    }

    @Override
    public void jump(float speed) {
        if ((grounded || Math.abs(getLinearVelocity().y) < 0.01f) && !isBlocking) {
            setLinearVelocity(new Vec2(getLinearVelocity().x, speed));
            grounded = false;
            if (jumpSound != null) jumpSound.play();
        }
    }

    /* Over here I used AI to show me how to structure this to handle all
     the cooldown timers manually, subtracting the step value each frame.
     */

    @Override
    public void preStep(StepEvent stepEvent) {
        float step = stepEvent.getStep();
        if (speedBoostTimer > 0) speedBoostTimer -= step;
        if (invincibleTimer > 0) invincibleTimer -= step;
        if (attackTimer > 0) attackTimer -= step;
        if (kickCooldown > 0) kickCooldown -= step;
        if (blockCooldown > 0) blockCooldown -= step;

        if (getLinearVelocity().x < -0.1f) facingRight = false;
        else if (getLinearVelocity().x > 0.1f) facingRight = true;
        else facingRight = true;

        /*
        This checks what Scott is doing and assigns it a string.
        Then, it checks if that string is DIFFERENT from the last frame.
        If it is different, it updates the GIF. This completely fixed the glitching issue that I had earlier
         */
        String newState;
        if (isBlocking && blockCooldown <= 0) newState = facingRight ? "BLOCK_R" : "BLOCK_L";
        else if (attackTimer > 0) newState = facingRight ? "KICK_R" : "KICK_L";
        else if (getLinearVelocity().x < -0.1f) newState = "WALK_L";
        else if (getLinearVelocity().x > 0.1f) newState = "WALK_R";
        else newState = "IDLE";

        if (!currentState.equals(newState)) {
            removeAllImages();
            switch (newState) {
                case "BLOCK_R": addImage(blockImg); break;
                case "BLOCK_L": addImage(blockLeftImg); break;
                case "KICK_R": addImage(kickRight); break;
                case "KICK_L": addImage(kickLeft); break;
                case "WALK_R": addImage(walkRight); break;
                case "WALK_L": addImage(walkLeft); break;
                default: addImage(idle); break;
            }
            currentState = newState;
        }
    }
    @Override
    public void postStep(StepEvent stepEvent) {}
}