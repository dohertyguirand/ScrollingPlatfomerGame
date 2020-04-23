package ooga.game;

import ooga.Entity;
import ooga.data.OogaEntity;

import javafx.collections.ObservableList;
import ooga.UserInputListener;

/**
 * An instance of a loaded game that holds all levels and global game data. Must be populated
 * with levels and any relevant persistent data like the starting number of lives. Handles
 * game logic that is applied to every entity within the game every frame.
 */
public interface Game {

  /**
   * @return All entities that ought to be drawn onscreen in the current frame.
   * This includes the player, enemies, terrain, powerups, etc., but not background or in-game
   * UI.
   */
  ObservableList<Entity> getEntities();


  //TODO: Remove, since this causes an abstraction (Game) to rely on an implementation (Entity)
  /**
   * @return The list of entities as a list of abstract Entity class objects instead of as
   * EntityAPI objects.
   */
  ObservableList<OogaEntity> getAbstractEntities();

  /**
   * Runs anything that needs to be run by the game, levels, or any entities in a level when
   * the game is first started.
   */
  void doGameStart();

  /**
   * Updates things in the gaem according to how much time has passed
   * @param elapsedTime time passed in milliseconds
   */
  void doGameStep(double elapsedTime);

  UserInputListener makeUserInputListener();

  /**
   * @return The String ID of the game's current level.
   */
  String getCurrentLevelId();
}
