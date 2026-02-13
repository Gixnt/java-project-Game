package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.World;

public abstract class GameLevel extends World {
    private Scott scott;
    private EvilEx evilEx;

    public GameLevel() {
        super();
    }

    public Scott getScott() {
        return scott;
    }

    public void setScott(Scott scott) {
        this.scott = scott;
    }

    public EvilEx getEvilEx() {
        return evilEx;
    }

    public void setEvilEx(EvilEx evilEx) {
        this.evilEx = evilEx;
    }

    public abstract boolean isComplete();
}