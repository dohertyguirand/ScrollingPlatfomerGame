package ooga.data;

import java.util.List;
import java.util.Map;

import ooga.OogaDataException;
import ooga.game.Game;
import ooga.game.Level;

/**
 * Handles the interaction with game data files, including interpretation and writing.
 * Forms the external side of data interaction so that the rest of the program doesn't
 * need to know how we're storing games.
 */
//TODO: make sure OogaDataReader methods are here too
public interface DataReader {

  /**
   * Returns a list of thumbnails for all the available games.
   * Returns an empty list if there are no files containing thumbnails.
   * @return The list of thumbnails of games.
   */
  List<Thumbnail> getThumbnails() throws OogaDataException;

  /**
   * Returns the filepaths to every game file in the library folder. Doesn't guarantee
   * that it will scan for full validity of every file, but makes basic checks.
   * Returns an empty list if library has no game files. Filepaths start in the data folder and
   * begin with "data/games-library/"
   * @return A list of filepaths of game data files in the given folder.
   */
  List<String> getGameFilePaths();

  /**
   * Give a Game a list of level ID's in the order that they're listed in the .xml files
   * @param gameName the name of the Game at the start of the .xml file
   * @return A list of strings, the ID's of the Level written in the game file;
   * @throws OogaDataException if the String given isn't a directory or the cooresponding file is not properly formatted
   */
  List<String> getLevelIDs(String gameName) throws OogaDataException;

  /**
   * @param gameName the name of the Game at the start of the .xml file
   * @return A map of variable names [Strings] to their initial values [Strings]
   * @throws OogaDataException if the String given isn't a directory or the cooresponding file is not properly formatted
   */
  Map<String, String> getVariableMap(String gameName) throws OogaDataException;

  /**
   * @param gameName The name of the game
   * @param levelID  The ID of the level the game is asking for
   * @return A fully loaded Level that is runnable by the game and represents the level in the
   * data file.
   * @throws OogaDataException If the given name isn't in the library or the ID is not in the game.
   */
  Level loadNewLevel(String gameName, String levelID) throws OogaDataException;

  /**
   *
   * @param UserName the name of the user for whom to load the level
   * @param Date the date of the level being loaded
   * @return A fully loaded Level that is runnable by the game and represents the level in the
   * data file.
   * @throws OogaDataException If the username doesn't exist or the user doesn't have at that date and time
   */
  Level loadSavedLevel(String UserName, String Date) throws OogaDataException;

  /**
   * returns information about all of the saves for a given game and user name
   * @param userName the name of the user who made the save file
   * @param gameName the name of the game for which the save was made
   * @return a list of Level ID and date pairs i.e.:
   * {{"Level 1", "1/1/20 12:30:45"},
   * {"Level 3", "1/1/25 1:13:65"}}
   * returns an empty list if the user has no saves for this game
   * @throws OogaDataException if the given username isn't listed as a user in the users file
   */
  List<List<String>> getGameSaves(String userName, String gameName) throws OogaDataException;

  /**
   * @param gameName: the name of the game for which a map is being requested
   * @return A map of all the entities described in a game file of the given name.
   * It maps from the entities' names to their definitions.
   */
  Map<String, ImageEntityDefinition> getImageEntityMap(String gameName) throws OogaDataException;

  /**
   * Saves the current state of the game so it can easily be loaded from where the player
   * left off.
   * @param filePath The filepath at which to save the game.
   * @throws OogaDataException if the filepath is invalid, or if no game is loaded.
   */
  void saveGameState(String filePath) throws OogaDataException;

  /**
   * Loads a file that contains a saved game state, and returns a runnable game that picks up
   * where it left off.
   * @param filePath The filepath of the file to load.
   * @return A runnable Game that matches with how the game was when saved.
   * @throws OogaDataException if there is no valid file at the given filepath.
   */
  Game loadGameState(String filePath) throws OogaDataException;

  /**
   * @param entityName The name of the entity to find the image resource for.
   * @param gameFile The name of the game file to look for the entity inside of.
   * @return The image file location associated with the identified entity.
   * @throws OogaDataException If the entity doesn't exist, or there's no valid game file given,
   * or the given entity has no associated image.
   */
  String getEntityImage(String entityName, String gameFile) throws OogaDataException;

  /**
   * @return A list of Profiles according to the data stored in the Users folder. Returns an empty list if there are no
   * existing profiles
   */
  List<OogaProfile> getProfiles() throws OogaDataException;

  /**
   * Adds a given profile to the profile folder
   * @param newProfile the profile to add to the saved profile folder
   */
  void addNewProfile(OogaProfile newProfile) throws OogaDataException;
}
