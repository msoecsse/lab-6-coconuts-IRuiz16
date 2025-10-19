package coconuts;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// This class manages the game, including tracking all island objects and detecting when they hit
public class OhCoconutsGameManager {
    private final Collection<IslandObject> allObjects = new LinkedList<>();
    private final Collection<HittableIslandObject> hittableIslandSubjects = new LinkedList<>();
    private final Collection<IslandObject> scheduledForRemoval = new LinkedList<>();
    private final int height, width;
    private final int DROP_INTERVAL = 10;
    private final int MAX_TIME = 100;
    private Pane gamePane;
    private Crab theCrab;
    private Beach theBeach;
    /* game play */
    private int coconutsInFlight = 0;
    private int gameTick = 0;
    public boolean gameEnd = false;
    private Label beachScore;
    private Label crabScore;

    private final Map<String, MediaPlayer> sounds;

    public OhCoconutsGameManager(int height, int width, Pane gamePane, Label beachScore, Label crabScore) {
        this.height = height;
        this.width = width;
        this.gamePane = gamePane;

        this.beachScore = beachScore;
        this.crabScore = crabScore;

        this.theCrab = new Crab(this, height, width);
        registerObject(theCrab);

        gamePane.getChildren().addAll(theCrab.getImageView());

        this.theBeach = new Beach(this, height, width);
        registerObject(theBeach);
        if (theBeach.getImageView() != null)
            System.out.println("Unexpected image view for beach");

        sounds = new HashMap<>();
        sounds.put("laser", new MediaPlayer(new Media(new File("soundEffects/laser.mp3").toURI().toString())));
        sounds.put("lose", new MediaPlayer(new Media(new File("soundEffects/losing.mp3").toURI().toString())));
        sounds.put("coconut", new MediaPlayer(new Media(new File("soundEffects/coconutHit.mp3").toURI().toString())));
    }

    private void registerObject(IslandObject object) {
        allObjects.add(object);
        if (object.isHittable()) {
            HittableIslandObject asHittable = (HittableIslandObject) object;
            hittableIslandSubjects.add(asHittable);
            HitEvent.addObserver((HittableIslandObject) object);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void coconutDestroyed() {
        coconutsInFlight -= 1;
    }

    public void tryDropCoconut() {
        if(!gameEnd) {
            if (gameTick % DROP_INTERVAL == 0 && theCrab != null) {
                coconutsInFlight += 1;
                Coconut c = new Coconut(this, (int) (Math.random() * width));
                registerObject(c);
                gamePane.getChildren().add(c.getImageView());
            }
            gameTick++;
        }
    }

    public void shootLaser(){
        if(!gameEnd) {
            if (theCrab != null) {
                playSound("laser");
                LaserBeam laserBeam = new LaserBeam(this, theCrab.y, theCrab.x);
                registerObject(laserBeam);
                gamePane.getChildren().add(laserBeam.getImageView());
            }
        }
    }

    public Crab getCrab() {
        return theCrab;
    }

    public void killCrab() {
        gamePane.getChildren().remove(theCrab.getImageView());
        scheduleForDeletion(theCrab);
        gameEnd = true;

        //Little delay to play sound after wards
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5)); // delay = 1.5 seconds
        delay.setOnFinished(e -> playSound("lose"));
        delay.play();
    }

    public void advanceOneTick() {
        for (IslandObject o : allObjects) {
            o.step();
            o.display();
        }

        scheduledForRemoval.clear();
        for (IslandObject thisObj : allObjects) {
            for (HittableIslandObject hittableObject : hittableIslandSubjects) {
                if (thisObj.canHit(hittableObject) && thisObj.isTouching(hittableObject)) {
                    hittableObject.onHit(thisObj, hittableObject, beachScore, crabScore);

                    scheduledForRemoval.add(hittableObject);
                    gamePane.getChildren().remove(hittableObject.getImageView());
                }
            }
        }
        for (IslandObject thisObj : scheduledForRemoval) {
            allObjects.remove(thisObj);
            if (thisObj instanceof HittableIslandObject) {
                hittableIslandSubjects.remove((HittableIslandObject) thisObj);
                HitEvent.removeObserver((HittableIslandObject) thisObj);
            }
        }
        scheduledForRemoval.clear();
    }

    public void scheduleForDeletion(IslandObject islandObject) {
        scheduledForRemoval.add(islandObject);
    }

    public boolean done() {
        return coconutsInFlight == 0 && gameTick >= MAX_TIME;
    }

    protected void playSound(String typeOfSound){
        //typeOfSound keys: laser, lose, coconut
        MediaPlayer mediaPlayer = sounds.get(typeOfSound.toLowerCase());
        mediaPlayer.stop(); //To reset mediaPlayer!
        mediaPlayer.play();
    }
}
