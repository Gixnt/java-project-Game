package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.World;
import city.cs.engine.SoundClip;

public abstract class GameLevel extends World {
    private Scott scott;
    private EvilEx evilEx;

    //Shared sound effects for all levels
    protected static SoundClip keySound;
    protected static SoundClip itemSound;
    protected static SoundClip teleportSound;

    /*This ensures the heavy .wav files are only loaded into memory once for the entire game,
     * which stops the game from freezing up every time a new level loads.
     */
    static {
        try {
            keySound = new SoundClip("data/KeyCollected.wav");
            itemSound = new SoundClip("data/CollectedItem.wav");
            teleportSound = new SoundClip("data/Teleport.wav"); // LOAD TELEPORT SOUND
        } catch (Exception e) {
            System.out.println("Error loading shared level sounds: " + e);
        }
    }

    public GameLevel() {
        super();
    }

    public Scott getScott() { return scott; }
    public void setScott(Scott scott) { this.scott = scott; }

    public EvilEx getEvilEx() { return evilEx; }
    public void setEvilEx(EvilEx evilEx) { this.evilEx = evilEx; }

    public abstract boolean isComplete();
    public abstract java.awt.Image getBackgroundImage();
    public abstract float getTimeRemaining();
    public abstract String getLevelName();
}