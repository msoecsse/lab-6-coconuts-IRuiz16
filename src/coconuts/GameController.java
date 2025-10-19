package coconuts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Optional;

// JavaFX Controller class for the game - generally, JavaFX elements (other than Image) should be here
public class GameController {

    /**
     * Time between calls to step() (ms)
     */
    private static final double MILLISECONDS_PER_STEP = 1000.0 / 30;
    private Timeline coconutTimeline;
    private boolean started = false;
    private int highScore = 0;

    @FXML
    private Pane gamePane;
    @FXML
    private Pane theBeach;
    @FXML
    private Label beachScore;
    @FXML
    private Label crabScore;

    private OhCoconutsGameManager theGame;

    @FXML
    public void initialize() {
        theGame = new OhCoconutsGameManager((int) (gamePane.getPrefHeight() - theBeach.getPrefHeight()),
                (int) (gamePane.getPrefWidth()), gamePane, beachScore, crabScore);

        gamePane.setFocusTraversable(true);

        coconutTimeline = new Timeline(new KeyFrame(Duration.millis(MILLISECONDS_PER_STEP), (e) -> {
            theGame.tryDropCoconut();
            theGame.advanceOneTick();
            if (theGame.done() || theGame.gameEnd) {
                coconutTimeline.pause();
                Platform.runLater(this::showEnd);
            }
        }));

        coconutTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void playAgain(){
        gamePane.getChildren().clear();
        theGame = new OhCoconutsGameManager((int) (gamePane.getPrefHeight() - theBeach.getPrefHeight()),
                (int) (gamePane.getPrefWidth()), gamePane, beachScore, crabScore);

        // Reset scores
        beachScore.setText("0");
        crabScore.setText("0");

        // Restart timeline
        coconutTimeline.playFromStart();
    }

    public void showEnd(){
        int score = Integer.parseInt(crabScore.getText());
        if(score > highScore){
            highScore = score;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over!");
        alert.setContentText("Score: " + score +"\nHigh Score: " + highScore);
        alert.initOwner(gamePane.getScene().getWindow());


        ButtonType yes = new ButtonType("Play Again");
        ButtonType no = new ButtonType("Quit");

        alert.getButtonTypes().setAll(yes, no);

        Optional<ButtonType> choice = alert.showAndWait();

        if(choice.isPresent() && choice.get() == yes){
            playAgain();
        } else{
            Platform.exit();
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT && !theGame.done()) {
            //just move the crab
            theGame.getCrab().crawl(10);

        } else if (keyEvent.getCode() == KeyCode.LEFT && !theGame.done()) {
            theGame.getCrab().crawl(-10);

        } else if(keyEvent.getCode() == KeyCode.UP){
            theGame.shootLaser();
            theGame.advanceOneTick();

        } else if (keyEvent.getCode() == KeyCode.SPACE) {
            if (!started) {
                coconutTimeline.play();
                started = true;
            } else {
                coconutTimeline.pause();
                started = false;
            }
        }
    }
}
