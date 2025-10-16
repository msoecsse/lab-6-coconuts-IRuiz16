package coconuts;

import javafx.scene.control.Label;

import java.util.ArrayList;

// An abstraction of all objects that can be hit by another object
// This captures the Subject side of the Observer pattern; observers of the hit event will take action
//   to process that event
// This is a domain class; do not introduce JavaFX or other GUI components here
public class HitEvent {
    //List of observers - objects that do something when a hit happens.
    private static ArrayList<HittableIslandObject> observers = new ArrayList<>();

    //Register or unregister the observers.
    public static void addObserver(HittableIslandObject observer){
        observers.add(observer);
    }

    public static void removeObserver(HittableIslandObject observer){
        observers.remove(observer);
    }

    //Method to notify all observers
    private static void notifyAllObservers(IslandObject source, HittableIslandObject target, Label beachScore, Label crabScore){
        for(HittableIslandObject o : observers){
            o.onHit(source, target, beachScore, crabScore);
        }
    }

    public static void hit(IslandObject source, HittableIslandObject target, Label beachScore, Label crabScore){
        //The source doesn't have to be hittable because like the laser beam can hit other things but can't be hit itself.
        notifyAllObservers(source, target, beachScore, crabScore);
        //Separate methods to allow for extra code if needed.
    }
}
