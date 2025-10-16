package coconuts;

import javafx.scene.control.Label;
import javafx.scene.image.Image;

// Represents the falling object that can kill crabs. If hit by a laser, the coconut disappears
// This is a domain class; other than Image, do not introduce JavaFX or other GUI components here
public class Coconut extends HittableIslandObject {
    private static final int WIDTH = 50;
    private static final Image coconutImage = new Image("file:images/coco-1.png");

    public Coconut(OhCoconutsGameManager game, int x) {
        super(game, x, 0, WIDTH, coconutImage);
    }

    @Override
    public void step() {
        y += 5;
    }

    @Override
    public void onHit(IslandObject source, HittableIslandObject obj, Label beachScore, Label crabScore) {
        //obj will always be the coconut

        if (source instanceof Beach){
            containingGame.playSound("coconut");
            beachScore.setText(String.valueOf(Integer.parseInt(
                    beachScore.getText()) + 1));
        } else if (source instanceof LaserBeam){
            crabScore.setText(String.valueOf(Integer.parseInt(
                    crabScore.getText()) + 1));
        } else if (source instanceof Crab){
            containingGame.killCrab();
        }
    }
}
