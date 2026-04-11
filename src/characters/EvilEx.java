package characters;

import city.cs.engine.*;
import game.Shockwave;
import org.jbox2d.common.Vec2;

/* * This is the final boss character, Gideon Graves.
 * I used AI to help me structure some of the more advanced logic here,
 * especially the StepListener stuff for handling cooldown timers frame-by-frame
 * and the state machine for his animations so the GIFs wouldn't glitch out.
 */
public class EvilEx extends Walker implements StepListener {
    private static final Shape shape = new BoxShape(1, 2.6f);
    private static final BodyImage idle = new BodyImage("data/gideonidle.gif", 6f);
    private static final BodyImage walkRight = new BodyImage("data/gideonWalkingRight.gif", 6f);
    private static final BodyImage walkLeft = new BodyImage("data/gideonWalkingLeft.gif", 6f);
    private static final BodyImage slashR = new BodyImage("data/gideonslashright.gif", 6f);
    private static final BodyImage slashL = new BodyImage("data/Gideonslashleft.gif", 6f);
    private static final BodyImage fastSlashR = new BodyImage("data/gideonfastslashright.gif", 6f);
    private static final BodyImage fastSlashL = new BodyImage("data/gideonfastslashleft.gif", 6f);

    //From this point below I have used AI to help me create all the sounds and make sure it works fine.

    private static SoundClip jumpSound;
    private static SoundClip slashSound;
    private static SoundClip smashSound;
    private static SoundClip hitSound;

    //This is what AI have suggested to do.
    // It is static so it can be shared across all instances of EvilEx even though there will only be one
    static {
        try {
            jumpSound = new SoundClip("data/Playerjump.wav");
            slashSound = new SoundClip("data/GideonSlash.wav");
            smashSound = new SoundClip("data/Groundslam.wav");
            hitSound = new SoundClip("data/LostLife.wav");

            jumpSound.setVolume(0.8);
            slashSound.setVolume(0.9);
            smashSound.setVolume(1.0);
            hitSound.setVolume(1.0);
        } catch (Exception e) {
            System.out.println("Error loading Gideon sounds: " + e);
        }
    }

    private boolean grounded = true;
    private int lives = 3;
    private float speedBoostTimer = 0f;
    private boolean facingRight = false;

    // GIDEON MECHANICS
    private boolean isEnraged = false;
    private float enrageTimer = 0f;
    private float enrageCooldown = 0f;
    private float attackTimer = 0f;
    private float slashCooldown = 0f;

    private int smashState = 0;
    private float liftTimer = 0f;
    private float smashCooldown = 0f;

    // Animation State Tracker
    private String currentState = "IDLE";

    public EvilEx(World world) {
        super(world, shape);
        addImage(idle);
        setGravityScale(3f);
        world.addStepListener(this);

        this.addCollisionListener(e -> {
            // Checks if Gideon hit the ground
            if (e.getOtherBody().getPosition().y < getPosition().y) {
                grounded = true;

                // smashState 2 means he was falling from his jump attack
                if (smashState == 2) {
                    smashState = 0;
                    smashCooldown = 5f; // FASTER SMASH COOLDOWN (5 seconds)
                    setGravityScale(3f);

                    //I used AI to figure out how to spawn two projectiles going in opposite directions
                    new Shockwave(getWorld(), true).setPosition(new Vec2(getPosition().x + 2, getPosition().y - 1));
                    new Shockwave(getWorld(), false).setPosition(new Vec2(getPosition().x - 2, getPosition().y - 1));
                }
            }
        });
    }

    public void setLives(int l) { this.lives = l; }
    public int getLives() { return lives; }

    public void decrementLives() {
        lives--;
        if (hitSound != null) hitSound.play();
    }

    public void takeDamage(int dmg) {
        lives -= dmg;
        if (hitSound != null) hitSound.play();
    }

    public void resetLives() { lives = 3; }
    public void incrementLives() { if (lives < 3) lives++; }
    public void activateSpeedBoost() { speedBoostTimer = 5f; }

    public boolean isFacingRight() { return facingRight; }

    // ENRAGE GETTERS
    public boolean isEnraged() { return isEnraged; }
    public float getEnrageTimer() { return enrageTimer; }
    public float getEnrageCooldown() { return enrageCooldown; }
    public float getSmashCooldown() { return smashCooldown; }

