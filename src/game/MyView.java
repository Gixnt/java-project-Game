package game;

import city.cs.engine.UserView;
import city.cs.engine.World;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Custom view to draw the HUD (Heads Up Display)
 * on top of the game world.
 */
public class MyView extends UserView {

    private GameLevel level;
    // Load a background image (Uncomment when you have one)
    // private Image background = new ImageIcon("data/background.jpg").getImage();

    public MyView(GameLevel world, int width, int height) {
        super(world, width, height);
        this.level = world;
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        // Standard grey background
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // If you have an image, use this instead:
        // g.drawImage(background, 0, 0, this);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        // This satisfies the "Display a statistic" requirement

        Knight knight = level.getKnight();

        // Draw Health Bar
        g.setColor(Color.RED);
        g.fillRect(20, 30, knight.getHealth() * 2, 20); // Width scales with health
        g.setColor(Color.WHITE);
        g.drawRect(20, 30, 200, 20); // Border
        g.drawString("HP", 25, 45);

        // Draw Estus Flask Count
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("Estus: " + knight.getEstusFlasks(), 20, 80);

        // Controls Hint
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("WASD to Move | E to Heal", 20, 550);
    }
}