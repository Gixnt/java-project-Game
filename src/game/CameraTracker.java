package game;

import characters.EvilEx;
import characters.Scott;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;
import views.GameView;

public class CameraTracker implements StepListener {
    private final GameView view;
    private Scott p1;
    private EvilEx p2;

    public CameraTracker(GameView view, Scott p1, EvilEx p2) {
        this.view = view;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void updatePlayers(Scott p1, EvilEx p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void preStep(StepEvent stepEvent) {
        Vec2 p1Pos = p1.getPosition();
        Vec2 p2Pos = p2.getPosition();

        // Finds the exact middle point between both players

        float midX = (p1Pos.x + p2Pos.x) / 2.0f;
        float midY = (p1Pos.y + p2Pos.y) / 2.0f;

        float currentZoom;

        /*I used AI here to help me with the math for the dynamic camera zoom.
         this calculates the distance between the players and changes the zoom
         so they both always stay in the frame without zooming out too far.
         */

        if (p1.getWorld() instanceof levels.Level3) {
            currentZoom = 15f;
            view.setZoom(currentZoom);
        } else {
            float distanceX = Math.abs(p1Pos.x - p2Pos.x);
            currentZoom = 450f / (distanceX + 15f);
            if (currentZoom > 25) currentZoom = 25;
            if (currentZoom < 12) currentZoom = 12;
            view.setZoom(currentZoom);
        }

        float viewWidthMeters = view.getWidth() / currentZoom;
        float viewHeightMeters = view.getHeight() / currentZoom;

        // Use the EXACT same floor as Level 1 and Level 2
        float floorY = levels.Level1.FLOOR_Y;
        float wallX = levels.Level1.WALL_X;

        if (p1.getWorld() instanceof levels.Level3) {
            wallX = 35f; // Only push the walls out, and keep the floor the same
        }

        // Stop camera from going below the floor
        float cameraMinY = floorY + (viewHeightMeters / 2.0f);
        if (midY < cameraMinY) {
            midY = cameraMinY;
        }

        float arenaRightEdge = wallX - 1f;
        float arenaLeftEdge = -wallX + 1f;

        float cameraMaxX = arenaRightEdge - (viewWidthMeters / 2.0f);
        float cameraMinX = arenaLeftEdge + (viewWidthMeters / 2.0f);

        if (midX > cameraMaxX) midX = cameraMaxX;
        if (midX < cameraMinX) midX = cameraMinX;

        view.setCentre(new Vec2(midX, midY));
    }

    @Override
    public void postStep(StepEvent stepEvent) {}
}