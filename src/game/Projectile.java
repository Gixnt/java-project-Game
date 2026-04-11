package game;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import java.awt.Color;

public class Projectile extends DynamicBody {
    private static final Shape bulletShape = new CircleShape(0.5f);

    public Projectile(World world) {
        super(world, bulletShape);
        setGravityScale(0); // Shoots straight, no gravity drop

        // Add the laser bullet image
        this.addImage(new BodyImage("data/LaserBullet.png", 1.5f));

        // Collision logic
        this.addCollisionListener(e -> {
            // If it hits Scott, ignore it so he doesn't shoot himself
            if (e.getOtherBody() instanceof Scott) {
                return;
            }

            // If it hits Gideon, damage him
            if (e.getOtherBody() instanceof EvilEx) {
                ((EvilEx) e.getOtherBody()).decrementLives();
            }

            // Destroy the bullet upon hitting a wall, floor, or Gideon
            this.destroy();
        });
    }
}