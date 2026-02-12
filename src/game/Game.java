package game;

import javax.swing.JFrame;

/**
 * The main entry point for the "Cinder's Shadow" game.
 * This handles the window setup and launching the world.
 */
public class Game {

    /** The World in which the bodies move and interact. */
    private GameLevel world;

    /** A graphical view of the world. */
    private MyView view;

    public Game() {
        // 1. Create the world (Level 1)
        world = new GameLevel();
        world.populate(this);

        // 2. Create a view to look into the world
        // 800x600 is the resolution recommended in the PDF
        view = new MyView(world, 800, 600);

        // 3. Create a Java Window (JFrame) to hold the view
        final JFrame frame = new JFrame("Cinder's Shadow - Milestone 1");
        frame.add(view);

        // 4. Enable keyboard controls
        Controller controller = new Controller(world.getKnight());
        frame.addKeyListener(controller);

        // 5. Standard Window settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        // 6. Start the physics engine
        world.start();

        // 7. Focus the window so keyboard works immediately
        frame.requestFocus();
    }

    public static void main(String[] args) {
        new Game();
    }
}