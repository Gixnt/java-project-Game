package characters;

import city.cs.engine.*;


/*The inheritance of scott extending walker is to contain all the basic logic
for a character that can move left, right and jump.
It takes everything Walker can do and adds specific features
 */

public class Scott extends Walker implements StepListener {
    private static final Shape shape = new BoxShape(1, 3f);

    private static final BodyImage idle = new BodyImage("data/ScottPilgrimIdle.gif", 6f);

    private static final BodyImage walkRight = new BodyImage("data/ScottRunning.gif", 6f);
    private static final BodyImage walkLeft = new BodyImage("data/ScottRunningLeft.gif", 6f);





    private int lives = 3;


    public Scott(World world) {
        super(world, shape);
        addImage(idle);
        setGravityScale(3f);

        world.addStepListener(this);

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

    @Override
    public void preStep(StepEvent stepEvent) {
        removeAllImages();

        // Simple checks based on movement speed
        if (getLinearVelocity().x < -0.1f) {
            // Walking left
            addImage(walkLeft);
        } else if (getLinearVelocity().x > 0.1f) {
            // Walking right
            addImage(walkRight);
        } else {
            // Standing perfectly still
            addImage(idle);
        }
    }


    @Override
    public void postStep(StepEvent stepEvent) {
        // Not needed for animations
    }


}
