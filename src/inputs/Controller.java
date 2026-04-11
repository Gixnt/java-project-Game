package inputs;

import characters.EvilEx;
import characters.Scott;
import levels.GameLevel;
import org.jbox2d.common.Vec2;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//I put both players' controls in here so they can play on the same keyboard.
public class Controller extends KeyAdapter {
    private Scott scott;
    private EvilEx evilEx;
    private GameLevel level;
    private final game.Game game;

    public Controller(Scott scott, EvilEx evilEx, GameLevel level, game.Game game) {
        this.scott = scott;
        this.evilEx = evilEx;
        this.level = level;
        this.game = game;
    }

    public void updatePlayers(Scott newScott, EvilEx newEvilEx, GameLevel newLevel) {
        this.scott = newScott;
        this.evilEx = newEvilEx;
        this.level = newLevel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        int location = e.getKeyLocation();

        if (code == KeyEvent.VK_ESCAPE) { if (game != null) game.showLevelSelect(); return; }

        if (scott.getLives() <= 0) {
            if (code == KeyEvent.VK_R) {
                if (game != null) game.restartCurrentLevel();
            }
            return;
        }

        if (code == KeyEvent.VK_E && scott.getTraps() > 0 && level instanceof levels.Level2) {
            scott.useTrap();
            ((levels.Level2) level).placeSuitcaseTrap(scott.getPosition());
        }

        // Check if we are in Level 3 to enable fighting controls
        boolean isLevel3 = level instanceof levels.Level3;

        // Scott Pilgrim Movement
        // Movement is always allowed
        if (code == KeyEvent.VK_W) scott.jump(scott.getJumpSpeed());
        else if (code == KeyEvent.VK_A) scott.startWalking(-scott.getWalkSpeed());
        else if (code == KeyEvent.VK_D) scott.startWalking(scott.getWalkSpeed());

        // fighting only allowed level 3 for scott
        if (isLevel3) {
            if (code == KeyEvent.VK_SPACE) scott.shoot(level);

            if (code == KeyEvent.VK_SHIFT && location == KeyEvent.KEY_LOCATION_LEFT) {
                scott.setBlocking(true);
            }

            if (code == KeyEvent.VK_F) {
                if (scott.canKick()) {
                    scott.kick();
                    float diffX = evilEx.getPosition().x - scott.getPosition().x;
                    boolean rightDirection = (scott.isFacingRight() && diffX > 0) || (!scott.isFacingRight() && diffX < 0);
                    if (rightDirection && scott.getPosition().sub(evilEx.getPosition()).length() < 3.5f) {
                        evilEx.takeDamage(1);
                    }
                }
            }
        }

        //Gideon Controls
        if (code == KeyEvent.VK_UP) evilEx.jump(evilEx.getJumpSpeed());
        else if (code == KeyEvent.VK_LEFT) evilEx.startWalking(-evilEx.getWalkSpeed());
        else if (code == KeyEvent.VK_RIGHT) evilEx.startWalking(evilEx.getWalkSpeed());

        // fighting only allowed level 3 for gideon
        if (isLevel3) {
            if (code == KeyEvent.VK_0 || code == KeyEvent.VK_NUMPAD0) {
                evilEx.triggerSmash();
            }

            if (code == KeyEvent.VK_ENTER) {
                evilEx.triggerEnrage();
            }

            if (code == KeyEvent.VK_SHIFT && location == KeyEvent.KEY_LOCATION_RIGHT) {
                if (evilEx.canSlash()) {
                    evilEx.slash();
                    float diffX = scott.getPosition().x - evilEx.getPosition().x;
                    boolean rightDirection = (evilEx.isFacingRight() && diffX > 0) || (!evilEx.isFacingRight() && diffX < 0);
                    if (rightDirection && evilEx.getPosition().sub(scott.getPosition()).length() < 4.0f) {
                        int dmg = evilEx.isEnraged() ? 2 : 1;
                        scott.takeDamage(dmg);
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        int location = e.getKeyLocation();

        if (code == KeyEvent.VK_SHIFT && location == KeyEvent.KEY_LOCATION_LEFT) scott.setBlocking(false);
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) scott.stopWalking();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) evilEx.stopWalking();
    }
}