package ooga.data;
import ooga.*;
import ooga.game.Game;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ooga.game.Level;
import ooga.game.OogaLevel;
import ooga.game.behaviors.Effect;
import ooga.game.behaviors.ConditionalBehavior;
import ooga.game.behaviors.Effect;
import ooga.game.behaviors.BehaviorInstance;
import ooga.view.OggaProfile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;

import static java.lang.Class.forName;

/**
 * info @ https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 * TODO: create a better header
 */

public class OogaDataReader implements DataReader{

    //TODO: put all magic strings (especially xml related stuff) in resource file
    private static final Object PATH_TO_CLASSES = "ooga.game.behaviors.";
    private String myLibraryFilePath;   //the path to the folder in which is held every folder for every game that will be displayed and run
    private static final String DEFAULT_LIBRARY_FILE = "data/games-library";
    private static final String DEFAULT_USERS_FILE = "data/users";
    private static final String BEHAVIORS_PROPERTIES_LOCATION = "ooga/data/resources/behaviors";
    private final ResourceBundle myBehaviorsResources = ResourceBundle.getBundle(BEHAVIORS_PROPERTIES_LOCATION);

    public OogaDataReader(String givenFilePath){
        myLibraryFilePath = givenFilePath;
    }
    public OogaDataReader(){
        this(DEFAULT_LIBRARY_FILE);
    }

