package characters;

import city.cs.engine.*;

public class EvilEx extends Walker {
    private static final Shape shape = new BoxShape(1,3);

    private static final BodyImage image = new BodyImage("data/GideonGraves.png", 9f);

    public EvilEx(World world) {
        super(world,shape);
        addImage(image);
        setGravityScale(4f);
    }

}
