package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.awt.Color;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.Image;


//The objective here is to collect 3 keys to progress while dodging Gideon and spikes.

public class Level2 extends GameLevel {

    private final Image bgImage = new ImageIcon("data/Level2.jpg").getImage();

    public static final float FLOOR_Y = -15f;
    public static final float WALL_X = 45f;

    private int keysCollected = 0;
    private final int KEYS_NEEDED = 3;

    private long lastTeleportTime = 0;
    private float gideonRespawnTimer = 0f;

    public Level2() {
        super();

        Scott scott = new Scott(this);
        scott.setPosition(new Vec2(-10f, FLOOR_Y + 3f));
        setScott(scott);

        EvilEx ex = new EvilEx(this);
        ex.setPosition(new Vec2(10f, FLOOR_Y + 3f));
        setEvilEx(ex);

        scott.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof EvilEx) {
                if (!scott.isInvincible()) {
                    scott.decrementLives();
                    scott.setPosition(new Vec2(-10f, FLOOR_Y + 3f));
                    ex.setPosition(new Vec2(10f, FLOOR_Y + 3f));
                    scott.setLinearVelocity(new Vec2(0,0));
                    ex.setLinearVelocity(new Vec2(0,0));
                }
            }
        });

       //Ground
        makeBlock(-25f, FLOOR_Y - 2f, 20f, 2f, Color.DARK_GRAY);
        makeBlock(25f, FLOOR_Y - 2f, 20f, 2f, Color.DARK_GRAY);

        // Spikes
        makeSpike(-4f, FLOOR_Y + 1.0f);
        makeSpike(0f, FLOOR_Y + 1.0f);
        makeSpike(4f, FLOOR_Y + 1.0f);

        makeBouncePad(-22f, FLOOR_Y + 1.5f);
        makeVerticalPipe(-13f, FLOOR_Y + 4f);

        makeLongPipePlatform(0f, FLOOR_Y + 8f);
        makeTeleporter(0f, FLOOR_Y + 10.5f, 0);

        makePipePlatform(28f, FLOOR_Y + 10f);
        makeSuitcaseCollectible(28f, FLOOR_Y + 12.2f);

        makePipePlatform(-32f, FLOOR_Y + 12f);
        makeHeart(-32f, FLOOR_Y + 14.0f);

        makeLongPipePlatform(14f, FLOOR_Y + 15f);
        makeBigSpike(14f, FLOOR_Y + 17.2f);
        makeStar(18.5f, FLOOR_Y + 16.5f);

        makePipePlatform(-14f, FLOOR_Y + 18f);
        makeEnergyDrink(-14f, FLOOR_Y + 20.0f);

        makePipePlatform(0f, FLOOR_Y + 23f);

        makePipePlatform(-36f, FLOOR_Y + 26f);
        makeTeleporter(-36f, FLOOR_Y + 28.5f, 1);

        makePipePlatform(-38f, FLOOR_Y + 34f);
        makeKey(-38f, FLOOR_Y + 36.5f);

        makePipePlatform(-20f, FLOOR_Y + 28f);

        makePipePlatform(36f, FLOOR_Y + 26f);
        makeTeleporter(36f, FLOOR_Y + 28.5f, 2);

        makePipePlatform(20f, FLOOR_Y + 28f);
        makeKey(20f, FLOOR_Y + 30.5f);

        makePipePlatform(0f, FLOOR_Y + 34f);
        makeKey(0f, FLOOR_Y + 36.5f);

        addStepListener(new StepListener() {
            @Override public void preStep(StepEvent e) {
                wrapPlayer(getScott());
                wrapPlayer(getEvilEx());

                //Timer for Gideon when he dies and he can later respawn.
                if (gideonRespawnTimer > 0) {
                    gideonRespawnTimer -= e.getStep();
                    if (gideonRespawnTimer <= 0) {
                        getEvilEx().resetLives();
                        getEvilEx().setPosition(new Vec2(10f, FLOOR_Y + 3f));
                        getEvilEx().setLinearVelocity(new Vec2(0,0));
                    }
                }
            }
            @Override public void postStep(StepEvent e) {}
        });
    }

    private void makeBlock(float x, float y, float w, float h, Color color) {
        Shape shape = new BoxShape(w, h);
        StaticBody b = new StaticBody(this, shape);
        b.setPosition(new Vec2(x, y));
        b.setFillColor(color);
        new SolidFixture(b, shape).setFriction(3f);
    }

    private void makePipePlatform(float x, float y) {
        Shape shape = new BoxShape(3f, 0.5f);
        StaticBody b = new StaticBody(this, shape);
        b.setPosition(new Vec2(x, y));
        b.addImage(new BodyImage("data/Pipe.png", 4f));
        new SolidFixture(b, shape).setFriction(3f);
    }

    private void makeLongPipePlatform(float x, float y) {
        makePipePlatform(x - 2.8f, y);
        makePipePlatform(x + 2.8f, y);
    }

    private void makeVerticalPipe(float x, float y) {
        Shape shape = new BoxShape(0.5f, 4f);
        StaticBody b = new StaticBody(this, shape);
        b.setPosition(new Vec2(x, y));
        b.addImage(new BodyImage("data/PipeVertical.png", 9f));
        new SolidFixture(b, shape).setFriction(3f);
    }

    private void wrapPlayer(Walker player) {
        if (player == null) return;
        Vec2 pos = player.getPosition();
        if (pos.x > WALL_X) player.setPosition(new Vec2(-WALL_X + 1f, pos.y));
        else if (pos.x < -WALL_X) player.setPosition(new Vec2(WALL_X - 1f, pos.y));
    }

    // Handles what happens when Gideon hits a spike or a trap
    public void damageGideon() {
        EvilEx enemy = getEvilEx();
        if (gideonRespawnTimer > 0) return;

        enemy.decrementLives();
        if (enemy.getLives() <= 0) {
            enemy.setPosition(new Vec2(0, -100f));
            enemy.setLinearVelocity(new Vec2(0,0));
            gideonRespawnTimer = 3f;
        } else {
            enemy.setPosition(new Vec2(10f, FLOOR_Y + 3f));
            enemy.setLinearVelocity(new Vec2(0,0));
        }
    }

    private void makeSpike(float x, float y) {
        Shape shape = new BoxShape(1.0f, 1.0f);
        StaticBody spike = new StaticBody(this, shape);
        spike.setPosition(new Vec2(x, y));
        spike.addImage(new BodyImage("data/Spike.png", 3.0f));

        spike.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                Scott s = (Scott) e.getOtherBody();
                if (!s.isInvincible()) {
                    s.decrementLives();
                    s.setPosition(new Vec2(-10f, FLOOR_Y + 3f));
                }
            } else if (e.getOtherBody() instanceof EvilEx) {
                damageGideon();
            }
        });
    }

    private void makeBigSpike(float x, float y) {
        Shape shape = new BoxShape(1.5f, 1.5f);
        StaticBody spike = new StaticBody(this, shape);
        spike.setPosition(new Vec2(x, y));
        spike.addImage(new BodyImage("data/Spike.png", 4.5f));

        spike.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                Scott s = (Scott) e.getOtherBody();
                if (!s.isInvincible()) {
                    s.decrementLives();
                    s.setPosition(new Vec2(-10f, FLOOR_Y + 3f));
                }
            } else if (e.getOtherBody() instanceof EvilEx) {
                damageGideon();
            }
        });
    }

    private void makeBouncePad(float x, float y) {
        Shape shape = new BoxShape(1.5f, 0.4f);
        StaticBody pad = new StaticBody(this, shape);
        pad.setPosition(new Vec2(x, y));
        pad.addImage(new BodyImage("data/JumpPad.png", 3.5f));

        pad.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Walker) {
                ((Walker) e.getOtherBody()).setLinearVelocity(new Vec2(0, 35f));
            }
        });
    }

    private void makeTeleporter(float x, float y, int teleporterId) {
        StaticBody portal = new StaticBody(this, new CircleShape(1.5f));
        portal.setPosition(new Vec2(x, y));
        portal.addImage(new BodyImage("data/Teleporter.png", 5.5f));

        portal.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Walker && System.currentTimeMillis() - lastTeleportTime > 2000) {
                Walker w = (Walker) e.getOtherBody();

                // Pick a random teleport destination that isn't the one we are currently standing on
                int destId = (int)(Math.random() * 2);
                if (destId >= teleporterId) destId++;

                if (destId == 0) {
                    w.setPosition(new Vec2(0f, FLOOR_Y + 12f));
                } else if (destId == 1) {
                    w.setPosition(new Vec2(-36f, FLOOR_Y + 29f));
                } else if (destId == 2) {
                    w.setPosition(new Vec2(36f, FLOOR_Y + 29f));
                }

                w.setLinearVelocity(new Vec2(0, 0));

                if (teleportSound != null) teleportSound.play();

                lastTeleportTime = System.currentTimeMillis();
            }
        });
    }

    private void makeKey(float x, float y) {
        StaticBody key = new StaticBody(this, new CircleShape(1.2f));
        key.setPosition(new Vec2(x, y));
        key.addImage(new BodyImage("data/Key.gif", 5.5f));

        key.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                if (keySound != null) keySound.play(); // Uses shared sound!
                keysCollected++;
                key.destroy();
            }
        });
    }

    private void makeHeart(float x, float y) {
        StaticBody heart = new StaticBody(this, new CircleShape(0.8f));
        heart.setPosition(new Vec2(x, y));
        heart.addImage(new BodyImage("data/heart.png", 1.8f));

        heart.addCollisionListener(e -> {
            boolean collected = false;
            if (e.getOtherBody() instanceof Scott) {
                Scott s = (Scott) e.getOtherBody();
                if (s.getLives() < 3) {
                    s.incrementLives();
                    collected = true;
                }
            } else if (e.getOtherBody() instanceof EvilEx) {
                EvilEx ex = (EvilEx) e.getOtherBody();
                if (ex.getLives() < 3) {
                    ex.incrementLives();
                    collected = true;
                }
            }

            if (collected) {
                if (itemSound != null) itemSound.play();
                heart.destroy();
                Timer respawnTimer = new Timer(10000, evt -> {
                    makeHeart(x, y);
                    ((Timer)evt.getSource()).stop();
                });
                respawnTimer.start();
            }
        });
    }

    private void makeStar(float x, float y) {
        StaticBody star = new StaticBody(this, new CircleShape(0.9f));
        star.setPosition(new Vec2(x, y));
        star.addImage(new BodyImage("data/Star.png", 2.5f));

        star.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                ((Scott) e.getOtherBody()).activateInvincibility();
                if (itemSound != null) itemSound.play(); // Uses shared sound!
                star.destroy();
                Timer respawnTimer = new Timer(20000, evt -> {
                    makeStar(x, y);
                    ((Timer)evt.getSource()).stop();
                });
                respawnTimer.start();
            }
        });
    }

    private void makeEnergyDrink(float x, float y) {
        StaticBody drink = new StaticBody(this, new BoxShape(0.8f, 1.2f));
        drink.setPosition(new Vec2(x, y));
        drink.addImage(new BodyImage("data/Energydrink.png", 3.5f));

        drink.addCollisionListener(e -> {
            boolean collected = false;
            if (e.getOtherBody() instanceof Scott) {
                ((Scott) e.getOtherBody()).activateSpeedBoost();
                collected = true;
            } else if (e.getOtherBody() instanceof EvilEx) {
                ((EvilEx) e.getOtherBody()).activateSpeedBoost();
                collected = true;
            }

            if (collected) {
                if (itemSound != null) itemSound.play(); // Uses shared sound!
                drink.destroy();
                Timer respawnTimer = new Timer(20000, evt -> {
                    makeEnergyDrink(x, y);
                    ((Timer)evt.getSource()).stop();
                });
                respawnTimer.start();
            }
        });
    }

    private void makeSuitcaseCollectible(float x, float y) {
        StaticBody suitcase = new StaticBody(this, new BoxShape(1.2f, 1.0f));
        suitcase.setPosition(new Vec2(x, y));
        suitcase.addImage(new BodyImage("data/briefcase.png", 3.5f));

        suitcase.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                ((Scott) e.getOtherBody()).addTrap();
                if (itemSound != null) itemSound.play(); // Uses shared sound!
                suitcase.destroy();

                Timer respawnTimer = new Timer(20000, evt -> {
                    makeSuitcaseCollectible(x, y);
                    ((Timer)evt.getSource()).stop();
                });
                respawnTimer.start();
            }
        });
    }

    // Called when the player presses E to drop the trap they collected
    public void placeSuitcaseTrap(Vec2 pos) {
        StaticBody trap = new StaticBody(this, new BoxShape(1.2f, 1.0f));
        trap.setPosition(pos);
        trap.addImage(new BodyImage("data/briefcase.png", 3.5f));

        trap.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof EvilEx) {
                System.out.println("Gideon hit the trap!");
                damageGideon();
                trap.destroy();
            }
        });
    }

    public int getKeysCollected() { return keysCollected; }
    public float getGideonRespawnTimer() { return gideonRespawnTimer; }

    @Override
    public boolean isComplete() {
        return keysCollected >= KEYS_NEEDED && getScott().getLives() > 0;
    }

    @Override
    public Image getBackgroundImage() { return bgImage; }

    @Override
    public float getTimeRemaining() { return -1; }

    @Override
    public String getLevelName() { return "Level 2: The Streets"; }
}