package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents the specific game level.
 * Handles population of objects (Platforms, Player, Items).
 */
public class GameLevel extends World {

    private Knight knight;

    public void populate(Game game) {
        // 1. Create the Player
        knight = new Knight(this);
        knight.setPosition(new Vec2(0, -10));

        // 2. Create the Ground (StaticBody)
        Shape groundShape = new BoxShape(30, 0.5f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(0, -11.5f));
        ground.setFillColor(new java.awt.Color(80, 50, 20)); // Brown

        // 3. Create Platforms (Simulating the High Wall of Lothric)
        Shape platformShape = new BoxShape(4, 0.5f);

        StaticBody platform1 = new StaticBody(this, platformShape);
        platform1.setPosition(new Vec2(-8, -4));

        StaticBody platform2 = new StaticBody(this, platformShape);
        platform2.setPosition(new Vec2(8, 2));

        // 4. Create Walls (to keep player on screen)
        Shape wallShape = new BoxShape(0.5f, 15);
        StaticBody leftWall = new StaticBody(this, wallShape);
        leftWall.setPosition(new Vec2(-20, 0));

        StaticBody rightWall = new StaticBody(this, wallShape);
        rightWall.setPosition(new Vec2(20, 0));

        // 5. Create Collectibles (Estus Flasks)
        EstusFlask flask1 = new EstusFlask(this);
        flask1.setPosition(new Vec2(-8, 0)); // On platform 1

        EstusFlask flask2 = new EstusFlask(this);
        flask2.setPosition(new Vec2(8, 6)); // On platform 2

        // 6. Register Collisions
        // We add a listener to the Knight to check if he touches things
        PickupHandler pickup = new PickupHandler(knight);
        knight.addCollisionListener(pickup);
    }

    public Knight getKnight() {
        return knight;
    }
}