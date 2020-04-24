package ooga.game.behaviors.noncollisioneffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ooga.Entity;
import ooga.game.GameInternal;
import ooga.game.behaviors.TimeDelayedEffect;

/**
 * Uses constant downward motion to simulate basic gravity.
 * Brings attention to the challenge of having acceleration.
 */
//TODO: use change velocity instead of this
@SuppressWarnings("unused")
public class GravityEffect extends TimeDelayedEffect {

    private static final double DEFAULT_Y_GRAVITY = 0.1;
    private List<String> myGravityVectorData;

    public GravityEffect(List<String> args) throws IndexOutOfBoundsException {
        super(args);
    }

    @Override
    public void processArgs(List<String> args) {
        String xGrav = args.get(0);
        String yGrav = args.get(1);
        myGravityVectorData = List.of(xGrav,yGrav);
    }

    public GravityEffect(double xGrav, double yGrav) {
        super(new ArrayList<>());
        myGravityVectorData = List.of(String.valueOf(xGrav),String.valueOf(yGrav));
    }

    /**
     * Performs the effect
     *  @param subject     The entity that owns this. This is the entity that should be modified.
     * @param otherEntity entity we are "interacting with" in this effect
     * @param elapsedTime time between steps in ms
     * @param variables   game variables
     * @param game        game instance
     */
    @Override
    protected void doTimeDelayedEffect(Entity subject, Entity otherEntity, double elapsedTime, Map<String, String> variables, GameInternal game) {
        subject.changeVelocity(parseData(myGravityVectorData.get(0), subject, variables, 0.0),
                parseData(myGravityVectorData.get(1), subject, variables, DEFAULT_Y_GRAVITY));
    }
}
