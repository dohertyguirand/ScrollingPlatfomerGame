package ooga.view;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class ProfileTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ViewProfile profile = new ViewProfile();
        profile.setProfileName("Dodo");
        profile.setStats(new HashMap<>(){{
            put("SuperMario", 100);
            put("Dino", 1000000);
            put("Doodle Jump", 3000);
        }});
        profile.showProfile();
        Scene scene = new Scene(profile.getPane());
        stage.setScene(scene);
        stage.show();
    }
}
