package game;

import city.cs.engine.*;

/**
 * A collectible item that restores health.
 * Extends DynamicBody so it can fall and react to physics.
 */
public class EstusFlask extends DynamicBody {

    private static final Shape flaskShape = new CircleShape(0.5f);

    // private static final BodyImage image = new BodyImage("data/estus.png", 1.0f);

    public EstusFlask(World world) {
        super(world, flaskShape);

        // Visuals
        // this.addImage(image);
        this.setFillColor(new java.awt.Color(255, 165, 0)); // Orange color like Estus
    }
}