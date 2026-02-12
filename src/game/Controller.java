package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.jbox2d.common.Vec2;

/**
 * Handles keyboard input to control the Knight.
 */
public class Controller extends KeyAdapter {

    private static final float WALKING_SPEED = 6;
    private static final float JUMPING_SPEED = 12;

    private Knight knight;

    public Controller(Knight knight) {
        this.knight = knight;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Movement: A/D or Left/Right arrows
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            knight.startWalking(-WALKING_SPEED);
        }
        else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            knight.startWalking(WALKING_SPEED);
        }
        // Jumping: Space or W
        else if (code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE) {
            // Only jump if we have vertical velocity close to 0 (touching ground)
            // This is a simple check, can be improved later
            if (Math.abs(knight.getLinearVelocity().y) < 0.01f) {
                knight.jump(JUMPING_SPEED);
            }
        }
        // Heal: E key
        else if (code == KeyEvent.VK_E) {
            knight.heal();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            knight.stopWalking();
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            knight.stopWalking();
        }
    }
}