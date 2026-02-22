package game;

import characters.EvilEx;
import characters.Scott;
import inputs.Controller;
import levels.Level1;
import views.GameView;
import javax.swing.JFrame;

public class Game {
    private Level1 level;
    private GameView view;
    private Controller controller;
    private CameraTracker tracker;

    public Game() {
        //Create the physics world for the level
        level = new Level1();

        // I started with a zoomed-out view so both players have an idea of what level they will face..
        view = new GameView(level, 800, 600);
        view.setZoom(20);

        // 3. You now connect the controller to the characters
        Scott scott = level.getScott();
        EvilEx evilEx = level.getEvilEx();
        controller = new Controller(scott, evilEx, level);
        view.addKeyListener(controller);

        // This ensures the game window captures keyboard inputs when clicked.
        view.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                view.requestFocus();
            }
        });

        // We are setting up camera logic.
        // The tracker needs access to both players to keep them in the frame.
        tracker = new CameraTracker(view, scott, evilEx);
        // Tell the level to run this tracker every frame
        level.addStepListener(tracker);



        // This is to create the Java Window.
        final JFrame frame = new JFrame("Scott Pilgrim Tag");
        frame.add(view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        // Start the actual game.
        level.start();
        view.requestFocus();

        //view.setGridResolution(1);
    }

    public static void main(String[] args) {
        new Game();
    }
}