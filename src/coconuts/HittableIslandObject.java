package coconuts;

import javafx.scene.image.Image;

// Represents island objects which can be hit
// This is a domain class; do not introduce JavaFX or other GUI components here
public abstract class HittableIslandObject extends IslandObject {
    public HittableIslandObject(OhCoconutsGameManager game, int x, int y, int width, Image image) {
        super(game, x, y, width, image);
    }

    public boolean isHittable() {
        return true;
    }

    public abstract void onHit(IslandObject source, HittableIslandObject obj);
}
