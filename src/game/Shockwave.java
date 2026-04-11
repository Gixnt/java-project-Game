package game;

import characters.Scott;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Shockwave extends DynamicBody {
    public Shockwave(World world, boolean movingRight) {
        super(world, new BoxShape(1f, 0.5f));
        setGravityScale(0);

        // Use the laser bullet image
        addImage(new BodyImage("data/LaserBullet.png", 1.5f));

        float speed = movingRight ? 20f : -20f;
        setLinearVelocity(new Vec2(speed, 0));

        this.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Scott) {
                Scott s = (Scott) e.getOtherBody();
                if (!s.isBlocking()) s.takeDamage(2); // Block saves him!
                this.destroy();
            } else if (e.getOtherBody() instanceof StaticBody) {
                this.destroy(); // Destroy on walls
            }
        });
    }
}