final
====

This project implements a player for multiple related games.

Names: Cary Shindell, Sam Thompson, Doherty Guirand, Chris Warren, Braeden Ward


### Timeline

Start Date: 3/31/2020 (Start of plan), 4/4/2020 (Start of coding)

Finish Date: 4/24/2020

Hours Spent: 
* css57 - 85
* stt13 - 70+?
* dg211 - 60+ 
* ccw43 - 55+
* bmw54 - 60+

### Primary Roles

css57 - setting up basic view/visualizer functionality (communication w back end and
updating display, camera), updating and refactoring data reader, conditional behaviors/effects
and collision in back end, fireboy and watergirl game.

stt13 - Game external & internal API, game loop implementation, Entity API and implementation, input processing,
some game file reader and game API refactoring, some effects, helped set up conditional
behaviors, Mario game.

dg211 - View menus and instantiating a game (selecting a profile, a game, and a past save). 
Profile API and implementation in view. Camera shifting. VVVVV game.

ccw43 - View API, setting up profile and selecting a profile in profile API, Game specific behaviors, flappy bird,
dino game, doodle jump

bmw54 - DataReader; The methods that involved writing to or reading from data on the computer.
This included reading and writing xml files as well as navigating and creating directories.

### Resources Used
To evaluate string expressions like "5+5" we used an expression parser we got from
stack overflow. The link is given in ExpressionEvaluator interface.

### Running the Program

Main class: Visualizer. This is the class that extends Application, and communicates with
both data and game.


**Data files needed:**
* All properties files in src/ooga/data/resources, for the data reader to interpret 
files, produce errors, and use reflection.
* src/ooga/game/behaviors/resources/effectdefaults.properties, so that 
that effects know how many arguments they have.
* src/ooga/game/collisiondetection/resources/collisiontypes.properties, so that the 
game can adapt to different collision type codes generated by collision
detectors.
* src/ooga/game/controls/inputs/keyboard.properties, to hold the mappings from
raw inputs to in-game input codes.
* src/ooga/view/Resources to hold the images and CSS style sheets
necessary for formatting the general (not game specific) UI. Also holds the 
language localizations for the UI.


**Features implemented:**

Dark Mode

Profiles

High Scores

Multiple Games at Once

Saving and Loading levels

### Files Used to Test Project and Expected Error Handling

**files used to test the project:**
* All of the 'tests' root folder. Also requires games-library folder with
mario XML file in it, but this is a constant in the ``NonCollisionEffectTest`` class.

The project should be able to read any xml data files without unhandled exceptions (assuming
files have the minimum of correctly closed tags). If the actual contents of the data
file that we parse is badly formatted or something is missing, an error message will
pop up in the UI. 

If an error occurs while the game is running, we do not expect the game to crash.
The program will display an error pop up window, and when that is closed it will
attempt to continue to run the program (things could start to get messy in back end
depending on the error). The same error will probably be thrown again, and if it is,
the error handler will check the type of error that was thrown and only display another
error pop up if the class of the error is different from the previously thrown error.
This is to avoid overloading the UI with error pop ups.

### Notes/Assumptions

**Assumptions or Simplifications:**

We assumed that for dark mode we just needed to change the overall colors, rather than
having a distinct image for each game entity.

We assumed that data files would be in xml format but set up the data reader apis to make
it easy to add data readers for other file types.

We assumed that the user profiles would want to keep track of score and that games would thus
use a Score variable to keep track of whatever information they wanted attached to
player profiles.

We assumed that no games would expect certain sound effects or music to play.
This isn't a hard assumption, but the APIs would have to change slightly in order to support
communication related to sounds.

We assumed that game designers would not have to change the names of 
certain entity variables, such as XPos, XVelocity, width.

We assumed that the user would not want to use their inputs to control multiple games at once, that all
inputs would be buttons or mouse click (no interpretation of analogue like controllers/bumpers, and no
interpretation of mouse movement or mouse button releases).

We assumed that the game designer would be responsible for accommodating multiple languages. All of
the non-game specific text can be put to another language. The text entities that are created by each game cannot. 

**Interesting data files:** See ``data/games-library`` after our demo (by May 3).

**Known Bugs:** 
* As of 4/25/2020, a collision between two entities will cause all onscreen entities
with a similar reaction to activate their effect. (Ex: Stomping on a koopa kills all onscreen koopas).
This is not exactly a bug, as this is just the default behavior for when no Entity1 is provided in a collision
condition. The solution is to put SELF for the text content of Entity1 in a collision condition.
* As of 4/26/2020, a new profile can only be added if the profile name is original, this bug is listed as a todo in XMLProfileReader. The
new profile's photo will show up as default photo but shows up as selected photo when program is reran. 

Extra credit: You can choose which game you want to play and get a preview of each from
the selection screen. 

Game language can be changed in the UI.


### Impressions

(css57) Some of the extension features were interesting but it seems like it might be better to
have game-specific extensions because we thought it was much better to improve our design
and make our back end more flexible. For example, rather than adding a social center,
which is cool but doesn't add much to design, we decided to implement a camera because
it's a key feature of platformers. So it might make sense to specify some extensions
based on the type of game?

(stt13): Same as above. I could easily imagine making a much less functional,
but still well-designed, "core" project, then adding a lot of the features from the 
checklist of 'extra' features. Instead, we opted for a very expansive and flexible
core, which meant that fewer resources went to things like saving, loading, multiple views,
DLC, profiles. Also, in the specification, it lists 'Example Games' alongside the other 
extra features. Does that mean that having the required number of games counts toward that? If not,
why are they in the same list?

(dg211) Same as above. I think that having a very expansive "core" project (being able to implement a wide range of games), 
was a very important thing that we were able to accomplish. Now that our game design is very robust, in further development we
could add new features like a social center. Our view would be able to handle these new features.

(ccw43) I agree with the others, there were many game specific extensions that were more important than getting
some of the extensions that were listed on the website. We did a good job of making it as flexible as possible to add whatever
scroller game we would want, but there was not enough time to do so. It was harder working remotely because peer coding had to
be done over zoom which was a new dynamic added.

(bmw54) I really enjoyed this project. Out of all of the projects so far, this one
felt the most fulfilling and our final product was the cleanest and most fun of the 
projects to date. While it was difficult to generalize our design to work for so many 
different types of games and we did have to alter our design one a few occasions to fit this
flexibility, the challenge resulted in a final project that was very well made and could 
run a surprisingly huge library of games and levels. I would agree with the others that
this flexibility and robustness was our main accomplishment and that every other feature
felt more like additions to make when that was accomplished.