package views;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.UserView;
import levels.GameLevel;
import javax.swing.*;
import java.awt.*;


/* This class handles all the drawing on the screen the HUD, health bars, game over screens
 I got some help from AI to figure out how to structure things and aiding in certain things for example
 hrinking the health bars and perfectly centering text on the screen.
 */
public class GameView extends UserView {

    private GameLevel level;
    private String overlayTitle = "";
    private String overlaySubtitle = "";

    public GameView(GameLevel world, int width, int height) {
        super(world, width, height);
        this.level = world;
    }

    public void setLevel(GameLevel newLevel) { this.level = newLevel; }


    // These methods let me trigger big text on the screen like GO or SCOTT WON
    public void setOverlayMessage(String title, String subtitle) {
        this.overlayTitle = title;
        this.overlaySubtitle = subtitle;
        repaint();
    }

    public void clearOverlayMessage() {
        this.overlayTitle = "";
        this.overlaySubtitle = "";
        repaint();
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        Image bg = level.getBackgroundImage();
        if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        Scott scott = level.getScott();
        EvilEx evilEx = level.getEvilEx();


        // HUD for level 1 and level 2

        //I asked AI for help to draw different UI layouts when the player reaches a different level.
        if (!(level instanceof levels.Level3)) {
            int hudHeight = (level instanceof levels.Level2) ? 145 : 100;
            g.setColor(new Color(20, 20, 20, 200));
            g.fillRoundRect(10, 10, 230, hudHeight, 15, 15);
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke(2));
            g.drawRoundRect(10, 10, 230, hudHeight, 15, 15);

            g.setFont(new Font("SansSerif", Font.BOLD, 16)); g.setColor(Color.WHITE);
            g.drawString("SCOTT PILGRIM", 20, 30);
            g.setFont(new Font("Dialog", Font.PLAIN, 10)); g.setColor(new Color(200, 200, 200));
            g.drawString("Scott (WASD) vs EvilEx (ARROWS)", 20, 45);

            g.setFont(new Font("SansSerif", Font.BOLD, 12)); g.setColor(scott.isInvincible() ? Color.CYAN : Color.YELLOW);
            g.drawString("SCOTT", 20, 70);
            g.setColor(Color.BLACK); g.fillRect(75, 60, 150, 14);
            g.setColor(Color.WHITE); g.drawRect(75, 60, 150, 14);
            g.setColor(new Color(220, 50, 50));

            // Draws small blocks for each life the player has
            int barWidth = 146 / 3;
            for (int i = 0; i < scott.getLives(); i++) g.fillRect(77 + (i * barWidth), 62, barWidth - 2, 10);

            if (evilEx != null) {
                g.setColor(Color.PINK); g.drawString("GIDEON", 20, 90);
                g.setColor(Color.BLACK); g.fillRect(75, 80, 150, 14);
                g.setColor(Color.WHITE); g.drawRect(75, 80, 150, 14);
                g.setColor(new Color(255, 50, 150));
                for (int i = 0; i < evilEx.getLives(); i++) g.fillRect(77 + (i * barWidth), 82, barWidth - 2, 10);
            }
            if (level instanceof levels.Level2) {
                g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString("TRAPS: " + scott.getTraps() + " [Press 'E' to drop]", 20, 130);
            }
        }


        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        String lvlName = level.getLevelName();
        int nameWidth = g.getFontMetrics().stringWidth(lvlName);
        String objectiveText = (level instanceof levels.Level2) ? "Objective: Collect all 3 keys (" + ((levels.Level2)level).getKeysCollected() + "/3)" : "";
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        int objWidth = objectiveText.isEmpty() ? 0 : g.getFontMetrics().stringWidth(objectiveText);

        int boxWidth = Math.max(140, Math.max(nameWidth, objWidth) + 30);
        int boxHeight = (level instanceof levels.Level2) ? 65 : 55;
        if (level instanceof levels.Level3) boxHeight = 35;
        int boxX = getWidth() / 2 - boxWidth / 2;

        g.setColor(new Color(0, 0, 0, 180)); g.fillRoundRect(boxX, 10, boxWidth, boxHeight, 15, 15);
        g.setColor(new Color(200, 200, 200)); g.drawRoundRect(boxX, 10, boxWidth, boxHeight, 15, 15);
        g.setColor(Color.CYAN); g.drawString(lvlName, getWidth() / 2 - nameWidth / 2, (level instanceof levels.Level3) ? 33 : 28);

