package ooga.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ooga.game.behaviors.Effect;
import ooga.Entity;
import ooga.data.ImageEntity;
import ooga.game.behaviors.asyncbehavior.RunIntoTerrain;
import ooga.game.collisiondetection.VelocityCollisionDetector;
import ooga.game.behaviors.Effects.GravityEffect;
import ooga.game.behaviors.Effects.JumpEffect;
import ooga.game.behaviors.Effects.VelocityInputEffect;

public class ControlsTestGameCreation {
  //TODO: Remove, or transform into a reasonable test
  public static OogaGame getGame() {
    List<Entity> entities = new ArrayList<>();
    Entity sampleEntity = new ImageEntity("dino", "file:data/games-library/example-dino/blue_square.jpg", 0.0, 0.0, 100.0, 100.0);
    sampleEntity.setMovementBehaviors(List.of(new GravityEffect(0,20.0/1000)));
    double speed = 160.0;
    speed = speed / 1000.0;
    double maxSpeed = speed * 2;
    Map<String, List<Effect>> controls = new HashMap<>();
    controls.put("UpKey2Pressed",List.of(new JumpEffect(-1 * 3.2 * speed)));
//    controls.put("UpKey",List.of(new MoveInputBehavior(0,-1 * speed)));
//    controls.put("DownKey",List.of(new MoveInputBehavior(0,speed)));
//    controls.put("LeftKey",List.of(new MoveInputBehavior(-1 * speed,0)));
//    controls.put("RightKey",List.of(new MoveInputBehavior(speed,0)));

//    controls.put("UpKey",List.of(new VelocityInputBehavior(0,-1 * speed,maxSpeed)));
    controls.put("DownKey2",List.of(new VelocityInputEffect(0,speed,maxSpeed)));
    controls.put("LeftKey2",List.of(new VelocityInputEffect(-1 * speed,0,maxSpeed)));
    controls.put("RightKey2",List.of(new VelocityInputEffect(speed,0,maxSpeed)));

    sampleEntity.setControlsBehaviors(controls);
    sampleEntity.setCollisionBehaviors(Map.of("cactus",List.of(new RunIntoTerrain(new ArrayList<>())),"block",List.of(new RunIntoTerrain(new ArrayList<>()))));
//    sampleEntity.setPosition(List.of(600.0-100-1,400.0));
    sampleEntity.setPosition(List.of(600.0,500.0-sampleEntity.getHeight()-1));
    entities.add(sampleEntity);

    Entity destructibleEntity = new ImageEntity("block","file:data/games-library/example-dino/cactus.jpeg", 0.0, 0.0, 100.0, 100.0);
//    destructibleEntity.setCollisionBehaviors(Map.of("dino",List.of(new CreateEntity(new ArrayList<>()))));
    destructibleEntity.setPosition(List.of(300.0,300.0));
    entities.add(destructibleEntity);


    for (int i = 0; i < 10; i ++) {
      Entity otherEntity = new ImageEntity("cactus","file:data/games-library/example-dino/mario_brick.png", 0.0, 0.0, 100.0, 100.0);
      otherEntity.setPosition(List.of(100 + 100.0 * i,600.0));
      entities.add(otherEntity);
    }
    Entity otherEntity = new ImageEntity("cactus","file:data/games-library/example-dino/mario_brick.png", 0.0, 0.0, 100.0, 100.0);
//      otherEntity.setMovementBehaviors(List.of(new MoveForwardBehavior(-450.0/1000,0)));
    otherEntity.setPosition(List.of(600.0,500.0));
    entities.add(otherEntity);
    return new OogaGame(new OogaLevel(entities), new VelocityCollisionDetector());
  }
}
