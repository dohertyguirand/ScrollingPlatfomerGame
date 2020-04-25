package ooga.data.gamerecorders;

import ooga.OogaDataException;
import org.w3c.dom.Document;

public interface GameRecorderInternal {

  /**
   * Looks through the user files and finds the path of the save file by the requested user at the requested date
   * @param UserName: Name of the user who made the save file
   * if the requested username isn't listed in the file or doesn't have a save at the given time
   */
  String getLevelFilePath(String UserName, String Date) throws OogaDataException;
}
