package ooga.view;

import ooga.Profile;

import java.util.Map;

public abstract class OggaProfile implements Profile {
    protected String myProfilePhotoPath;
    protected String myName;
    protected Map<String, Integer> myHighestScores;

    @Override
    public void setProfilePhoto(String photoPath){
        myProfilePhotoPath = photoPath;
    }
    @Override
    public void setProfileName(String name) {
        myName = name;
    }

    @Override
    public void setStats(Map<String,Integer> stats) {
        myHighestScores = stats;
    }

    @Override
    public Map<String,Integer> getStats() {
        return myHighestScores;
    }

    @Override
    public void updateStats(String gameName, Integer highScore) {
        myHighestScores.putIfAbsent(gameName,0);
        myHighestScores.put(gameName,highScore);
    }
    @Override
    public int getGameStats(String gameName) {
        return myHighestScores.get(gameName);
    }

    @Override
    public String getProfileName() {
        return myName;
    }


}
