<<<<<<< HEAD:tests/ooga/game/collisiondetection/TestVelocityCollisionDetector.java
package ooga.game.collisiondetection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import ooga.Entity;
import ooga.data.ImageEntity;
import org.junit.jupiter.api.Test;

public class TestVelocityCollisionDetector {

  @Test
  void testVerticalCollision() {
    double ySpeed = 1.0;
    double elapsedTime = 1.0;
//    CollisionDetector detector = new VelocityCollisionDetector();
    Entity a = new ImageEntity("a");
    Entity b = new ImageEntity("b");
    a.setPosition(List.of(500.0,500.0));
    b.setPosition(List.of(480.0,500.0-a.getHeight()-1.0));
=======
//package ooga.game;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.util.List;
//import ooga.Entity;
//import ooga.data.ImageEntity;
//import ooga.game.collisiondetection.VelocityCollisionDetector;
//import org.junit.jupiter.api.Test;
//
//public class CollisionDetectionTest {
//
//  @Test
//  void testVerticalCollision() {
//    double ySpeed = 1.0;
//    double elapsedTime = 1.0;
//    CollisionDetector detector = new VelocityCollisionDetector();
//    Entity a = new ImageEntity("a");
//    Entity b = new ImageEntity("b");
//    a.setPosition(List.of(500.0,500.0));
//    b.setPosition(List.of(480.0,500.0-a.getHeight()-1.0));
>>>>>>> chris:tests/ooga/game/CollisionDetectionTest.java
//    b.move(0.0,ySpeed);
//    assertTrue(detector.isColliding(a,b, elapsedTime));
//    assertTrue(detector.isCollidingVertically(a,b, elapsedTime));
//    assertFalse(detector.isCollidingHorizontally(a,b, elapsedTime));
<<<<<<< HEAD:tests/ooga/game/collisiondetection/TestVelocityCollisionDetector.java
  }

  @Test
  void testHorizontalCollision() {
    double xSpeed = 1.0;
    double elapsedTime = 1.0;
//    CollisionDetector detector = new VelocityCollisionDetector();
    Entity a = new ImageEntity("a");
    Entity b = new ImageEntity("b");
    a.setPosition(List.of(500.0,500.0));
    b.setPosition(List.of(500.0-a.getWidth()-1.0,480.0));
=======
//  }
//
//  @Test
//  void testHorizontalCollision() {
//    double xSpeed = 1.0;
//    double elapsedTime = 1.0;
//    CollisionDetector detector = new VelocityCollisionDetector();
//    Entity a = new ImageEntity("a");
//    Entity b = new ImageEntity("b");
//    a.setPosition(List.of(500.0,500.0));
//    b.setPosition(List.of(500.0-a.getWidth()-1.0,480.0));
>>>>>>> chris:tests/ooga/game/CollisionDetectionTest.java
//    b.move(xSpeed,0.0);
//    assertTrue(detector.isColliding(a,b, elapsedTime));
//    assertTrue(detector.isCollidingHorizontally(a,b, elapsedTime));
//    assertFalse(detector.isCollidingVertically(a,b,elapsedTime ));
<<<<<<< HEAD:tests/ooga/game/collisiondetection/TestVelocityCollisionDetector.java
  }

}
=======
//  }
//
//}
>>>>>>> chris:tests/ooga/game/CollisionDetectionTest.java
