package game;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import levels.Level1;
import org.jbox2d.common.Vec2;

public class TagListener implements CollisionListener {
    private Scott scott;
    private EvilEx evilEx;

    public TagListener(Scott scott, EvilEx evilEx) {
        this.scott = scott;
        this.evilEx = evilEx;
    }


    @Override
    public void collide(CollisionEvent e) {
        /* I used AI to help me understand 'instanceof'.
           This checks if the thing Scott collided with is actually the EvilEx character.
           If it is, Scott loses a life.
        */
        if (e.getOtherBody() instanceof EvilEx) {
            scott.decrementLives();

            // Respawn them apart so they don't instantly collide again
            scott.setPosition(new Vec2(-10, Level1.FLOOR_Y + 3f));
            evilEx.setPosition(new Vec2(10, Level1.FLOOR_Y + 3f));


            // Stop their momentum
            scott.setLinearVelocity(new Vec2(0,0));
            evilEx.setLinearVelocity(new Vec2(0,0));
        }
    }
}