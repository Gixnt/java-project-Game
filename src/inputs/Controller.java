package inputs;

import characters.EvilEx;
import characters.Scott;
import levels.GameLevel;
import org.jbox2d.common.Vec2;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    // Adjusting these values changes how the game feels.
    private static final float JUMP_SPEED = 20;
    private static final float WALK_SPEED = 8;

    private Scott scott;
    private EvilEx evilEx;
    private GameLevel level; //This will be used later for milestone 2.

    public Controller(Scott scott, EvilEx evilEx, GameLevel level) {
        this.scott = scott;
        this.evilEx = evilEx;
        this.level = level;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // if Scott has no lives, I disable all keys except 'R' for restart
        if (scott.getLives() <= 0) {

            if (code == KeyEvent.VK_R) {
                scott.resetLives();
                scott.setPosition(new org.jbox2d.common.Vec2(-10, -8));
                evilEx.setPosition(new org.jbox2d.common.Vec2(10, -8));

                scott.setLinearVelocity(new Vec2(0,0));
                evilEx.setLinearVelocity(new Vec2(0,0));
            }
            return; // Stop other controls from working
        }

        // Scott Pilgrim
        if (code == KeyEvent.VK_W) {
            // I check the vertical velocity to ensure Scott is on the ground before jumping.
            // If I don't check this, he can fly by spamming W *I have used AI to assist on this.
            //if (scott.getLinearVelocity().y < 6f)
            scott.jump(JUMP_SPEED);
        } else if (code == KeyEvent.VK_A) {
            scott.startWalking(-WALK_SPEED);
        } else if (code == KeyEvent.VK_D) {
            scott.startWalking(WALK_SPEED);
        }

        // Evil Ex
        if (code == KeyEvent.VK_UP) {
            //Essentially doing the same thing over here.
            //if (evilEx.getLinearVelocity().y < 6f)
            evilEx.jump(JUMP_SPEED);
        } else if (code == KeyEvent.VK_LEFT) {
            evilEx.startWalking(-WALK_SPEED);
        } else if (code == KeyEvent.VK_RIGHT) {
            evilEx.startWalking(WALK_SPEED);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        // We have to stop walking when the key is released, otherwise they slide forever.
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) scott.stopWalking();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) evilEx.stopWalking();
    }
}