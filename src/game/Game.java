package game;

import characters.EvilEx;
import characters.Scott;
import inputs.Controller;
import levels.Level1;
import levels.Level2;
import levels.Level3;
import levels.GameLevel;
import views.GameView;
import city.cs.engine.SoundClip;

import javax.swing.*;
import java.awt.*;

public class Game {
    private GameLevel level;
    private final GameView view;
    private final Controller controller;
    private final CameraTracker tracker;

    private final JFrame frame;
    private final JPanel mainContainer;
    private final CardLayout cardLayout;
    private int maxUnlockedLevel = 1;

    private boolean isTransitioning = false;


    // The currently playing background music
    private SoundClip currentMusic;
    private SoundClip clickSound;

    public void playBackgroundMusic(String filename, boolean loop) {
        try {
            if (currentMusic != null) {
                currentMusic.stop();
            }

            currentMusic = new SoundClip(filename);
            currentMusic.setVolume(0.3); // Lower volume so sound effects can be heard

            if (loop) {
                currentMusic.loop();
            } else {
                currentMusic.play();
            }
        } catch (Exception e) {
            System.out.println("Error playing music: " + filename + " - " + e);
        }
    }

    private void playClickSound() {
        try {
            if (clickSound != null) {
                clickSound.play();
            }
        } catch (Exception e) {
            System.out.println("Error playing click sound: " + e);
        }
    }

