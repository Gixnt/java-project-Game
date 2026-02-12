package game;

import city.cs.engine.*;

/**
 * Handles collision events for the Knight.
 * Detects when the Knight touches an Estus Flask.
 */
public class PickupHandler implements CollisionListener {

    private Knight knight;

    public PickupHandler(Knight knight) {
        this.knight = knight;
    }

    @Override
    public void collide(CollisionEvent e) {
        // 'e.getOtherBody()' is the object the Knight collided with

        if (e.getOtherBody() instanceof EstusFlask) {
            // 1. Update the Knight's state
            knight.collectEstus();

            // 2. Remove the flask from the world
            e.getOtherBody().destroy();
        }
    }
}