    @Override
    public List<Thumbnail> getThumbnails() {
        // TODO: when OogaDataReader is constructed, check that libraryFile is a directory and isn't empty and that the gameDirectories aren't empty
        ArrayList<Thumbnail> thumbnailList = new ArrayList<>();
        for (File gameFile : getAllXMLFiles(myLibraryFilePath)){
            try {
                // create a new document to parse
                File fXmlFile = new File(String.valueOf(gameFile));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fXmlFile);

                // find the required information in the document
                String gameTitle = doc.getElementsByTagName("Name").item(0).getTextContent();
                String gameDescription = doc.getElementsByTagName("Description").item(0).getTextContent();
                String gameThumbnailImageName = doc.getElementsByTagName("Thumbnail").item(0).getTextContent();

                String fullImagePath = "file:" + gameFile.getParentFile() + "/" + gameThumbnailImageName;
                Thumbnail newThumbnail = new Thumbnail(fullImagePath, gameTitle, gameDescription);
                thumbnailList.add(newThumbnail);
            } catch (SAXException | ParserConfigurationException | IOException e) {
                // TODO: This ^v is gross get rid of it :) (written by Braeden to Braeden)
                e.printStackTrace();
            }
        }
        return thumbnailList;
    }

    @Override
    public List<List<String>> getBasicGameInfo(String givenGameName) throws OogaDataException {
        List<List<String>> basicGameInfo = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        File gameFile = findGame(givenGameName);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(gameFile);
            String[] outerTagNames = new String[] {"Level", "Variable", "Variable"};
            String[] innerTagNames = new String[] {"ID", "Name", "StartValue"};
            for(int j=0; j<outerTagNames.length; j++){
                NodeList outerList = doc.getElementsByTagName(outerTagNames[j]);
                // add all elements to the corresponding list
                for (int i = 0; i < outerList.getLength(); i++) {
                    Node currentNode = outerList.item(i);
                    Element nodeAsElement = (Element) currentNode;
                    String newItem = nodeAsElement.getElementsByTagName(innerTagNames[j]).item(0).getTextContent();
                    basicGameInfo.get(j).add(newItem);
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            // this error will never happen because it would have happened in findGame()
            throw new OogaDataException("This error should not ever occur");
        }
        return basicGameInfo;
    }

    @Override
    public List<String> getGameFilePaths() {
        ArrayList<String> FilePaths = new ArrayList<>();
        for(File f : getAllXMLFiles(myLibraryFilePath)){
            FilePaths.add(f.getPath());
        }
        return FilePaths;
    }

    /**
     * For Users and for Library, there is one directory holding several smaller directories, each
     * of which represents a game or user with its stored images and .xml files. This method returns a list
     * of those .xml files.
     * @return a list of xml files stored in subdirectories of the given file.
     * @param rootDirectory
     */
    private List<File> getAllXMLFiles(String rootDirectory){
        ArrayList<File> fileList = new ArrayList<>();
        File libraryFile = new File(rootDirectory);
        // loop through the library and find each game
        for (File gameDirectory : Objects.requireNonNull(libraryFile.listFiles())){
            if(!gameDirectory.isDirectory()) continue;
            // go through the game folder and find the game file
            for (File gameFile : Objects.requireNonNull(gameDirectory.listFiles())){
                // check if the file is a .xml
                String[] splitFile = gameFile.getName().split("\\.");
                String fileExtension = splitFile[splitFile.length-1];
                if(fileExtension.equals("xml")) fileList.add(gameFile);
            }
        }
        return fileList;
    }

    private File findGame(String givenGameName) throws OogaDataException {
        List<File> gameFiles = getAllXMLFiles(myLibraryFilePath);
        for(File f : gameFiles) {
            // check if this game file is the correct game file
            try {
                // create a new document to parse
                File fXmlFile = new File(String.valueOf(f));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fXmlFile);
                String gameTitle = doc.getElementsByTagName("Name").item(0).getTextContent();
                if (gameTitle.equals(givenGameName)) return f;
            } catch (SAXException | ParserConfigurationException | IOException e) {
                // TODO: This ^v is gross get rid of it :) (written by Braeden to Braeden)
                e.printStackTrace();
                break;
            }
        }
        throw new OogaDataException("Requested game name not found in Library");
    }


    @Override
    public Level loadLevel(String givenGameName, String givenLevelID) throws OogaDataException {
        ArrayList<Entity> initialEntities = new ArrayList<>();
        File gameFile = findGame(givenGameName);
        Map<String, ImageEntityDefinition> entityMap = getImageEntityMap(givenGameName);
        String nextLevelID = null;
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(gameFile);
            // in the xml create a list of all 'Level' nodes
            NodeList levelNodes = doc.getElementsByTagName("Level");
            // for each check the ID
            for (int i = 0; i < levelNodes.getLength(); i++) {
                Element level = (Element) levelNodes.item(i);
                String levelID = level.getElementsByTagName("ID").item(0).getTextContent();
                if(levelID.equals(givenLevelID)){
                    nextLevelID = level.getElementsByTagName("NextLevel").item(0).getTextContent();
                    //TODO: refactor the below loops into a single loop
                    NodeList imageEntityNodes = level.getElementsByTagName("ImageEntityInstance");
                    // for each, save a copy of the specified instance at the specified place
                    for (int j = 0; j < imageEntityNodes.getLength(); j++) {
                        Node currentEntity = imageEntityNodes.item(j);
                        Element entityElement = (Element) currentEntity;
                        String entityName = entityElement.getElementsByTagName("Type").item(0).getTextContent();
                        String[] parameterNames = new String[] {"XPos", "YPos"};
                        List<Double> parameterValues = constructEntity(entityElement, entityName, parameterNames);
                        int[] rowsAndCols = getRowsAndCols(entityElement);
                        double xPos;
                        double yPos = parameterValues.get(1);
                        for(int row=0; row<rowsAndCols[0]; row++){
                            xPos = parameterValues.get(0);
                            for(int col=0;col<rowsAndCols[1];col++){
                                OogaEntity entity = entityMap.get(entityName).makeInstanceAt(xPos,yPos);
                                entity.setPropertyVariableDependencies(getEntityVariableDependencies(entityElement));
                                entity.setVariables(getEntityVariables(entityElement));
                                initialEntities.add(entity);
                                xPos += entityMap.get(entityName).getMyWidth();
                            }
                            yPos += entityMap.get(entityName).getMyHeight();
                        }
                    }
                    NodeList textEntityNodes = level.getElementsByTagName("TextEntityInstance");
                    for (int j = 0; j < textEntityNodes.getLength(); j++) {
                        Node currentEntity = textEntityNodes.item(j);
                        Element entityElement = (Element) currentEntity;
                        String text = entityElement.getElementsByTagName("Text").item(0).getTextContent();
                        String font = entityElement.getElementsByTagName("Font").item(0).getTextContent();
                        String[] parameterNames = new String[] {"XPos", "YPos", "Width", "Height"};
                        List<Double> parameterValues = constructEntity(entityElement, text, parameterNames);
                        int index = 0;
                        OogaEntity entity = new TextEntity(text, font, parameterValues.get(index++), parameterValues.get(index++),
                                parameterValues.get(index++),  parameterValues.get(index));
                        entity.setPropertyVariableDependencies(getEntityVariableDependencies(entityElement));
                        entity.setVariables(getEntityVariables(entityElement));
                        initialEntities.add(entity);
                    }
                    break;
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            // this error will never happen because it would have happened in findGame()
            throw new OogaDataException("This error should not ever occur");
        }

        OogaLevel oogaLevel = new OogaLevel(initialEntities);
        oogaLevel.setNextLevelID(nextLevelID);
        return oogaLevel;
    }

    private Map<String, String> getEntityVariables(Element entityElement) throws OogaDataException {
        Map<String, String> variableMap = new HashMap<>();
        NodeList nameNodes = entityElement.getElementsByTagName("VariableNames");
        NodeList valueNodes = entityElement.getElementsByTagName("VariableValues");
        if(valueNodes.getLength() != nameNodes.getLength()){
            throw new OogaDataException("Entity variable names and values lists must be same length");
        }
        if(valueNodes.getLength() > 0 && nameNodes.getLength() > 0) {
            String[] variableNames = nameNodes.item(0).getTextContent().split(" ");
            String[] variableValues = valueNodes.item(0).getTextContent().split(" ");
            for(int i=0; i<variableNames.length; i++){
                try {
                    variableMap.put(variableNames[i], variableValues[i]);
                } catch(NumberFormatException e){
                    throw new OogaDataException("Entity variables values must be numeric");
                }
            }
        }
        return variableMap;
    }

    /**
     * gets the rows and columns fields of this entity, each defaults to 1 if not specified
     * @param entityElement element in the xml of this entity
     * @return array of rows, columns
     * @throws OogaDataException if either field is not parsable to an int
     */
    private int[] getRowsAndCols(Element entityElement) throws OogaDataException {
        int[] rowsAndCols = new int[]{1, 1};
        String[] keys = new String[]{"Rows", "Columns"};
        for(int i=0; i<rowsAndCols.length; i++) {
            NodeList nodes = entityElement.getElementsByTagName(keys[i]);
            if (nodes.getLength() > 0) {
                try {
                    rowsAndCols[i] = Integer.parseInt(nodes.item(0).getTextContent());
                } catch(NumberFormatException e){
                    throw new OogaDataException("Row/columns number incorrectly formatted");
                }
            }
        }
        return rowsAndCols;
    }

    private List<Double> constructEntity(Element entityElement, String entityName, String[] parameterNames) {
        List<Double> parameterValues = new ArrayList<>();
        for(String parameterName : parameterNames){
            parameterValues.add(Double.parseDouble(entityElement.getElementsByTagName(parameterName).item(0).getTextContent()));
        }
        System.out.println(String.format("%s @ %f,%f", entityName, parameterValues.get(0), parameterValues.get(1)));
        return parameterValues;
    }

    private Map<String, String> getEntityVariableDependencies(Element entityElement){
        Map<String, String> dependencyMap = new HashMap<>();
        NodeList dependencyList = entityElement.getElementsByTagName("PropertyVariableDependency");
        for(int i=0; i<dependencyList.getLength(); i++){
            Element dependencyElement = (Element)dependencyList.item(i);
            String variableName = dependencyElement.getElementsByTagName("VariableName").item(0).getTextContent();
            String propertyName = dependencyElement.getElementsByTagName("PropertyName").item(0).getTextContent();
            dependencyMap.put(variableName, propertyName);
        }
        return dependencyMap;
    }

    @Override
    public Map<String, ImageEntityDefinition> getImageEntityMap(String gameName) throws OogaDataException {
        Map<String, ImageEntityDefinition> retMap = new HashMap<>();
        File gameFile = findGame(gameName);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(gameFile);
            // in the xml create a list of all 'Level' nodes
            NodeList entityNodes = doc.getElementsByTagName("ImageEntity");
            // add all entities to the map
            for (int i = 0; i < entityNodes.getLength(); i++) {
                // create a new entity
                Node currentEntity = entityNodes.item(i);
                // if the entity has an image, it is an imageEntity
                Element entityElement = (Element) currentEntity;
                // add the ImageEntity to the map
                String newName = entityElement.getElementsByTagName("Name").item(0).getTextContent();
                ImageEntityDefinition newIED = createImageEntityDefinition(entityElement, gameFile.getParentFile().getName());
                newIED.setVariables(getEntityVariables(entityElement));
                retMap.put(newName, newIED);
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            // this error will never happen because it would have happened in findGame()
            throw new OogaDataException("This error should not ever occur");
        }
        return retMap;
    }

    /**
     * Read the .xml file and create a new EntityDefinition as it describes
     * @param entityElement the Node describing the requested Entity
     * @param gameDirectory
     * @return
     */
    private ImageEntityDefinition createImageEntityDefinition(Element entityElement, String gameDirectory) throws OogaDataException {
        String name = entityElement.getElementsByTagName("Name").item(0).getTextContent();
        double height = Double.parseDouble(entityElement.getElementsByTagName("Height").item(0).getTextContent());
        double width = Double.parseDouble(entityElement.getElementsByTagName("Width").item(0).getTextContent());
        String imagePath = "file:" + myLibraryFilePath + "/" + gameDirectory + "/" + entityElement.getElementsByTagName("Image").item(0).getTextContent();
        System.out.print(String.format("Name: %s ", name));

        List<ConditionalBehavior> behaviors = new ArrayList<>();
        NodeList nodeList = entityElement.getElementsByTagName("Behavior");
        for (int i=0; i<nodeList.getLength(); i++){
            Element behaviorElement = (Element) nodeList.item(i);
            Map<String, Double> variableConditions = new HashMap<>();
            //TODO: Make a default for conditions so that if no Requirement is specified, it assumes 'true' is required
            Map<String, Boolean> inputConditions = new HashMap<>();
            Map<String, Boolean> verticalCollisionConditions = new HashMap<>();
            Map<String, Boolean> horizontalCollisionConditions = new HashMap<>();
            addConditions(verticalCollisionConditions, behaviorElement.getElementsByTagName("VerticalCollisionCondition"), "EntityName", "CollisionRequirement");
            addConditions(horizontalCollisionConditions, behaviorElement.getElementsByTagName("HorizontalCollisionCondition"), "EntityName", "CollisionRequirement");
            addConditions(inputConditions, behaviorElement.getElementsByTagName("InputCondition"), "Key", "InputRequirement");
            addVariableConditions(variableConditions, behaviorElement.getElementsByTagName("VariableCondition"));
            NodeList EffectNodes = behaviorElement.getElementsByTagName("Effect");
            List<Effect> effects = new ArrayList<>();
            for(int j=0; j<EffectNodes.getLength(); j++) {
                String[] reactionEffect = EffectNodes.item(j).getTextContent().split(" ");
                Effect effect = (Effect) makeBasicEffect(reactionEffect, "NonCollision");
                effects.add(effect);
            }
            Map<String, List<Effect>> EffectsMap = new HashMap<>();
            NodeList EffectNodes = behaviorElement.getElementsByTagName("Effect");
            for(int j=0; j<EffectNodes.getLength(); j++) {
                String otherEntityName = behaviorElement.getElementsByTagName("With").item(0).getTextContent();
                EffectsMap.put(otherEntityName, new ArrayList<>());
                NodeList reactionEffectNodes = ((Element)EffectNodes.item(j)).getElementsByTagName("Reaction");
                for(int k=0; k<reactionEffectNodes.getLength(); k++) {
                    String[] reactionEffect = reactionEffectNodes.item(k).getTextContent().split(" ");
                    Effect effect = (Effect) makeBasicEffect(reactionEffect, "Collision");
                    EffectsMap.get(otherEntityName).add(effect);
                }
            }
            behaviors.add(new BehaviorInstance(variableConditions, inputConditions, verticalCollisionConditions,
                    horizontalCollisionConditions, EffectsMap, effects));
        }

        return new ImageEntityDefinition(name, height, width, imagePath, behaviors);
    }

    private void addVariableConditions(Map<String, Double> conditionMap, NodeList variableConditionNodes) {
        for(int j=0; j<variableConditionNodes.getLength(); j++){
            String name = ((Element)variableConditionNodes.item(j)).getElementsByTagName("VariableName").item(0).getTextContent();
            String requiredValue = ((Element)variableConditionNodes.item(j)).getElementsByTagName("RequiredValue").item(0).getTextContent();
            conditionMap.put(name, Double.parseDouble(requiredValue));
        }
    }

    private void addConditions(Map<String, Boolean> conditionMap, NodeList verticalCollisionConditionNodes, String keyName, String valueName) {
        for(int j=0; j<verticalCollisionConditionNodes.getLength(); j++){
            String name = ((Element)verticalCollisionConditionNodes.item(j)).getElementsByTagName(keyName).item(0).getTextContent();
            String requirementBoolean = ((Element)verticalCollisionConditionNodes.item(j)).getElementsByTagName(valueName).item(0).getTextContent();
            conditionMap.put(name, Boolean.parseBoolean(requirementBoolean));
        }
    }

    private Object makeBasicEffect(String[] effect, String effectType) throws OogaDataException {
        String effectName = effect[0];
        String effectClassName = myBehaviorsResources.getString(effectName);
        try {
            Class cls = forName(PATH_TO_CLASSES + effectClassName);
            Constructor cons = cls.getConstructor(List.class);
            List<String> list = Arrays.asList(effect).subList(1, effect.length);
            return cons.newInstance(Arrays.asList(effect).subList(1, effect.length));
        } catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException e){
//            e.printStackTrace();
            throw new OogaDataException(effectType + " Behavior listed in game file is not recognized.\n Behavior name: " + effectName);
        } catch(InvocationTargetException e){ // this should be OogaDataException but it won't work because reflection is used
            throw new OogaDataException(effectName + " Effect argument list not formatted correctly");
        }
    }

    @Override
    public List<OggaProfile> getProfiles() {
        // TODO: when OogaDataReader is constructed, check that libraryFile is a directory and isn't empty and that the gameDirectories aren't empty
        ArrayList<OggaProfile> profileList = new ArrayList<>();
        for (File userFile : getAllXMLFiles(DEFAULT_USERS_FILE)){
            try {
                // create a new document to parse
                File fXmlFile = new File(String.valueOf(userFile));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fXmlFile);

                // find the required information in the document
                String userName = doc.getElementsByTagName("Name").item(0).getTextContent();
                String userImage = doc.getElementsByTagName("Image").item(0).getTextContent();

                String fullImagePath = "file:" + userFile.getParentFile() + "/" + userImage;
                OggaProfile newProfile = new OggaProfile();
                newProfile.setProfileName(userName);
                newProfile.setProfilePhoto(fullImagePath);


                profileList.add(newProfile);
            } catch (SAXException | ParserConfigurationException | IOException e) {
                // TODO: This ^v is gross get rid of it :) (written by Braeden to Braeden)
                e.printStackTrace();
            }
        }
        return profileList;
    }

    @Override
    public void saveGameState(String filePath) throws OogaDataException {

    }

    @Override
    public Game loadGameState(String filePath) throws OogaDataException {
        return null;
    }

    @Override
    public String getEntityImage(String entityName, String gameFile) throws OogaDataException {

        return null;
    }
}
