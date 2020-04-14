package ooga.data;

/**
 * A temporary profile class made so DataReader has something to use for now before I merge with Dodo and get the Profile
 * class that she has already made
 * - Braeden 4/14/20
 */
public class Profile_Temporary {
    private String myName;
    private String myImagePath;

    public Profile_Temporary(String profileName, String imagePath){
        myImagePath = imagePath;
        myName = profileName;
    }
    public String getMyName() {
        return myName;
    }

    public String getMyImagePath() {
        return myImagePath;
    }
}
