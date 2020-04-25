package ooga.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;


public class PauseMenu extends OptionMenu {
  private final ResourceBundle myResources = ResourceBundle.getBundle("ooga/view/Resources.config");
  private final String ICON_STYLE = myResources.getString("iconcss");
  private static final String PAUSE_MENU_TITLE = "Game Paused";
  private static final double ICON_SIZE = 50;

  private final BooleanProperty resumed = new SimpleBooleanProperty(true);
  private final BooleanProperty quit = new SimpleBooleanProperty(false);
  private final BooleanProperty save = new SimpleBooleanProperty(false);
  final Map<BooleanProperty, String> buttonPropertiesAndNames = new HashMap<>(){{
    put(resumed, "Play");
    put(quit, "Quit");
    put(save, "Save");
  }};


  /**
   * Menu that appears when game has been paused
   */
  public PauseMenu(){
    super(PAUSE_MENU_TITLE);
    this.setLeft(setMenuItems(createButtons()));
  }

  /**
   * used by ViewerGame to tell if user has selected to resume game
   * @return true if resumed false if not
   */
  public BooleanProperty resumedProperty() {
    return resumed;
  }

  /**
   * Sets resumed Property
   * @param resumed - a boolean that states if game is in play
   */
  public void setResumed(boolean resumed) {
    this.resumed.set(resumed);
  }

  /**
   * Used by ViewerGame to tell if game as been exited by user
   * @return true if exited, false if not
   */
  public BooleanProperty quitProperty() {
    return quit;
  }

  /**
   * Used by ViewGame to tell is game as been saved by user
   * @return true if saved
   */
  public BooleanProperty saveProperty() {
    return save;
  }


  private Node makeButton(BooleanProperty statusProperty, String text){
    Button button = new Button(text);
    ImageView icon = new ImageView(new Image(myResources.getString(text)));
    icon.setStyle(ICON_STYLE);
    icon.setFitHeight(ICON_SIZE);
    icon.setFitWidth(ICON_SIZE);
    button.setGraphic(icon);
    button.setOnAction(e -> statusProperty.setValue(!statusProperty.getValue()));
    return button;
  }


  private List<Node> createButtons() {
    List<Node> list = new ArrayList<>();
    for(Map.Entry<BooleanProperty, String> buttonPropertyAndName : buttonPropertiesAndNames.entrySet()){
      list.add(makeButton(buttonPropertyAndName.getKey(), buttonPropertyAndName.getValue()));
    }
    return list;
  }
}
