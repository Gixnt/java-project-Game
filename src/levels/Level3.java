package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Image;

public class Level3 extends GameLevel {

    private final Image bgImage = new javax.swing.ImageIcon("data/Level3.jpg").getImage();

    public Level3() {
        super();

        // Players spaced for a fair start
        Scott scott = new Scott(this);
        scott.setPosition(new Vec2(-15f, -10f));
        scott.setMaxLives(50);
        setScott(scott);

        EvilEx ex = new EvilEx(this);
        ex.setPosition(new Vec2(15f, -10f));
        ex.setLives(50);
        setEvilEx(ex);

        // Invisible Arena Walls
        StaticBody leftWall = new StaticBody(this, new BoxShape(1f, 40f));
        leftWall.setPosition(new Vec2(-35f, 0f));
        leftWall.setFillColor(new Color(0, 0, 0, 0));

        StaticBody rightWall = new StaticBody(this, new BoxShape(1f, 40f));
        rightWall.setPosition(new Vec2(35f, 0f));
        rightWall.setFillColor(new Color(0, 0, 0, 0));

        //Basic gray floor.
        Shape groundShape = new BoxShape(45f, 1f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(0, -15.5f));
        ground.setFillColor(Color.DARK_GRAY);
        new SolidFixture(ground, groundShape).setFriction(20f);

        //Level layouts.

        // Bottom middle vertical line
        makeVerticalPlank(0f, -11.5f, 0.8f);

        // Far-left small floating platform
        makeHorizontalPlank(-28f, -4f, 1f);

        // Upside-down T on the left
        makeHorizontalPlank(-18f, 5f, 1.2f);
        makeVerticalPlank(-18f, 8.1f, 0.8f);

        // Center platform where the GUN spawns
        makeHorizontalPlank(0f, 0f, 1f);

        // Stepping stone block on the right side
        makeHorizontalPlank(25f, -4f, 1f);

        // Right side middle floating platform
        makeHorizontalPlank(20f, 3f, 1f);

        // Right side upper floating platform
        makeHorizontalPlank(30f, 10f, 1f);

        // Start Spawners
        spawnInitialGun();
    }


     // Here I asked AI for help to so that the collision box perfectly matches the visual size of the plank
    private void makeHorizontalPlank(float x, float y, float scale) {
        float h = 0.5f * scale;
        float w = 4.55f * scale;
        Shape shape = new BoxShape(w, h);
        StaticBody block = new StaticBody(this, shape);
        block.setPosition(new Vec2(x, y));
        block.addImage(new BodyImage("data/Plank.png", h * 10f));
        new SolidFixture(block, shape).setFriction(3f);
    }


    private void makeVerticalPlank(float x, float y, float scale) {
        float h = 3.2f * scale;
        float w = 1.0f * scale;
        Shape shape = new BoxShape(w, h);
        StaticBody block = new StaticBody(this, shape);
        block.setPosition(new Vec2(x, y));
        block.addImage(new BodyImage("data/plankvertical.png", h * 2.8f));
        new SolidFixture(block, shape).setFriction(3f);
    }

    private void spawnInitialGun() {
        StaticBody gun = new StaticBody(this, new BoxShape(1.0f, 0.5f));

        // Placed resting on top of the centered middle platform
        gun.setPosition(new Vec2(0f, 0.75f));
        gun.addImage(new BodyImage("data/gun.png", 2f));

        gun.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                Scott s = (Scott) e.getOtherBody();
                s.setHasGun(true);
                s.addAmmo(10);
                if (itemSound != null) itemSound.play();
                gun.destroy();

                // Starts the ammo cycle ONLY after the gun is picked up
                startAmmoCooldown();
            }
        });
    }

    // Wait 15 seconds before dropping a new magazine so the map doesn't flood with so many.
    private void startAmmoCooldown() {
        Timer ammoTimer = new Timer(15000, e -> {
            spawnMagazine();
            ((Timer)e.getSource()).stop();
        });
        ammoTimer.start();
    }

    //Spawn the magazines in any of the platforms that is placed on the layout.
    private void spawnMagazine() {
        StaticBody mag = new StaticBody(this, new BoxShape(0.8f, 0.8f));

        // All platforms.
        Vec2[] spawnLocations = {
                new Vec2(-28f, -2.7f),
                new Vec2(-21f, 6.5f),
                new Vec2(25f, -2.7f),
                new Vec2(20f, 4.3f),
                new Vec2(28f, 11.5f),
                new Vec2(-20f, -14f),
                new Vec2(20f, -14f)
        };

        int randomIndex = (int) (Math.random() * spawnLocations.length);
        Vec2 spawnPos = spawnLocations[randomIndex];

        mag.setPosition(spawnPos);
        mag.addImage(new BodyImage("data/Magazines.png", 2.5f));

        mag.addCollisionListener(event -> {
            if (event.getOtherBody() instanceof Scott) {
                ((Scott) event.getOtherBody()).addAmmo(10);
                if (itemSound != null) itemSound.play(); // USING SHARED SOUND
                mag.destroy();

                startAmmoCooldown();
            }
        });
    }

    @Override
    public boolean isComplete() {
        return getEvilEx().getLives() <= 0 && getScott().getLives() > 0;
    }

    @Override
    public Image getBackgroundImage() { return bgImage; }

    @Override
    public float getTimeRemaining() {
        return 0;
    }

    @Override
    public String getLevelName() { return "Level 3: Final Battle"; }
}