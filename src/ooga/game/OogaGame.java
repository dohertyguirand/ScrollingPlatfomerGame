package ooga.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ooga.Entity;
import ooga.OogaDataException;
import ooga.UserInputListener;
import ooga.data.DataReader;
import ooga.data.ImageEntityDefinition;
import ooga.game.collisiondetection.DirectionalCollisionDetector;
import ooga.game.inputmanagers.OogaInputManager;

public class OogaGame implements Game, UserInputListener, GameInternal {

  public static final String CLICKED_ON_CODE = "ClickedOn";
  public static final String KEY_ACTIVE_REQUIREMENT = "KeyActive";
  public static final String KEY_PRESSED_REQUIREMENT = "KeyPressed";
  public static final String DEFAULT_INPUT_MAPPINGS = "ooga/game/resources/inputs/keyboard";
  private List<String> myLevelIds;
  private Level currentLevel;
  private String myName;
  private DataReader myDataReader;
  private CollisionDetector myCollisionDetector;
  private ControlsInterpreter myControlsInterpreter;
  private final InputManager myInputManager = new OogaInputManager();
  private Map<String, String> myVariables;
  private ObservableList<Entity> myEntities;
  private final List<Entity> myNewCreatedEntities = new ArrayList<>();
  Map<String, ImageEntityDefinition> myEntityDefinitions;
  private final List<DoubleProperty> cameraShiftProperties = List.of(new SimpleDoubleProperty(), new SimpleDoubleProperty());


  public OogaGame(String gameName, DataReader dataReader, CollisionDetector detector,
      ControlsInterpreter controls, String profileName) throws OogaDataException {
    myDataReader = dataReader;
    myName = gameName;
    myLevelIds = myDataReader.getLevelIDs(gameName);
    myCollisionDetector = detector;
    myControlsInterpreter = controls;
    myEntities = FXCollections.observableArrayList(new ArrayList<>());
    myEntityDefinitions = myDataReader.getImageEntityMap(gameName);
    initVariableMap(gameName);
  }

  private void initVariableMap(String gameName) throws OogaDataException {
    myVariables = new HashMap<>();
    for (String key : myDataReader.getVariableMap(gameName).keySet()){
      myVariables.put(key, myDataReader.getVariableMap(gameName).get(key));
    }
  }

  public OogaGame(String gameName, DataReader dataReader, String profileName, String date) throws OogaDataException {
    this(gameName, dataReader, new DirectionalCollisionDetector(), new KeyboardControls(
        DEFAULT_INPUT_MAPPINGS), "");
    currentLevel = loadGameLevel(gameName, myLevelIds.get(0));
  }

  private Level loadGameLevel(String gameName, String id) throws OogaDataException {
    Level level = myDataReader.loadLevel(gameName,id);
    myEntities.clear();
    myEntities.addAll(level.getEntities());
    return level;
  }

  @Deprecated
  public OogaGame(Level startingLevel, CollisionDetector collisions) {
    myName = "Unnamed";
    myCollisionDetector = collisions;
    myControlsInterpreter = new KeyboardControls(DEFAULT_INPUT_MAPPINGS);
    currentLevel = startingLevel;
  }

  @Override
  public ObservableList<Entity> getEntities() {
    return myEntities;
  }

  /**
   * Updates things in the game according to how much time has passed
   *
   * @param elapsedTime time passed in milliseconds
   */
  @Override
  public void doGameStep(double elapsedTime) {
    doUpdateLoop(elapsedTime);
    myInputManager.update();
  }

  private Map<Entity, Map<String, List<Entity>>> findDirectionalCollisions(double elapsedTime) {
    Map<Entity, Map<String, List<Entity>>> collisionInfo = new HashMap<>();
    for(Entity entity : currentLevel.getEntities()){
      Map<String, List<Entity>> collisionsByDirection = new HashMap<>();
      findEntityCollisions(elapsedTime, entity, collisionsByDirection);
      collisionInfo.put(entity, collisionsByDirection);
    }
    return collisionInfo;
  }

  private void findEntityCollisions(double elapsedTime, Entity entity,
      Map<String, List<Entity>> collisionsByDirection) {
    for(String direction : myCollisionDetector.getSupportedDirections()){
      collisionsByDirection.put(direction, new ArrayList<>());
    }
    for(Entity collidingWith : currentLevel.getEntities()){
      if(entity == (collidingWith)) {
        continue;
      }
      String collisionDirection = myCollisionDetector.getCollisionDirection(entity, collidingWith, elapsedTime);
      if (collisionDirection != null){
        collisionsByDirection.get(collisionDirection).add(collidingWith);
      }
    }
  }

  private void doUpdateLoop(double elapsedTime) {
    doEntityFrameUpdates(elapsedTime);
    doEntityBehaviors(elapsedTime);
    doEntityCleanup();
    executeEntityMovement(elapsedTime);
    doEntityCreation();
  }

  private void doEntityFrameUpdates(double elapsedTime) {
    for (Entity entity : currentLevel.getEntities()) {
      entity.blockInAllDirections(false);
      entity.updateSelf(elapsedTime);
      entity.reactToVariables(myVariables);
    }
  }

  private void doEntityBehaviors(double elapsedTime) {
    List<String> activeKeys = myInputManager.getActiveKeys();
    List<String> pressedKeys = myInputManager.getPressedKeys();
    Map<String,String> allInputs = new HashMap<>();
    for (String key : activeKeys) {
      allInputs.put(key, KEY_ACTIVE_REQUIREMENT);
    }
    for (String key : pressedKeys) {
      allInputs.put(key, KEY_PRESSED_REQUIREMENT);
    }
    Map<Entity, Map<String, List<Entity>>> collisionInfo = findDirectionalCollisions(elapsedTime);
    for (Entity entity : currentLevel.getEntities()) {
      Map<String,String> entityInputs = findEntityInputs(allInputs, entity);
      entity.doConditionalBehaviors(elapsedTime, entityInputs, myVariables, collisionInfo, this);
    }
  }

