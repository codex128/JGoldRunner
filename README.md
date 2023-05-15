# JGoldRunner
A spin-off of LodeRunner built with JMonkeyEngine3.

# Download
1. Download the zip file for your system from the releases. Windows is currently not supported.
2. Extract the zip file.
3. Run the launcher in the resulting folder.

# Game Instructions
* Move with Arrow Keys.
* Dig brick down + left/right with A and D (which creates a hole).
* Boost by pressing SPACE.
* Collect all the gold and then make it to the top of the level.
* Your energy bar fills when you collect a gold.
* Do not get caught by the enemies, or the level will restart.
* Restart the current level at any time with R.
* Quit the current level at any time with BACKSPACE.
* Advance to the next level at any time with END.

# Additional Tips
* You can fall through holes you dig, but enemies will get stuck inside. This is how you avoid enemies.
* You can walk on **top** of enemies without getting caught. The only exception to this rule are ladders, where the enemies climb up into you.
* Enemies can pick up gold, too. You can force them to drop their gold by putting them in a hole.
* After a bit, an enemy may try to climb out of a hole he fell in. You can stand on top of him to stop this.

# Settings
* Enable/disable boosting.
* Enable/disable in-game music.
* Speedrun Display: the clock will display total time for the entire package, instead of time for the current level.
* ExpertMode: enemies not stopped by holes. You gotta be on your toes if you enable this!
* SluggishMode (will be removed): Makes you slower than the enemies.

# Platform Support
This game is supported primarily on Linux.
MacOS has not been tested, but is expected to work.
Windows is **not** supported yet.

# Save Data
Save data will be stored in ".goldrunnerdata" in the user's home folder.<br>
Exported level packages will be stored under ".goldrunnerdata/exports".

# Sources
Download "JGoldRunner-sources.zip" from the releases.

Dependencies:
* JMonkeyEngine 3.5+
* Lemur 1.16.0+
* J3map 0.0.1+ (https://github.com/codex128/J3map/releases/tag/v0.0.1)
* JmeUtilityLibrary 0.0.1+ (https://github.com/codex128/JmeUtilityLibrary/releases/tag/v0.0.1)

# Known Issues
* GUI does not support larger or smaller window sizes.
* Gold visuals sometimes disappear after an enemy drops it.
* Multiple heros can be added after closing and opening the level editor.
* JMonkeyEngine3.6: level editor seems to be crashing on exit.
* Package Editor: swapping levels crashes during swap or cancel.
* Level Editor: Gold is too difficult to see.
