package game;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
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
        // Check if the collision is with the EvilEx
        if (e.getOtherBody() instanceof EvilEx) {
            scott.decrementLives();

            // "Respawn" them apart so they don't instantly collide again
            scott.setPosition(new Vec2(-10, -10));
            evilEx.setPosition(new Vec2(10, -10));
        }
    }
}