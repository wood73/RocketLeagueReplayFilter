<a id="readme-top"></a>

<div align="center">

<h3 align="center">Rocket League Replay Filter</h3>

  <p align="center">
    <a href="https://github.com/wood73/RocketLeagueReplayFilter/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/wood73/RocketLeagueReplayFilter/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

<br />
<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#breakdown-of-the-graphical-user-interface">Breakdown of the Graphical User Interface</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#additional-information">Additional Information</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#Dev-info">Dev info</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project

<img src="images/RLReplayFilter Window.PNG" alt="program window">
This program enables Rocket League replays to be copied to an output folder with their File System names renamed to their in-game names (with support for replays that share the same in-game name (by incrementing a number at the end of the file name)).  It can also filter which .replay files get copied to the output folder depending on 
whether inputted text matches what's in the .replay files (after the .replay files have automatically been converted to 
readable JSON format, thanks to jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>).
<br /> <br />

Use cases:

* Renaming replay's File System names to match their in-game names

* Filtering for replays that have been given an in-game name ("ReplayName" will be present in any replay given a name- even replays from 2017)

* Filtering for replays based on titles used in game
  
* Filtering for replays based on usernames
<br /><br />

## Breakdown of the Graphical User Interface

* The **Pick RocketLeagueReplayParser.Console.exe Location** button isn't needed by default, since this program comes packaged with
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a> (in releases, not the repo), and it'll find the path automatically.
  
* The **Choose Input Folder** button will prompt you to pick a folder, that should be a collection of replays that you want to filter/rename from.
  
* The **Choose Output Folder** button will prompt you to pick a folder, that should be where you want filtered/renamed replays to be copied to.
  
* The **Filter by whether there's a replay name** checkbox will add "ReplayName" to the **Comma Separated Filters text area**, effectively
  filtering for any .replay files where an in-game name was set.
  
* The **Comma Separated Filters** text area is where you specify the text that filters the replays.  For example, an input of
  "spinning, car, wizard" (excluding quotes) will filter for any .replay files that contain all 3 strings of text.  It is case-insensitive,
  and leading and trailing spaces will be removed from each string of text.

* The **Rename Replays** button does not use filters, and will copy all replays to the output folder, then rename them to their in-game name (if   present).

* The **Filter and Rename Replays** button will start filtering the replays using the above settings, and will rename replays in the output folder to match their in-game name (if present).
  
* The **Filter Replays** button will start filtering the replays using the above settings.
  
* The **Choose Replay File to Convert to JSON** button will prompt you to choose a .replay file to convert into a readable JSON file using
  jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>.  This can be handy if you want to analyze a replay.
  
<br /><br />

## Usage

To rename .replay files on the File System to their in-game names, just set the **Input Folder** and **Output Folder** and click **Rename Replays**, and the renamed replays will appear in the output folder (along with replays that don't have an in-game name).
<br /><br />
To rename .replay files on the File System to their in-game names and sort them from replays without an in-game name, make sure the input and output folders are set, and check the **Filter by whether there's a replay name** checkbox, and click the **Filter and Rename Replays** button.

<br /><br />

## Additional Information

It takes about 1 minute and 10 seconds to analyze 50 replays, and progress gets updated in the GUI.  It should be able to handle any number of .replay files in the input folder, and it doesn't overwrite files without permission.
<br /><br />
If you're on Steam you may need to temporarily disable Steam Cloud if you remove old replays and don't want them re-added from the cloud.  I'm not sure about Epic's Launcher.
<br /><br />

## Installation

For Windows x64, see the [releases](https://github.com/wood73/RocketLeagueReplayFilter/releases) page.

<br /><br />

## Roadmap

- [x] Instead of **Filter and Rename Replays** handling both renaming with and without a filter, add a separate button to handle renaming without a filter.
- [ ] Add a way to filter using OR logic, not just AND; maybe done by wrapping the OR operands in parentheses within the **Filters text area**.

<br /><br />

## Contributing

Contributions are welcome - if you have a suggestion feel free to <a href="https://github.com/wood73/RocketLeagueReplayFilter/issues/new?labels=enhancement&template=feature-request---.md">open an issue</a> with the enhancement label/tag, and maybe fork the repo and make a pull request.

<br /><br />

## Dev info

For getting started, see what this program is <a href="#built-with">built with</a>, and you'll need to get an updated version of RocketLeagueReplayParser.Console.exe, since RocketLeagueReplayParser's latest [release](https://github.com/jjbott/RocketLeagueReplayParser/releases) 3.11.0 is out of date with its repository.  I tested 3.11.0 to partially work with older (2019 & 2023) .replay files, but it broke with .replay files from October 2026.  You can get RocketLeagueReplayParser.Console.exe either by downloading the October 2026 version that comes with this program in [releases](https://github.com/wood73/RocketLeagueReplayFilter/releases), or by building it.
<br /><br />
For testing the program, I created the [Testing with replays](https://github.com/wood73/RocketLeagueReplayFilter/tree/master/Testing%20with%20replays) folder, which has two folders of replays, and [Testing procedure.txt](https://github.com/wood73/RocketLeagueReplayFilter/blob/master/Testing%20with%20replays/Testing%20procedure.txt), which goes over the expected inputs and outputs of several tests with different settings.  
<br /><br />
There are also some JUnit tests for the Utility class in the tests folder.

<br /><br />

## Built With

* **Replay Parser:** jjbott's [RocketLeagueReplayParser](https://github.com/jjbott/RocketLeagueReplayParser) with the repo built to an .exe during October 2026.

* **GUI Framework:** [JavaFX 27](https://openjfx.io)

* **UI Component Library:** [JFoenix](https://github.com/sshahine/JFoenix)
  
* **Fat JAR:** Built using standard [Java JDK 27](https://oracle.com)
  
* **Windows Executable:** Packaged via **jpackage** using [Azul ZuluFX JDK 27](https://azul.com) (includes embedded JavaFX).

<br /><br />

## Contact

three3pade3@gmail.com

<br /><br />

## Acknowledgements

This wouldn't have been possible without jjbott's <a href="https://github.com/jjbott/RocketLeagueReplayParser">RocketLeagueReplayParser</a>, my thanks to those contributors.

<br /><br />

## License

Distributed under the MIT License. See [License](https://github.com/wood73/RocketLeagueReplayFilter/blob/master/LICENSE) for more information.

<br /><br />
<p align="left">(<a href="#readme-top">back to top</a>)</p>