    // I set up this boolean check so he can only slash if he's not already smashing or on cooldown
    public boolean canSlash() {
        if (isEnraged) return attackTimer <= 0 && smashState == 0;
        return attackTimer <= 0 && smashState == 0 && slashCooldown <= 0;
    }

    public void slash() {
        attackTimer = isEnraged ? 0.15f : 0.4f;
        slashCooldown = isEnraged ? 0.0f : 1.0f; // Slash spamming when enraged

        if (slashSound != null) {
            if (isEnraged) slashSound.stop(); // Stops previous sound to instantly restart it for the spam effect
            slashSound.play();
        }
    }

    // Triggered by ENTER
    public void triggerEnrage() {
        if (enrageCooldown <= 0) {
            isEnraged = true;
            enrageTimer = 10f; // 10 seconds of Enrage
            enrageCooldown = 20f; // 20 second total cooldown 10s active, 10s waiting
        }
    }

    public void triggerSmash() {
        if (smashCooldown <= 0 && smashState == 0) {
            smashState = 1; // 1 means he is lifting up into the air
            liftTimer = 0.8f;
            setGravityScale(0); // I set this to 0 so he floats up smoothly before dropping
            setLinearVelocity(new Vec2(getLinearVelocity().x, 6f));
            grounded = false;
            if (smashSound != null) smashSound.play();
        }
    }

    public float getWalkSpeed() {
        float base = isEnraged ? 14f : 8f; // MUCH faster movement when enraged
        return speedBoostTimer > 0 ? base * 2 : base;
    }

    public float getJumpSpeed() { return 25f; }

    @Override
    public void jump(float speed) {
        if ((grounded || Math.abs(getLinearVelocity().y) < 0.01f) && smashState == 0) {
            setLinearVelocity(new Vec2(getLinearVelocity().x, speed));
            grounded = false;
            if (jumpSound != null) jumpSound.play();
        }
    }

    /* This preStep method runs every single frame.
     I had to use AI to help manage all the different timers here, otherwise the cooldowns
     wouldn't tick down correctly based on the stepEvent.
     */
    @Override
    public void preStep(StepEvent stepEvent) {
        float step = stepEvent.getStep();

        // Ticking down all the basic cooldowns
        if (speedBoostTimer > 0) speedBoostTimer -= step;
        if (attackTimer > 0) attackTimer -= step;
        if (slashCooldown > 0) slashCooldown -= step;
        if (smashCooldown > 0) smashCooldown -= step;

        // Handle Enrage Timers
        if (enrageCooldown > 0) enrageCooldown -= step;
        if (enrageTimer > 0) {
            enrageTimer -= step;
            if (enrageTimer <= 0) isEnraged = false; // Turn off enrage when 10s is up
        }

        // Figures out which way Gideon is facing based on his velocity
        if (getLinearVelocity().x < -0.1f) facingRight = false;
        else if (getLinearVelocity().x > 0.1f) facingRight = true;
        else facingRight = false;

        // Handles the timing for the smash attack dropping down
        if (smashState == 1) {
            liftTimer -= step;
            if (liftTimer <= 0) {
                smashState = 2; // 2 means he is slamming down
                setGravityScale(10f); // Make him fall super fast
                setLinearVelocity(new Vec2(0, -40f));
            }
        }

        /*I used AI to help me build this string-based state tracker.
         Before I had this, the game was reloading the GIF every single frame which
         made the animations look super glitchy. Now it only reloads if the state actually changes.
         */
        String newState;
        if (attackTimer > 0) {
            if (isEnraged) newState = facingRight ? "FAST_SLASH_R" : "FAST_SLASH_L";
            else newState = facingRight ? "SLASH_R" : "SLASH_L";
        } else if (getLinearVelocity().x < -0.1f) {
            newState = "WALK_L";
        } else if (getLinearVelocity().x > 0.1f) {
            newState = "WALK_R";
        } else {
            newState = "IDLE";
        }

        if (!currentState.equals(newState)) {
            removeAllImages();
            switch (newState) {
                case "FAST_SLASH_R": addImage(fastSlashR); break;
                case "FAST_SLASH_L": addImage(fastSlashL); break;
                case "SLASH_R": addImage(slashR); break;
                case "SLASH_L": addImage(slashL); break;
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