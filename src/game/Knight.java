package game;

import city.cs.engine.*;

/**
 * The main player character.
 * Extends Walker to gain movement capabilities.
 */
public class Knight extends Walker {

    // Remember: standard BoxShape is measured from the center to the edge.
    // So 1.0f width means the box is 2 meters wide total.
    private static final Shape knightShape = new BoxShape(1.0f, 2.0f);

    // Game State specific to the Knight
    private int health;
    private int estusFlasks;

    // Image for the Knight (Uncomment when you have a 'knight.png' in your data folder)
    // private static final BodyImage image = new BodyImage("data/knight.png", 4.0f);

    public Knight(World world) {
        super(world, knightShape);

        // Initial Stats
        this.health = 100;
        this.estusFlasks = 0;

        // Visuals: Attach image if available, otherwise solid color
        // this.addImage(image);
        this.setFillColor(new java.awt.Color(50, 50, 50)); // Dark Grey Armor color
    }

    // --- Getters and Setters (Encapsulation) ---

    public int getHealth() {
        return health;
    }

    public int getEstusFlasks() {
        return estusFlasks;
    }

    public void collectEstus() {
        estusFlasks++;
        System.out.println("Estus Flask acquired! Total: " + estusFlasks);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public void heal() {
        if (estusFlasks > 0) {
            health = 100;
            estusFlasks--;
        }
    }
}