        if (level instanceof levels.Level2) {
            g.setFont(new Font("SansSerif", Font.BOLD, 14)); g.setColor(Color.YELLOW);
            g.drawString(objectiveText, getWidth() / 2 - objWidth / 2, 50);

            levels.Level2 l2 = (levels.Level2) level;
            if (l2.getGideonRespawnTimer() > 0) {
                g.setColor(Color.RED); g.setFont(new Font("SansSerif", Font.BOLD, 22));
                String msg = "Gideon died! Respawning in " + (int)Math.ceil(l2.getGideonRespawnTimer()) + " seconds";
                g.drawString(msg, getWidth() / 2 - g.getFontMetrics().stringWidth(msg) / 2, 95);
            }
        } else if (!(level instanceof levels.Level3)) {
            float time = level.getTimeRemaining();
            if (time >= 0) {
                int seconds = (int) time;
                String timeStr = "TIME: 00:" + String.format("%02d", seconds);
                g.setFont(new Font("Monospaced", Font.BOLD, 18));
                g.setColor(seconds <= 10 ? Color.RED : Color.WHITE);
                g.drawString(timeStr, getWidth() / 2 - g.getFontMetrics().stringWidth(timeStr) / 2, 48);
            }
        }

        //Level 3 Split HUD
        if (level instanceof levels.Level3) {
            int maxBossHP = 50;
            int sideBoxWidth = 260;
            int bossBarHeight = 20;

            // Scotts HUD
            int sX = 15;
            int sY = 15;

            g.setColor(new Color(20, 20, 20, 200));
            g.fillRoundRect(sX, sY, sideBoxWidth, 110, 15, 15);
            g.setColor(new Color(255, 165, 0)); // Orange
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(sX, sY, sideBoxWidth, 110, 15, 15);

            g.setFont(new Font("SansSerif", Font.BOLD, 14)); g.setColor(Color.WHITE);
            g.drawString("SCOTT PILGRIM", sX + 15, sY + 22);

            g.setColor(Color.BLACK); g.fillRect(sX + 15, sY + 30, sideBoxWidth - 30, bossBarHeight);

            /* AI helped me with this math calculation.  It calculates the percentage of health remaining
             and scales the colored rectangle to match the background box perfectly.
             */
            g.setColor(new Color(255, 165, 0));
            int sHpWidth = (int)(((double)scott.getLives() / maxBossHP) * (sideBoxWidth - 30));
            g.fillRect(sX + 17, sY + 32, Math.max(sHpWidth - 4, 0), bossBarHeight - 4);
            g.setColor(Color.WHITE); g.drawRect(sX + 15, sY + 30, sideBoxWidth - 30, bossBarHeight);

            if (scott.hasGun()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 12)); g.setColor(Color.CYAN);
                g.drawString("AMMO: " + scott.getAmmo(), sX + 15, sY + 65);
            } else {
                g.setFont(new Font("SansSerif", Font.BOLD, 12)); g.setColor(Color.GRAY);
                g.drawString("FIND THE WEAPON", sX + 15, sY + 65);
            }

            g.setFont(new Font("Dialog", Font.PLAIN, 10)); g.setColor(new Color(200, 200, 200));
            g.drawString("Controls: [WASD] Move   [SPACE] Shoot", sX + 15, sY + 85);
            g.drawString("          [L-SHIFT] Block   [F] Kick", sX + 15, sY + 100);

            //GIDEON'S HUD
            int gX = getWidth() - sideBoxWidth - 15;
            int gY = 15;

            g.setColor(new Color(20, 20, 20, 200));
            g.fillRoundRect(gX, gY, sideBoxWidth, 110, 15, 15);
            g.setColor(new Color(220, 20, 60)); // Crimson
            g.drawRoundRect(gX, gY, sideBoxWidth, 110, 15, 15);

            g.setFont(new Font("SansSerif", Font.BOLD, 14)); g.setColor(Color.WHITE);
            String gName = "GIDEON GRAVES";
            g.drawString(gName, gX + sideBoxWidth - g.getFontMetrics().stringWidth(gName) - 15, gY + 22);

            g.setColor(Color.BLACK); g.fillRect(gX + 15, gY + 30, sideBoxWidth - 30, bossBarHeight);
            g.setColor(new Color(220, 20, 60));
            int gHpWidth = (int)(((double)evilEx.getLives() / maxBossHP) * (sideBoxWidth - 30));
            g.fillRect(gX + 17, gY + 32, Math.max(gHpWidth - 4, 0), bossBarHeight - 4);
            g.setColor(Color.WHITE); g.drawRect(gX + 15, gY + 30, sideBoxWidth - 30, bossBarHeight);

            // enrage and smash HUD
            g.setFont(new Font("SansSerif", Font.BOLD, 11));

            if (evilEx.isEnraged()) {
                g.setColor(Color.RED);
                g.drawString("ENRAGED: " + (int)Math.ceil(evilEx.getEnrageTimer()) + "s", gX + 15, gY + 65);
            } else if (evilEx.getEnrageCooldown() > 0) {
                g.setColor(new Color(255, 100, 100)); // Light red / cooling down
                g.drawString("ENRAGE [ENT]: " + (int)Math.ceil(evilEx.getEnrageCooldown()) + "s", gX + 15, gY + 65);
            } else {
                g.setColor(Color.CYAN);
                g.drawString("ENRAGE [ENT]: READY", gX + 15, gY + 65);
            }

            if (evilEx.getSmashCooldown() > 0) {
                g.setColor(new Color(255, 100, 100));
                g.drawString("SMASH [0]: " + (int)Math.ceil(evilEx.getSmashCooldown()) + "s", gX + 145, gY + 65);
            } else {
                g.setColor(Color.CYAN);
                g.drawString("SMASH [0]: READY", gX + 145, gY + 65);
            }

            g.setFont(new Font("Dialog", Font.PLAIN, 10)); g.setColor(new Color(200, 200, 200));
            g.drawString("Controls: [ARROWS] Move  [R-SHIFT] Slash", gX + 15, gY + 85);
            g.drawString("          [0] Smash      [ENTER] Enrage", gX + 15, gY + 100);
        }

        // Invincibility Alert
        if (scott.isInvincible()) {
            g.setColor(new Color(255, 215, 0));
            g.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
            g.drawString("SCOTT IS INVINCIBLE!", getWidth() / 2 - g.getFontMetrics().stringWidth("SCOTT IS INVINCIBLE!") / 2, 135);
        }

        // -ESC menu
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(255, 255, 255, 200));
        if (level instanceof levels.Level3) {
            g.drawString("[ESC] Menu / Levels", getWidth() - 160, getHeight() - 20);
        } else {
            g.drawString("[ESC] Menu / Levels", getWidth() - 150, 25);
        }

        /* I struggled a lot trying to get the text to perfectly center on the screen.
         I used AIcto measure the exact width of the string in pixels before drawing it
         */
        if (!overlayTitle.isEmpty()) {
            g.setColor(new Color(0, 0, 0, 190)); g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 55));
            int txtX = getWidth() / 2 - g.getFontMetrics().stringWidth(overlayTitle) / 2;
            int txtY = getHeight() / 2 - 20;

            g.setColor(Color.BLACK); g.drawString(overlayTitle, txtX + 4, txtY + 4);
            g.setColor(Color.YELLOW); g.drawString(overlayTitle, txtX, txtY);

            if (!overlaySubtitle.isEmpty()) {
                g.setFont(new Font("Monospaced", Font.BOLD, 40)); g.setColor(Color.WHITE);
                g.drawString(overlaySubtitle, getWidth() / 2 - g.getFontMetrics().stringWidth(overlaySubtitle) / 2, txtY + 60);
            }
        }

        // Game over screen
        if (scott.getLives() <= 0) {
            g.setColor(new Color(0, 0, 0, 180)); g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font("SansSerif", Font.BOLD, 60)); g.setColor(Color.RED);
            String msg = "SCOTT PILGRIM DIED!";
            g.drawString(msg, getWidth() / 2 - g.getFontMetrics().stringWidth(msg) / 2, getHeight() / 2);
            g.setFont(new Font("SansSerif", Font.BOLD, 25)); g.setColor(Color.WHITE);
            String subMsg = "PRESS 'R' TO RESTART";
            g.drawString(subMsg, getWidth() / 2 - g.getFontMetrics().stringWidth(subMsg) / 2, getHeight() / 2 + 60);
        }
    }
}