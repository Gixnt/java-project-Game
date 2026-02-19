package characters;

import city.cs.engine.*;


/*The inheritance of scott extending walker is to contain all the basic logic
for a character that can move left, right and jump.
It takes everything Walker can do and adds specific features
 */

public class Scott extends Walker {
    private static final Shape shape = new BoxShape(1, 3f);

    private static final BodyImage image = new BodyImage("data/ScottPilgrim.png", 6f);

    private int lives = 3;

    public Scott(World world) {
        super(world, shape);
        addImage(image);
        setGravityScale(3f);

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
