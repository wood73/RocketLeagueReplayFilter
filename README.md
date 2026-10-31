<a id="readme-top"></a>

<div align="center">

<h3 align="center">Rocket League Replay Filter</h3>

  <p align="center">
    <a href="https://github.com/wood73/RocketLeagueReplayFilter/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
  </p>
</div>

<br />
<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#breakdown-of-the-graphical-user-interface">Breakdown of the Graphical User Interface</a></li>
    <li><a href="#additional-information">Additional Information</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project

<img src="images/RocketLeagueReplayFilter Window.png" alt="program window">
This program enables Rocket League replays to be filtered (copied) to an output folder depending on 
whether inputted text matches what's in the .replay files (after the .replay files have automatically been converted to 
readable JSON format, thanks to jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>).
<br /> <br />

Use cases:

* Filtering replays that have been given a name ("ReplayName" will be present in any replay given a name- even replays from 2017)

* Filtering replays based on titles used in game
  
* Filtering replays based on usernames
<br /><br />

## Breakdown of the Graphical User Interface

* The __Manual__ button will also breakdown the GUI.

* The **Pick RocketLeagueReplayParser.Console.exe Location** button isn't needed by default, since this program comes packaged with
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a> (in releases, not the repo), and it'll find the path automatically.
  
* The **Choose Input Folder** button will prompt you to pick a folder, that should be a collection of replays that you want to filter from.
  
* The **Choose Output Folder** button will prompt you to pick a folder, that should be where you want filtered replays to be copied to.
  
* The **Filter by whether there's a replay name** checkbox will add "ReplayName" to the **Comma Separated Filters text area**, effectively
  filtering for any .replay files where a custom name was set.
  
* The **Comma Separated Filters** text area is where you specify the text that filters the replays.  For example, an input of
  "spinning, car, wizard" (excluding quotes) will filter for any .replay files that contain all 3 strings of text.  It is case-insensitive,
  and leading and trailing spaces will be removed from each string of text.
  
* The **Filter Replays** button will start filtering the replays using the above settings.
  
* The **Choose Replay File to Convert to JSON** button will prompt you to choose a .replay file to convert into a readable JSON file using
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>.  This can be handy if you want to analyze a replay.
  
<br /><br />

## Additional Information

It takes about 1 minute and 10 seconds to analyze 50 replays, and progress gets updated in the GUI.  It should be able to handle any number of .replay files in the input folder, and it doesn't overwrite files without permission.
<br /><br />
If you're on Steam you may need to temporarily disable Steam Cloud if you remove old replays and don't want them re-added from the cloud.  I'm not sure about Epic's Launcher.
<br /><br />

## Installation

For Windows x64, see the [releases](https://github.com/wood73/RocketLeagueReplayFilter/releases) page.

<br /><br />

## Built With

* **Replay Parser:** jjbott's [RocketLeagueReplayParser](https://github.com/jjbott/RocketLeagueReplayParser) with the repo built to an .exe during October 2026.

* **GUI Framework:** [JavaFX 27](https://openjfx.io)

* **UI Component Library:** [JFoenix](https://github.com/sshahine/JFoenix)
  
* **Fat JAR:** Built using standard [Java JDK 27](https://oracle.com)
  
* **Windows Executable:** Packaged via **jpackage** using [Azul ZuluFX JDK 27](https://azul.com) (includes embedded JavaFX).

<br /><br />

## Acknowledgements

This wouldn't have been possible without jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>, my thanks to those contributors.

<br /><br />

## License

Distributed under the MIT License. See [License](https://github.com/wood73/RocketLeagueReplayFilter/blob/master/LICENSE) for more information.

<br /><br />
<p align="left">(<a href="#readme-top">back to top</a>)</p>
