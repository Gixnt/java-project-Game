package characters;

import city.cs.engine.*;

public class Scott extends Walker {
    private static final Shape shape = new BoxShape(1, 3f);

    private static final BodyImage image = new BodyImage("data/ScottPilgrim.png", 6f);

    private int lives = 3;

    public Scott(World world) {
        super(world, shape);
        addImage(image);
        setGravityScale(4f);

    }

    public int getLives() {
        return lives;
    }

    public void decrementLives(){
        lives--;
        System.out.println("Scott has lost a life! Remaining lives: " + lives);
    }

    public void resetLives(){
        lives = 3;
    }


}
