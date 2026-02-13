package inputs;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.Walker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private static final float JUMP_SPEED = 14;
    private static final float WALK_SPEED = 6;

    private Scott scott;
    private EvilEx evilEx;

    public Controller(Scott scott, EvilEx evilEx) {
        this.scott = scott;
        this.evilEx = evilEx;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // --- Player 1 (Scott): WASD ---
        if (code == KeyEvent.VK_W) {
            scott.jump(JUMP_SPEED);
        } else if (code == KeyEvent.VK_A) {
            scott.startWalking(-WALK_SPEED);
        } else if (code == KeyEvent.VK_D) {
            scott.startWalking(WALK_SPEED);
        }

        // --- Player 2 (Evil Ex): Arrow Keys ---
        if (code == KeyEvent.VK_UP) {
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


        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
            scott.stopWalking();
        }


        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
            evilEx.stopWalking();
        }
    }


    public void updateBodies(Scott s, EvilEx e) {
        this.scott = s;
        this.evilEx = e;
    }
}