    public Game() {
        try {
            clickSound = new SoundClip("data/Click.wav");
            clickSound.setVolume(0.8);
        } catch (Exception e) {
            System.out.println("Error loading click sound: " + e);
        }

        level = new Level1();
        view = new GameView(level, 800, 600);
        view.setZoom(20);

        Scott scott = level.getScott();
        EvilEx evilEx = level.getEvilEx();

        controller = new Controller(scott, evilEx, level, this);
        view.addKeyListener(controller);

        view.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                view.requestFocusInWindow();
            }
        });

        tracker = new CameraTracker(view, scott, evilEx);
        level.addStepListener(tracker);

        level.addStepListener(new city.cs.engine.StepListener() {
            private boolean isGameOver = false;

            @Override public void preStep(city.cs.engine.StepEvent stepEvent) {}
            @Override public void postStep(city.cs.engine.StepEvent stepEvent) {
                if (level.isComplete() && !isTransitioning) {
                    isTransitioning = true;
                    handleLevelCompletion();
                } else if (level.getScott().getLives() <= 0 && !isGameOver) {
                    isGameOver = true;
                    playBackgroundMusic("data/GameOver.wav", false);
                }
            }
        });

        frame = new JFrame("Scott Pilgrim Tag");
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // CardLayout lets me swap between the menu panels and the actual game screen
        mainContainer.add(createMainMenu(), "MainMenu");
        mainContainer.add(createLevelSelectMenu(), "LevelSelect");
        mainContainer.add(view, "Game");

        frame.add(mainContainer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        cardLayout.show(mainContainer, "MainMenu");
    }

    private void handleLevelCompletion() {
        level.stop();
        playBackgroundMusic("data/LEVELCOMPLETE.wav", false);

        if (level instanceof Level3) {
            view.setOverlayMessage("SCOTT HAS WON!", "THANKS FOR PLAYING.");
            return;
        }

        final int[] count = {3};
        view.setOverlayMessage("SCOTT WON!", "Next level in: " + count[0]);

        Timer victoryTimer = new Timer(1000, e -> {
            count[0]--;
            if (count[0] > 0) {
                view.setOverlayMessage("SCOTT WON!", "Next level in: " + count[0]);
            } else {
                ((Timer)e.getSource()).stop();

                int nextLvl = 2;
                if (level instanceof Level2) nextLvl = 3;

                maxUnlockedLevel = Math.max(maxUnlockedLevel, nextLvl);
                loadLevel(nextLvl, true);
            }
        });
        victoryTimer.start();
    }
    /* I used a Java Swing Timer here for the GO countdown.
     Used AI to structure it so the level is paused until the countdown finishes.
     */

    private void triggerCountdown() {
        level.stop();

        // Play the countdown level first
        playBackgroundMusic("data/LevelCountdown.wav", false);

        final int[] count = {3};
        view.setOverlayMessage(level.getLevelName(), "Starts in: " + count[0]);

        Timer countdownTimer = new Timer(1000, e -> {
            count[0]--;
            if (count[0] > 0) {
                view.setOverlayMessage(level.getLevelName(), "Starts in: " + count[0]);
            } else if (count[0] == 0) {
                view.setOverlayMessage(level.getLevelName(), "GO!");
            } else {
                view.clearOverlayMessage();
                isTransitioning = false;
                level.start();

                // start the background music after go.
                if (level instanceof Level1) playBackgroundMusic("data/Level1.wav", true);
                else if (level instanceof Level2) playBackgroundMusic("data/Level2.wav", true);
                else if (level instanceof Level3) playBackgroundMusic("data/Level3.wav", true);

                ((Timer)e.getSource()).stop();
            }
        });
        countdownTimer.start();
    }

    /* Used AI on how to make custom buttons instead of the default boring ones.
     It helped me write this method that draws the button shapes and text in the Scott Pilgrim style.
     */
    private JButton createScottPilgrimButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), skew = 25;
                int yOffset = getModel().isPressed() ? 4 : 0;
                int xOffset = getModel().isPressed() ? 4 : 0;

                int[] sx = {skew + 6, w, w - skew, 6};
                int[] sy = {6, 6, h, h};
                g2d.setColor(Color.BLACK); g2d.fillPolygon(sx, sy, 4);

                int[] mx = {skew + xOffset, w - 8 + xOffset, w - skew - 8 + xOffset, xOffset};
                int[] my = {yOffset, yOffset, h - 8 + yOffset, h - 8 + yOffset};
                g2d.setColor(getModel().isRollover() ? baseColor.brighter() : baseColor);
                g2d.fillPolygon(mx, my, 4);

                g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(4)); g2d.drawPolygon(mx, my, 4);

                g2d.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2 + xOffset;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset - 4;

                g2d.setColor(Color.BLACK); g2d.drawString(getText(), textX + 2, textY + 2);
                g2d.setColor(Color.WHITE); g2d.drawString(getText(), textX, textY);
            }
        };
        btn.setPreferredSize(new Dimension(280, 65));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Created a testing button so I am able to access all levels for quick testing.
    private JButton createSecretButton(boolean isStageSelect) {
        JButton secretBtn = new JButton("[?]");
        secretBtn.setFont(new Font("Monospaced", Font.BOLD, 22));
        secretBtn.setForeground(new Color(255, 255, 255, 180));
        secretBtn.setFocusPainted(false);
        secretBtn.setContentAreaFilled(false);
        secretBtn.setBorderPainted(false);
        secretBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // This is purely for testing purposes.

        secretBtn.addActionListener(e -> {
            playClickSound();
            maxUnlockedLevel = 3;
            System.out.println("All Levels Unlocked!");
            if (isStageSelect) showLevelSelect();
        });
        return secretBtn;
    }



    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            Image menuBg = new ImageIcon("data/Mainmenu.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (menuBg != null && menuBg.getWidth(null) > 0) g.drawImage(menuBg, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(new Color(20, 10, 30)); g.fillRect(0, 0, getWidth(), getHeight()); }
            }
        };

        //Main title text
        JPanel titlePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                String line1 = "SCOTT PILGRIM", line2 = "T  A  G";
                g2d.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 75));
                FontMetrics fm = g2d.getFontMetrics();
                int x1 = (getWidth() - fm.stringWidth(line1)) / 2, y1 = fm.getAscent() + 10;
                int x2 = (getWidth() - fm.stringWidth(line2)) / 2, y2 = y1 + fm.getHeight() - 15;

                g2d.setColor(Color.BLACK);
                for (int i = -4; i <= 4; i++) for (int j = -4; j <= 4; j++)
                    if (i != 0 || j != 0) { g2d.drawString(line1, x1 + i, y1 + j); g2d.drawString(line2, x2 + i, y2 + j); }

                g2d.setColor(new Color(255, 0, 150)); g2d.drawString(line1, x1 + 6, y1 + 6); g2d.drawString(line2, x2 + 6, y2 + 6);
                g2d.setColor(Color.YELLOW); g2d.drawString(line1, x1, y1); g2d.drawString(line2, x2, y2);
            }
        };
        titlePanel.setPreferredSize(new Dimension(800, 220));
        titlePanel.setOpaque(false);

        JButton playBtn = createScottPilgrimButton("START GAME", new Color(40, 200, 80));
        playBtn.addActionListener(e -> {
            playClickSound();
            cardLayout.show(mainContainer, "Game");
            triggerCountdown();
            view.requestFocusInWindow();
        });

        JButton quitBtn = createScottPilgrimButton("QUIT", new Color(70, 70, 75));
        quitBtn.addActionListener(e -> {
            playClickSound();
            System.exit(0);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH; gbc.insets = new Insets(30, 0, 0, 0);
        panel.add(titlePanel, gbc);

        gbc.gridy = 1; gbc.weighty = 0.0; gbc.anchor = GridBagConstraints.SOUTH; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(playBtn, gbc);

        gbc.gridy = 2; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH; gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(quitBtn, gbc);

        GridBagConstraints gbcSecret = new GridBagConstraints();
        gbcSecret.gridx = 0; gbcSecret.gridy = 3; gbcSecret.weightx = 1.0;
        gbcSecret.anchor = GridBagConstraints.SOUTHEAST; gbcSecret.insets = new Insets(0, 0, 10, 10);
        panel.add(createSecretButton(false), gbcSecret);

        playBackgroundMusic("data/MainMenuMusic.wav", true);

        return panel;
    }

    public void showLevelSelect() {
        level.stop();
        playBackgroundMusic("data/PauseMenu.wav", true);
        mainContainer.add(createLevelSelectMenu(), "LevelSelect");
        cardLayout.show(mainContainer, "LevelSelect");
    }

    private JPanel createLevelSelectMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(20, 40, 50));

        JPanel titlePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                String text = "STAGE SELECT"; g2d.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 55));
                FontMetrics fm = g2d.getFontMetrics(); int x = (getWidth() - fm.stringWidth(text)) / 2, y = fm.getAscent() + 10;

                g2d.setColor(Color.BLACK);
                for (int i = -4; i <= 4; i++) for (int j = -4; j <= 4; j++) g2d.drawString(text, x + i, y + j);

                g2d.setColor(new Color(0, 100, 200)); g2d.drawString(text, x + 6, y + 6);
                g2d.setColor(Color.CYAN); g2d.drawString(text, x, y);
            }
        };
        titlePanel.setPreferredSize(new Dimension(800, 100));
        titlePanel.setOpaque(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); btnPanel.setOpaque(false);

        JButton lvl1 = createScottPilgrimButton("LEVEL 1", new Color(50, 150, 220));
        lvl1.setPreferredSize(new Dimension(200, 65));
        lvl1.addActionListener(e -> { playClickSound(); loadLevel(1, true); });

        JButton lvl2 = createScottPilgrimButton(maxUnlockedLevel >= 2 ? "LEVEL 2" : "LOCKED", maxUnlockedLevel >= 2 ? new Color(50, 200, 100) : new Color(60, 60, 60));
        lvl2.setPreferredSize(new Dimension(200, 65)); lvl2.setEnabled(maxUnlockedLevel >= 2);
        lvl2.addActionListener(e -> { playClickSound(); loadLevel(2, true); });

        JButton lvl3 = createScottPilgrimButton(maxUnlockedLevel >= 3 ? "BOSS" : "LOCKED", maxUnlockedLevel >= 3 ? new Color(220, 50, 50) : new Color(60, 60, 60));
        lvl3.setPreferredSize(new Dimension(200, 65)); lvl3.setEnabled(maxUnlockedLevel >= 3);
        lvl3.addActionListener(e -> { playClickSound(); loadLevel(3, true); });

        btnPanel.add(lvl1); btnPanel.add(lvl2); btnPanel.add(lvl3);

        JButton resume = createScottPilgrimButton("RESUME GAME", new Color(200, 150, 50));
        resume.addActionListener(e -> {
            playClickSound();
            if (level instanceof Level1) playBackgroundMusic("data/Level1.wav", true);
            else if (level instanceof Level2) playBackgroundMusic("data/Level2.wav", true);
            else if (level instanceof Level3) playBackgroundMusic("data/Level3.wav", true);

            cardLayout.show(mainContainer, "Game");
            level.start();
            view.requestFocusInWindow();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 50, 0); panel.add(titlePanel, gbc);
        gbc.gridy = 1; panel.add(btnPanel, gbc);
        gbc.gridy = 2; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH; gbc.insets = new Insets(60, 0, 0, 0); panel.add(resume, gbc);

        GridBagConstraints gbcSecret = new GridBagConstraints();
        gbcSecret.gridx = 0; gbcSecret.gridy = 3; gbcSecret.weightx = 1.0;
        gbcSecret.anchor = GridBagConstraints.SOUTHEAST; gbcSecret.insets = new Insets(0, 0, 10, 10);
        panel.add(createSecretButton(true), gbcSecret);

        return panel;
    }

    private void loadLevel(int levelNum, boolean runCountdown) {
        level.stop();
        view.removeKeyListener(controller);

        if (levelNum == 1) level = new Level1();
        else if (levelNum == 2) level = new Level2();
        else if (levelNum == 3) level = new Level3();

        // Removed music triggers from here so they don't clash with the countdown sound!

        view.setLevel(level);
        view.setWorld(level);

        controller.updatePlayers(level.getScott(), level.getEvilEx(), level);
        tracker.updatePlayers(level.getScott(), level.getEvilEx());
        view.addKeyListener(controller);

        level.addStepListener(tracker);

        // This StepListener checks if the level is over or if the player died
        level.addStepListener(new city.cs.engine.StepListener() {
            private boolean isGameOver = false;

            @Override public void preStep(city.cs.engine.StepEvent e) {}
            @Override public void postStep(city.cs.engine.StepEvent e) {
                if (level.isComplete() && !isTransitioning) {
                    isTransitioning = true;
                    handleLevelCompletion();
                } else if (level.getScott().getLives() <= 0 && !isGameOver) {
                    isGameOver = true;
                    playBackgroundMusic("data/GameOver.wav", false);
                }
            }
        });

        cardLayout.show(mainContainer, "Game");
        view.requestFocusInWindow();

        if (runCountdown) {
            triggerCountdown();
        } else {
            // Skips countdown when instantly restarting a level
            if (level instanceof Level1) playBackgroundMusic("data/Level1.wav", true);
            else if (level instanceof Level2) playBackgroundMusic("data/Level2.wav", true);
            else if (level instanceof Level3) playBackgroundMusic("data/Level3.wav", true);
            level.start();
        }
    }

    public void restartCurrentLevel() {
        int currentLevelNum = 1;
        if (level instanceof Level2) currentLevelNum = 2;
        if (level instanceof Level3) currentLevelNum = 3;
        loadLevel(currentLevelNum, false);
    }

    public void goToNextLevel() {
        int next = 2;
        if (level instanceof Level2) next = 3;
        loadLevel(next, true);
    }

    public static void main(String[] args) {
        new Game();
    }
}