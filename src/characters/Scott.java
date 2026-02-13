package characters;

import city.cs.engine.*;

public class Scott extends Walker {
    private static final Shape shape = new BoxShape(1, 2);

    private static final BodyImage image = new BodyImage("data/ScottPilgrim.png", 4f);

    private int lives = 3;

    public Scott(World world) {
        super(world, shape);
        addImage(image);

    }

    public int getLives() {
        return lives;
    }

    public void decrementLives(){
        lives--;
        System.out.println("Scott has lost a life! Remaining lives: " + lives);
    }


}
