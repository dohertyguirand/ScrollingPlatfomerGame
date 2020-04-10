package ooga.data;

import ooga.Entity;
import ooga.OogaDataException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a class that just tests DataReader and makes sure it is working correctly.
 * @author braedenward
 */
public class DataReaderTest {
    private OogaDataReader testDataReader = new OogaDataReader();
    private String GAME_NAME = "Chrome Dino";
    //private String GAME_NAME = "Super Mario Bros";
    private ArrayList<String> ID_LIST  = new ArrayList<>(List.of("1"));

    @Test
    public void testGetThumbnails(){
        List<Thumbnail> thumbnailList = testDataReader.getThumbnails();
        for (Thumbnail t : thumbnailList) {
            System.out.println(String.format("Title: %s \nDescription: %s \nImage: %s\n", t.getTitle(), t.getDescription(), t.getImageFile()));
        }
    }
    @Test
    public void testGetBasicGameInfo(){
        List<String> stringList = null;
        try {
            stringList = testDataReader.getBasicGameInfo(GAME_NAME);
        } catch (OogaDataException e) {
            // TODO: Fix this, Braeden
            System.out.println("Test Failed");
            e.printStackTrace();
        }
        System.out.println("List of Level IDs recieved for " + GAME_NAME + ": " + stringList + "\n");
        assertEquals(ID_LIST, stringList);
    }

    @Test
    public void testLoadLevel(){
        boolean testPassed = true;
        for(String id : ID_LIST){
            try {
                testDataReader.loadLevel(GAME_NAME, id);
            } catch (OogaDataException e) {
                // TODO: Fix this, Braeden
                testPassed = false;
                e.printStackTrace();
            }
        }
        assertTrue(testPassed);
    }

    @Test
    public void testGetGameFilePaths(){
        List<String> pathList = testDataReader.getGameFilePaths();
        System.out.println(pathList);
    }

    @Test
    public void testGetEntityMap(){
        Map<String, ImageEntityDefinition> retMap = null;
        try {
            retMap = testDataReader.getEntityMap(GAME_NAME);
        } catch (OogaDataException e) {
            e.printStackTrace();
            fail();
        }
        for(String key : retMap.keySet()){
            Entity e = retMap.get(key).makeInstanceAt(0.0,0.0);
            System.out.print("Name: "+ key + "   ");
            System.out.print("Height: " + e.getHeight()+"   ");
            System.out.println("Width: " + e.getWidth());
        }
        System.out.println(retMap);
    }

}
