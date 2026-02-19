package views;

import characters.Scott;
import city.cs.engine.UserView;
import levels.GameLevel;
import javax.swing.*;
import java.awt.*;

public class GameView extends UserView {

    private GameLevel level;
    private Image background;

    public GameView(GameLevel world, int width, int height) {
        super(world, width, height);
        this.level = world;

        //Set the image background for the game.
        background = new ImageIcon("data/ScottPilgrimBackground.jpg").getImage();
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        // HUD Background
        // A cleaner, rounded box at the top left of the screen used AI for a bit of help.
        g.setColor(new Color(20, 20, 20, 200)); // make it dark grey and transparent
        g.fillRoundRect(10, 10, 260, 110, 15, 15);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2)); // Thick Border
        g.drawRoundRect(10, 10, 260, 110, 15, 15);

        // This is for the text info and fonts
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("SCOTT PILGRIM", 25, 40);

        g.setFont(new Font("Dialog", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        g.drawString("Scott (WASD) vs EvilEx (ARROWS)", 25, 60);

        // Lives bar
        Scott scott = level.getScott();

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.YELLOW);
        g.drawString("HP", 25, 95);

        // Draw the empty health bar container
        g.setColor(Color.BLACK);
        g.fillRect(55, 82, 160, 18);
        g.setColor(Color.WHITE);
        g.drawRect(55, 82, 160, 18);

        // Draw the actual lives (Red blocks)
        g.setColor(new Color(220, 50, 50));


        // Each life takes up a chunk of the bar. 3 lives = 3 chunks.
        /* I have used AI for the following code blow as I didn't know how to make a game overlay when
        scott ran out of lives */
        int barWidth = 156 / 3;
        for (int i = 0; i < scott.getLives(); i++) {
            g.fillRect(57 + (i * barWidth), 84, barWidth - 2, 14);
        }

        // Game Over Overlay
        if (scott.getLives() <= 0) {
            // Darken screen
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(new Font("SansSerif", Font.BOLD, 60));
            String msg = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(msg);
            int x = getWidth() / 2 - textWidth / 2;
            int y = getHeight() / 2;

            g.setColor(Color.BLACK);
            g.drawString(msg, x + 4, y + 4);
            g.setColor(Color.RED);
            g.drawString(msg, x, y);


            g.setFont(new Font("SansSerif", Font.BOLD, 25));
            g.setColor(Color.WHITE);
            String subMsg = "Press 'R' to Restart";
            int subWidth = g.getFontMetrics().stringWidth(subMsg);
            g.drawString(subMsg, getWidth() / 2 - subWidth / 2, y + 60);
        }
    }
}