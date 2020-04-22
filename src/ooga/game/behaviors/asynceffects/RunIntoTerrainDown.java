package ooga.game.behaviors.asynceffects;

import ooga.Entity;
import ooga.game.GameInternal;

import java.util.List;
import java.util.Map;

public class RunIntoTerrainDown extends RunIntoTerrain {
  public RunIntoTerrainDown(List<String> args) {
    super(args);
  }

  /**
   * Performs the effect
   *
   * @param subject     The entity that owns this. This is the entity that should be modified.
   * @param otherEntity
   * @param elapsedTime
   * @param variables
   * @param game
   */
  @Override
  protected void doTimeDelayedEffect(Entity subject, Entity otherEntity, double elapsedTime, Map<String, Double> variables, GameInternal game) {
    //subject.setVelocity(subject.getVelocity().get(0),0);
    double targetX = subject.getPosition().get(0);
    double targetY = otherEntity.getPosition().get(1)-subject.getHeight();
    subject.setPosition(List.of(targetX,targetY));
    subject.blockInDirection("Down", true);
  }
}