  private Map<String, String> findEntityInputs(Map<String, String> allInputs, Entity entity) {
    Map<String,String> entityInputs = allInputs;
    for (List<Double> clickPos : myInputManager.getMouseClickPos()) {
      if (myCollisionDetector.entityAtPoint(entity, clickPos.get(0), clickPos.get(1))) {
        entityInputs = new HashMap<>(allInputs);
        entityInputs.put(CLICKED_ON_CODE,KEY_PRESSED_REQUIREMENT);
        break;
      }
    }
    return entityInputs;
  }

  private void executeEntityMovement(double elapsedTime) {
    for (Entity e : currentLevel.getEntities()) {
      e.executeMovement(elapsedTime);
    }
  }

  private void doEntityCreation() {
    for (Entity created : myNewCreatedEntities) {
      myEntities.add(created);
      currentLevel.addEntity(created);
    }
    myNewCreatedEntities.clear();
  }

  private void doEntityCleanup() {
    List<Entity> destroyedEntities = new ArrayList<>();
    for (Entity e : currentLevel.getEntities()) {
      if (e.isDestroyed()) {
        destroyedEntities.add(e);
      }
    }
    for (Entity destroyed : destroyedEntities) {
      if (destroyed.isDestroyed()) {
        currentLevel.removeEntity(destroyed);
        myEntities.remove(destroyed);
      }
    }
  }

  @Override
  public String getCurrentLevelId() {
    return currentLevel.getLevelId();
  }

  /**
   * @param mouseX The X-coordinate of the mouse click, in in-game screen coordinates
   *               (not "view-relative" coordinates).
   * @param mouseY The Y-coordinate of the mouse click, in-game screen coordinates.
   */
  @Override
  public void reactToMouseClick(double mouseX, double mouseY) {
    myInputManager.mouseClicked(mouseX,mouseY);
  }

  /**
   * React to a key being pressed. Use data files to determine appropriate action
   *
   * @param keyName string name of key
   */
  @Override
  public void reactToKeyPress(String keyName) {
    String inputType = myControlsInterpreter.translateInput(keyName);
    myInputManager.keyPressed(inputType);
  }

  @Override
  public void reactToKeyRelease(String keyName) {
    String inputType = myControlsInterpreter.translateInput(keyName);
    myInputManager.keyReleased(inputType);
  }

  /**
   * Handles when the command is given to save the game to a file.
   */
  @Override
  public void reactToGameSave() {
    try {
      myDataReader.saveGameState(myName);
    } catch (OogaDataException e) {
      //if it doesn't work, just keep playing the game.
    }
  }

  /**
   * Handles when the command is given to quit the currently running game.
   * This might be modified to account for identifying which game must close when
   * there are multiple games open.
   */
  @Override
  public void reactToGameQuit() {

  }

  /**
   * indicates the pause button was clicked in the ui
   *
   * @param paused whether or not the button clicked was pause or resume
   */
  @Override
  @SuppressWarnings("EmptyMethod")
  public void reactToPauseButton(boolean paused) {
    //Ooga games do nothing as a reaction to pausing, but other implementations could do things.
  }

  @Override
  public void createEntity(String type, List<Double> position) {
    Entity created = makeEntityInstance(type, position);
    myNewCreatedEntities.add(created);
  }

  @Override
  public void createEntity(String type, List<Double> position, double width, double height) {
    Entity created = makeEntityInstance(type, position);
    created.setHeight(height);
    created.setWidth(width);
    myNewCreatedEntities.add(created);
  }

  private Entity makeEntityInstance(String type, List<Double> position) {
    ImageEntityDefinition definition = myEntityDefinitions.get(type);
    return definition.makeInstanceAt(position.get(0), position.get(1));
  }

  @Override
  public Entity getEntityWithId(String id) {
    for (Entity e : myEntities) {
      String entityId = e.getEntityID();
      if (entityId != null && entityId.equals(id)) {
        return e;
      }
    }
    return null;
  }

  @Override
  public List<Entity> getEntitiesWithName(String name) {
    List<Entity> entitiesWithName = new ArrayList<>();
    for (Entity e : myEntities) {
      if (e.getName().equals(name)) {
        entitiesWithName.add(e);
      }
    }
    return entitiesWithName;
  }

  @Override
  public void goToLevel(String levelID) {
    try {
      currentLevel = loadGameLevel(myName,levelID);
      setCameraShiftValues(0,0);
    }
    catch (OogaDataException ignored) {
      //To preserve the pristine gameplay experience, we do nothing (rather than crash).
    }
  }

  @Override
  public void goToNextLevel() {
    goToLevel(currentLevel.nextLevelID());
    setCameraShiftValues(0,0);
  }

  @Override
  public void restartLevel() {
    goToLevel(currentLevel.getLevelId());
    setCameraShiftValues(0,0);
  }

  @Override
  public void setCameraShiftProperties(List<DoubleProperty> properties){
    for(int i = 0; i < cameraShiftProperties.size(); i ++){
      cameraShiftProperties.get(i).bindBidirectional(properties.get(i));
    }
  }

  public void setCameraShiftValues(double xValue, double yValue){
    cameraShiftProperties.get(0).set(xValue);
    cameraShiftProperties.get(1).set(yValue);
  }

  @Override
  public List<Double> getCameraShiftValues() {
    return List.of(cameraShiftProperties.get(0).getValue(), cameraShiftProperties.get(0).getValue());
  }
}
