package levels;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Level1 extends GameLevel {

    public Level1() {

        Shape groundShape = new BoxShape(30, 0.5f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(0, -11.5f));

        Shape wallShape = new BoxShape(0.5f, 6);
        StaticBody leftWall = new StaticBody(this, wallShape);
        leftWall.setPosition(new Vec2(-11.5f, -6));

        StaticBody rightWall = new StaticBody(this, wallShape);
        rightWall.setPosition(new Vec2(11.5f, -6));


        Shape platformShape = new BoxShape(4, 0.5f);
        StaticBody platform1 = new StaticBody(this, platformShape);
        platform1.setPosition(new Vec2(-7, -4));

        StaticBody platform2 = new StaticBody(this, platformShape);
        platform2.setPosition(new Vec2(5, 2));


        Scott scott = new Scott(this);
        scott.setPosition(new Vec2(-8, -10));
        setScott(scott);

        EvilEx ex = new EvilEx(this);
        ex.setPosition(new Vec2(8, -10));
        setEvilEx(ex);
    }

    @Override
    public boolean isComplete() {
        if (getScott().getLives() == 0) return true;
        return false;
    }
}