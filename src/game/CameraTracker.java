package game;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import levels.Level1;
import org.jbox2d.common.Vec2;
import views.GameView;

public class CameraTracker implements StepListener {
    private GameView view;
    private Scott p1;
    private EvilEx p2;

    public CameraTracker(GameView view, Scott p1, EvilEx p2) {
        this.view = view;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void preStep(StepEvent stepEvent) {
        // I wanted the camera to zoom in when players are close and zoom out when they are far apart.
        // This calculates the distance and sets a zoom level, I have used AI for help on this.
        Vec2 p1Pos = p1.getPosition();
        Vec2 p2Pos = p2.getPosition();
        float distanceX = Math.abs(p1Pos.x - p2Pos.x);

        float currentZoom = 450f / (distanceX + 15f);
        if (currentZoom > 25) currentZoom = 25;
        if (currentZoom < 12) currentZoom = 12; // Max zoom out

        view.setZoom(currentZoom);

        // This finds the center point between the two players, so the camera looks at the middle of the action.
        float midX = (p1Pos.x + p2Pos.x) / 2.0f;
        float midY = (p1Pos.y + p2Pos.y) / 2.0f;

        /* I had a bug where the camera would scroll too far and show the grey empty space
           outside the game world. I used AI to help me calculate these "clamping" values.
           It checks the width of the view against the zoom level to ensure the camera
           never moves past the walls or below the floor.
        */

        // Calculate how much of the world is currently visible
        float viewWidthMeters = view.getWidth() / currentZoom;
        float viewHeightMeters = view.getHeight() / currentZoom;

        // Stop the camera from going below the floor
        float cameraMinY = Level1.FLOOR_Y + (viewHeightMeters / 2.0f);
        if (midY < cameraMinY) {
            midY = cameraMinY;
        }

        // Stop the camera from going past the left or right walls
        float arenaRightEdge = Level1.WALL_X - 1f;
        float arenaLeftEdge = -Level1.WALL_X + 1f;

        // The camera center cannot be closer to the wall than half the screen width
        float cameraMaxX = arenaRightEdge - (viewWidthMeters / 2.0f);
        float cameraMinX = arenaLeftEdge + (viewWidthMeters / 2.0f);


        if (midX > cameraMaxX) midX = cameraMaxX;
        if (midX < cameraMinX) midX = cameraMinX;

        // Update the view to the new safe position
        view.setCentre(new Vec2(midX, midY));
    }

    @Override
    public void postStep(StepEvent stepEvent) {
    }
}