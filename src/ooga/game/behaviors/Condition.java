// For my masterpiece I decided to make conditions for Behaviors into their own type of object,
// with an outward facing interface, so that ConditionalBehavior classes don't have to know
// the implementation details about what kinds of conditions exist. This way, as long as the
// Data component of the project has a way of making Condition instances (maybe with a Simple
// Factory), the ConditionalBehavior can have any number and type of Conditions, and new types can
// be created polymorphically. Because the interface has just one method, the ConditionalBehavior
// can have a loop that goes through each and checks it, instead of having the long if statement
// that the Checklist dislikes (BehaviorInstance lines 72-77).
// NOTE: Some parts of this masterpiece are based on code I wrote, and some parts are based on
// code I wrote jointly with Cary Shindell. I feel that I have some ownership over all involved
// code here.

package ooga.game.behaviors;

import java.util.Map;
import ooga.game.EntityInternal;
import ooga.game.GameInternal;

/**
 * @author Sam Thompson
 * Represents some kind of predicate that must be true in order for the Effects of a Behavior
 * to execute. Is owned by a Behavior for assessment each game step.
 * For Reflection: Each concrete implementation should have a constructor that takes in a
 *              Map of String,String pairs as arguments and throw an exception if it is invalid.
 * Dependencies:  Relies on EntityInternal for access to information about them, such as their Map
 *                of variables, or their position.
 *                Relies on GameInternal for access to game-wide information and variables.
 * Examples:  1. The Game variable 'Lives' must be less than 1.
 *            2. There must not be any Koopa colliding downwards with a Button.
 */
public interface Condition {

  /**
   * @param subject The Entity whose Behavior owns this Condition.
   * @param game The GameInternal that this Entity is part of (that it will use for checking
   *             game-wide information).
   * @param inputs The Map with String keys representing the standardized key code of an input
   *               and the String values representing their status this game step.
   * @param collisions The Map where EntityInternal keys are each active Entity and the
   *                   Map values map each Entity they are colliding with to a String representing
   *                   the collision type (direction).
   * @return True if the condition is met this game step. Generally, if all of a Behavior's Conditions
   * assess to true, then a Behavior's Effects are executed.
   */
  boolean isSatisfied(EntityInternal subject, GameInternal game, Map<String,String> inputs, Map<EntityInternal, Map<EntityInternal, String>> collisions);
}