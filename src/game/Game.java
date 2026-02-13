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

    public Game() {

        level = new Level1();


        view = new GameView(level, 800, 600);


        Scott scott = level.getScott();
        EvilEx evilEx = level.getEvilEx();
        controller = new Controller(scott, evilEx);
        view.addKeyListener(controller);


        final JFrame frame = new JFrame("Scott Pilgrim Tag");
        frame.add(view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);


        level.start();


        view.requestFocus();
    }

    public static void main(String[] args) {
        new Game();
    }
}