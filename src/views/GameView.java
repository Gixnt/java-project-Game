package views;

import city.cs.engine.UserView;
import city.cs.engine.World;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class GameView extends UserView {

    private Image background;

    public GameView(World world, int width, int height) {
        super(world, width, height);
        // background = new ImageIcon("data/snowy_background.png").getImage();
    }

    @Override
    protected void paintBackground(Graphics2D g) {

        // g.drawImage(background, 0, 0, this);
    }

    @Override
    protected void paintForeground(Graphics2D g) {

        g.setColor(Color.BLACK);
        g.drawString("Player 1: WASD | Player 2: Arrows", 20, 30);
        g.drawString("Scott Lives: 3", 20, 50);
    }
}