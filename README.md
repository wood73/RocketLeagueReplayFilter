<div align="center">
  <!-- <a href="https://github.com/wood73/RocketLeagueReplayFilter"> -->

  

<h3 align="center">Rocket League Replay Filter</h3>

  <p align="center">
    This program enables Rocket League replays to be filtered (copied) to an output folder depending on 
    whether inputted text matches what's in the .replay files (after the .replay files have been converted to 
    readable JSON format, thanks to jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>).
    <br />
    <br />
    <a href="https://github.com/wood73/RocketLeagueReplayFilter/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
  </p>
</div>

## About The Project
<img src="images/RocketLeagueReplayFilter Window.png" alt="program window">
This project first started when I wanted to filter replays based off of titles used in game, but I realized there were more uses:
* Filtering replays that have been given a name ("ReplayName" will be present in any replay given a name- even replays from 2017)
* Filtering replays based on usernames
<br />
# Breakdown of the Graphical User Interface
* The **Manual** button will also breakdown the GUI.
* The **Pick RocketLeagueReplayParser.Console.exe Location** button isn't needed by default, since this program comes packaged with
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>, and it'll find the path automatically.
* The **Choose Input Folder** button will prompt you to pick a folder, that should be a collection of replays that you want to filter from.
* The **Choose Output Folder** button will prompt you to pick a folder, that should be where you want filtered replays to be copied to.
* The **Filter by whether there's a replay name** checkbox will add "ReplayName" to the *Comma Separated Filters text area*, effectively
  filtering for any .replay files where a custom name was set.
* The **Comma Separated Filters** text area is where you specify the text that filters the replays.  For example, an input of
  "spinning, car, wizard" (excluding quotes) will filter for any .replay files that contain all 3 strings of text.  It is case-insensitive,
  and leading and trailing spaces will be removed.
* The **Filter Replays** button will start filtering the replays using the above settings.
* The **Choose Replay File to Convert to JSON** button will prompt you to choose a .replay file to convert into a readable JSON file using
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>.  This can be handy if you want to analyze a replay.
  
<br />
# Additional Information
It takes about 1 minute and 10 seconds to analyze 50 replays.  This program has been tested to work with an input folder of over 5,000 replays.
<br />
If the program tries copying a .replay file into the output folder, and a file with the same name is found in the output folder, it will ask whether you want to overwrite it (prompt will only appear after all replays have been analyzed).
