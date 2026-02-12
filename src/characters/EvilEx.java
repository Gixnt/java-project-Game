package characters;

import city.cs.engine.*;

public class EvilEx extends Walker {
    private static final Shape shape = new BoxShape(1,2);

    private static final BodyImage image = new BodyImage("data/books.png", 4f);

    public EvilEx(World world) {
        super(world,shape);
        addImage(image);
    }

}
