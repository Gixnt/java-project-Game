package characters;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class EvilEx extends Walker {
    private static final Shape shape = new BoxShape(1,3);

    private static final BodyImage image = new BodyImage("data/GideonGraves.png", 9f);

    private boolean grounded = true;

    public EvilEx(World world) {
        super(world,shape);
        addImage(image);
        setGravityScale(3f);

        this.addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                // If the thing EvilEx collides with is below him, he is on the ground
                if (e.getOtherBody().getPosition().y < getPosition().y) {
                    grounded = true;
                }
            }
        });
    }

    // Overriding the default strict jump method to allow jumping off moving platforms
    @Override
    public void jump(float speed) {
        // We also check < 0.01f as a backup failsafe to CityEngine's default behaviour
        if (grounded || Math.abs(getLinearVelocity().y) < 0.01f) {
            setLinearVelocity(new Vec2(getLinearVelocity().x, speed));
            grounded = false; // Cannot jump again until he lands
        }
    }
}


