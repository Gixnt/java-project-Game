package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import game.TagListener;
import org.jbox2d.common.Vec2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DISCLAIMER: Most of the code in this file is mainly with the assist of AI, as this was the only file
 I actually had trouble and spent most time trying to do with placing platforms/objects and making things work.
 I have added a lot of comments here to also explain what's going on and based on how I understood the code.
 AI has helped me design the level and ensure platforms were able to move, teleporters worked, and jump pads worked.

 */
public class Level1 extends GameLevel {

    // Constants used to place objects in the level in a consistent way
    public static final float FLOOR_Y = -15f;
    public static final float WALL_X = 45f;
    public static final float PLAYER_SPAWN_X = 6f;  // horizontal offset for initial spawn positions

    // List of safe teleport destinations. When a player touches a teleporter,
    // they will be moved to one of these Vec2 positions. We store safe spawn
    // coordinates, not the teleporter circle center, so the player doesn't
    // get teleported into walls.
    private List<Vec2> teleportDestinations = new ArrayList<>();

    // Throttle teleporting so a player cannot be teleported repeatedly in a
    // short time. Stores the last time a teleport happened.
    private long lastTeleportTime = 0;

    // Accumulates the time passed across steps.
    private float timeAccumulator = 0f;

    public Level1() {
        super();


        // Create the ground and a small center divider

        // Ground is a wide static box. We place it slightly below FLOOR_Y, so
        // the top surface sits at FLOOR_Y.
        Shape groundShape = new BoxShape(WALL_X, 1f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(0, FLOOR_Y - 1f));
        ground.setFillColor(new Color(30, 30, 30));
        // High friction so characters don't slide across the main ground unrealistically.
        new SolidFixture(ground, groundShape).setFriction(20f);

        // Small center divider, a visual/physical barrier in the middle of the arena.
        makeBlock(0, FLOOR_Y + 2f, 0.5f, 3f);


        //Inner zone: spawn players and add barriers/roof blocks

        // Create the player and enemy characters and place them on the left/right
        // of the center divider. GameLevel stores references to these so other
        // systems can access them.
        Scott scott = new Scott(this);
        scott.setPosition(new Vec2(-PLAYER_SPAWN_X, FLOOR_Y + 3f));
        setScott(scott);

        EvilEx ex = new EvilEx(this);
        ex.setPosition(new Vec2(PLAYER_SPAWN_X, FLOOR_Y + 3f));
        setEvilEx(ex);

        // Tall barrier blocks between the player area and the teleporters.
        // These become part of the level geometry and prevent players from easily jumping to the teleporters
        makeBlock(-11f, FLOOR_Y + 3f, 0.5f, 4f);
        makeBlock(11f, FLOOR_Y + 3f, 0.5f, 4f);

        // Roof blocks above teleporters to create a small enclosed area.
        makeBlock(-13.5f, FLOOR_Y + 7.5f, 3f, 0.5f);
        makeBlock(13.5f, FLOOR_Y + 7.5f, 3f, 0.5f);

        //Teleporters - circles the player can touch to be teleported

        // Each teleporter also records a safe destination coordinate which is
        // added to `teleportDestinations` so teleportation never drops a player
        // inside geometry.
        makeTeleporter(-15f, FLOOR_Y + 1.5f, -15f, FLOOR_Y + 3.5f);
        makeTeleporter(15f, FLOOR_Y + 1.5f, 15f, FLOOR_Y + 3.5f);
        makeTeleporter(-40f, FLOOR_Y + 24f, -40f, FLOOR_Y + 25.5f);
        makeTeleporter(40f, FLOOR_Y + 24f, 40f, FLOOR_Y + 25.5f);


        //Cup structures, small containers at the edges of the level


        makeSketchCupWalls(-40f, FLOOR_Y + 22f);
        makeSketchCupWalls(40f, FLOOR_Y + 22f);


        //Middle zone: platforms and bounce/jump pads

        // A platform with a jump pad on the left
        makePlatform(-24f, FLOOR_Y + 7f, 3f, Color.GRAY);
        makeBouncePad(-24f, FLOOR_Y + 7.6f);

        // A block with a bounce pad on the right
        makeBlock(24f, FLOOR_Y + 1f, 2.5f, 1f);
        makeBouncePad(24f, FLOOR_Y + 2.3f);


        //Outer zone: vertical moving platforms

        // These platforms move up and down and will carry a player with them.
        makeVerticalMovingPlatform(-35f, FLOOR_Y + 9f, 3f, 5f);
        makeVerticalMovingPlatform(35f, FLOOR_Y + 9f, 3f, 5f);


        //Center sky structure: platforms and moving elements above the arena

        makePlatform(0f, FLOOR_Y + 15f, 8f, new Color(139, 69, 19));
        makePlatform(-9f, FLOOR_Y + 22f, 3f, Color.GRAY);
        makePlatform(9f, FLOOR_Y + 22f, 3f, Color.GRAY);

        // Moving platforms near the top.
        makeVerticalMovingPlatform(0f, FLOOR_Y + 28f, 3f, 4f);
        makeHorizontalMovingPlatform(-14f, FLOOR_Y + 30f, 2.5f, 4f);
        makeHorizontalMovingPlatform(14f, FLOOR_Y + 30f, 2.5f, 4f);


        //Gameplay wiring: tag collisions and pre-step updates

        // TagListener handles interactions between Scott and EvilEx, for
        // example, checking when Scott tags the enemy or vice versa.
        scott.addCollisionListener(new TagListener(scott, ex));

        // Add a step listener that increments the time each frame
        // and also wraps players horizontally when they go past the left/right
        // world bounds. Wrapping means the player appears on the opposite side.
        addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {
                // Keep track of the total time passed; used by moving platforms
                timeAccumulator += stepEvent.getStep();
                // If a player walks off the right edge, teleport them to the left
                // and vice versa. This keeps the gameplay area continuous.
                wrapPlayer(getScott());
                wrapPlayer(getEvilEx());
            }
            @Override
            public void postStep(StepEvent stepEvent) {}
        });
    }



    /*
     * If the player crosses the horizontal boundary WALL_X, move them to the
     * opposite side so the level wraps horizontally.
     */
    private void wrapPlayer(Walker player) {
        if (player == null) return;
        Vec2 pos = player.getPosition();
        if (pos.x > WALL_X) player.setPosition(new Vec2(-WALL_X + 1f, pos.y));
        else if (pos.x < -WALL_X) player.setPosition(new Vec2(WALL_X - 1f, pos.y));
    }

    /*
     Create a teleporter visual object and record a
     safe spawn coordinate (safeSpawnX, safeSpawnY) that players will be
     moved to when they touch the teleporter.

     The teleporter registers a collision listener that only acts when a
     player or enemy touches it, and at least 2 seconds have passed
     since the last teleport. This prevents immediate repeat teleports.
     */
    private void makeTeleporter(float x, float y, float safeSpawnX, float safeSpawnY) {
        StaticBody portal = new StaticBody(this, new CircleShape(1.5f));
        portal.setPosition(new Vec2(x, y));
        portal.setFillColor(new Color(0, 255, 255, 100));

        // Store a safe destination to choose from when teleporting a player.
        teleportDestinations.add(new Vec2(safeSpawnX, safeSpawnY));

        portal.addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                // Only teleport Walkers and only if enough time has passed
                if (e.getOtherBody() instanceof Walker && System.currentTimeMillis() - lastTeleportTime > 2000) {
                    teleportPlayerRandomly((Walker) e.getOtherBody());
                }
            }
        });
    }

    /*
     Picks a random safe teleport destination and moves the player there.
     Ensures the chosen destination is not too close to the player's current
     position and gives up after several attempts.
     After teleporting we clear the player's velocity and record the time of
     the teleport so we can throttle subsequent teleports.
     */
    private void teleportPlayerRandomly(Walker player) {
        if (teleportDestinations.isEmpty()) return;
        Random rand = new Random();
        int index = rand.nextInt(teleportDestinations.size());
        Vec2 dest = teleportDestinations.get(index);

        int attempts = 0;
        // If we accidentally picked a destination too close to the player,
        // try a few more times to pick a farther one.
        while (dest.sub(player.getPosition()).length() < 5f && attempts < 10) {
            index = rand.nextInt(teleportDestinations.size());
            dest = teleportDestinations.get(index);
            attempts++;
        }

        // Move the player instantly to the chosen safe position and stop movement
        player.setPosition(dest);
        player.setLinearVelocity(new Vec2(0, 0));
        lastTeleportTime = System.currentTimeMillis();
    }


    private void makeSketchCupWalls(float x, float y) {
        float w = 8f;
        float h = 4f;
        float thickness = 0.2f;

        makeBlock(x, y, w/2, thickness);
        makeBlock(x - w/2, y + h/2, thickness, h/2);
        makeBlock(x + w/2, y + h/2, thickness, h/2);
    }


    private void makePlatform(float x, float y, float width, Color color) {
        Shape shape = new BoxShape(width, 0.3f);
        StaticBody p = new StaticBody(this, shape);
        p.setPosition(new Vec2(x, y));
        p.setFillColor(color);
        new SolidFixture(p, shape).setFriction(3f);
    }


     //Create a rectangular static block used for the platforms.

    private void makeBlock(float x, float y, float w, float h) {
        Shape shape = new BoxShape(w, h);
        StaticBody b = new StaticBody(this, shape);
        b.setPosition(new Vec2(x, y));
        b.setFillColor(Color.DARK_GRAY);
        new SolidFixture(b, shape).setFriction(3f);
    }


     //Create a bounce pad. When a Walker touches this pad, they are launched in the air.

    private void makeBouncePad(float x, float y) {
        StaticBody pad = new StaticBody(this, new BoxShape(1.2f, 0.2f));
        pad.setPosition(new Vec2(x, y));
        pad.setFillColor(new Color(255, 105, 180));
        pad.addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                if (e.getOtherBody() instanceof Walker) {
                    // Give the walker a strong upward velocity to simulate a jump pad
                    ((Walker) e.getOtherBody()).setLinearVelocity(new Vec2(0, 35f));
                }
            }
        });
    }




    private void makeHorizontalMovingPlatform(final float startX, final float startY, float width, final float range) {
        Shape shape = new BoxShape(width, 0.25f);
        final DynamicBody platform = new DynamicBody(this, shape);
        platform.setPosition(new Vec2(startX, startY));
        platform.setFillColor(new Color(255, 140, 0)); // Orange
        platform.setGravityScale(0);

        SolidFixture fix = new SolidFixture(platform, shape);
        // High friction so players standing on the platform move with it.
        fix.setFriction(15f);

        addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {
                float step = stepEvent.getStep();
                if (step > 0) {
                    float targetX = startX + (float)Math.sin(timeAccumulator * 2) * range;

                    float vx = (float)Math.cos(timeAccumulator * 2) * 2 * range;

                    // Force the exact position to avoid the platform being pushed
                    // by collisions. Then set a linear velocity for friction.
                    platform.setPosition(new Vec2(targetX, startY));
                    platform.setLinearVelocity(new Vec2(vx, 0));
                }
            }
            @Override
            public void postStep(StepEvent stepEvent) {
                // Keep the platform from rotating due to collisions
                platform.setAngle(0);
                platform.setAngularVelocity(0);
            }
        });
    }


    //Create a vertical moving platform that moves up and down.


    private void makeVerticalMovingPlatform(final float startX, final float startY, float width, final float range) {
        Shape shape = new BoxShape(width, 0.4f);
        final DynamicBody platform = new DynamicBody(this, shape);
        platform.setPosition(new Vec2(startX, startY));
        platform.setFillColor(new Color(50, 205, 50)); // Green
        platform.setGravityScale(0);

        SolidFixture fix = new SolidFixture(platform, shape);
        fix.setFriction(15f);

        addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {
                float step = stepEvent.getStep();
                if (step > 0) {

                    float targetY = startY + (float)Math.sin(timeAccumulator * 2) * range;
                    float vy = (float)Math.cos(timeAccumulator * 2) * 2 * range;

                    /* Force vertical position and set velocity so players ride
                    the platform smoothly.*/
                    platform.setPosition(new Vec2(startX, targetY));
                    platform.setLinearVelocity(new Vec2(0, vy));
                }
            }
            @Override
            public void postStep(StepEvent stepEvent) {
                platform.setAngle(0);
                platform.setAngularVelocity(0);
            }
        });
    }

    @Override
    public boolean isComplete() { return getScott().getLives() <= 0; }
}