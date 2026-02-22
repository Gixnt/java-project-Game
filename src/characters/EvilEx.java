package characters;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class EvilEx extends Walker implements StepListener {
    private static final Shape shape = new BoxShape(1,2.6f);

    private static final BodyImage idle = new BodyImage("data/gideonidle.gif", 6f);

    private static final BodyImage walkRight = new BodyImage("data/gideonWalkingRight.gif", 6f);
    private static final BodyImage walkLeft = new BodyImage("data/gideonWalkingLeft.gif", 6f);

    private boolean grounded = true;

    public EvilEx(World world) {
        super(world,shape);
        addImage(idle);
        setGravityScale(3f);

        world.addStepListener(this);

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

    // Called before each physics step. We update which image is shown here so the
    // sprite matches movement in sync with the physics engine.
    @Override
    public void preStep(StepEvent stepEvent) {
        removeAllImages();

        // Simple checks based on movement speed
        if (getLinearVelocity().x < -0.1f) {
            // Walking left
            addImage(walkLeft);
        } else if (getLinearVelocity().x > 0.1f) {
            // Walking right
            addImage(walkRight);
        } else {
            // Standing perfectly still
            addImage(idle);
        }
    }

    // Called after each physics step. Not needed for animations in this class,
    // but required by the StepListener interface. Good place to have just in case for after physics logic.
    @Override
    public void postStep(StepEvent stepEvent) {

    }
}


