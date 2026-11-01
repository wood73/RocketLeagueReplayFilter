package org.wood.replayfilterscene;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.wood.util.Utility;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class ReplayFilterController implements Initializable {

    @FXML
    private MenuItem manualMenuItem;

    @FXML
    private MenuItem githubMenuItem;

    @FXML
    private JFXButton replayParserLocationButton;

    @FXML
    private JFXButton chooseInputFolderButton;

    @FXML
    private JFXButton chooseOutputFolderButton;

    @FXML
    private Label replayParserEXELabel;

    @FXML
    private Label inputFolderLabel;

    @FXML
    private Label outputFolderLabel;

    @FXML
    public TextArea filtersTextArea;

    @FXML
    public JFXButton filterReplaysButton;

    @FXML
    public Label filteringProgressLabel;

    @FXML
    public JFXButton replayToJSONButton;

    @FXML
    CheckBox filterByReplayNameCheckBox;

    public File inputFolder;
    public File outputFolder;
    public File replayParserEXE;

    public Stage stage;

    /** Handles when filter replays button is pressed */
    private FilterReplaysHandler filterReplaysHandler;

    /** Handles when replay to JSON button is pressed */
    private ReplayToJSONHandler replayToJSONHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // if the replay parser exe file is local, automatically set it
        File potentialReplayParser = new File("RocketLeagueReplayParser-win-x64/" +
                "RocketLeagueReplayParser.Console.exe");
        if(potentialReplayParser.exists()) {
            replayParserEXE = potentialReplayParser;
            replayParserEXELabel.setText(replayParserEXE.getAbsoluteFile().getAbsolutePath());
        }

        filterReplaysHandler = new FilterReplaysHandler(this);
        replayToJSONHandler = new ReplayToJSONHandler(this);

        // make it so the most recently pressed button traversable / stay a different color
        replayParserLocationButton.setFocusTraversable(false);
        chooseInputFolderButton.setFocusTraversable(false);
        chooseOutputFolderButton.setFocusTraversable(false);
        filterReplaysButton.setFocusTraversable(false);
        replayToJSONButton.setFocusTraversable(false);

    }

    /** Executes when the Manual menu item is clicked */
    public void manualClicked() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("Manual");
        alert.setContentText("The input folder should be a collection of replays " +
                "that you want to filter from.\n\nThe output folder should be " +
                "where you want filtered replays to be copied to.\n\n" +
                "The 'Filter by whether there's a replay name' checkbox will add " +
                "'ReplayName' to the comma separated filters text area, effectively " +
                "filtering for any .replay files where a custom name was set.\n\n" +
                "The comma separated filters are where you specify the text " +
                "that filters the replays.  For example an input of " +
                "\"spinning, car, wizard\" (excluding quotes) will filter for any .replay files " +
                "that contain all 3 strings of text.  It is case-insensitive, and " +
                "leading and trailing spaces will be removed.\n\n" +
                "The 'Filter Replays' button will start filtering the replays using the " +
                "above settings.\n\n" +
                "If you want to analyze the replay files in the same format this program does, " +
                "you can press the 'Choose Replay to Convert to JSON' button, which " +
                "will prompt you to choose a replay file.  Make sure the " +
                "RocketLeagueReplayParser.Console.exe Location is set.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/alertstyle.css").toExternalForm());
        dialogPane.getStyleClass().add("infoDialog");

        alert.show();

    }

    /** Executes when the githubMenuItem is clicked */
    public void githubClicked() {

        try {
            Desktop.getDesktop().browse(URI.create
                    ("https://github.com/wood73/RocketLeagueReplayFilter/tree/master"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Executes when replayParserLocationButton is pressed */
    public void chooseReplayParserLocation() {

        FileChooser fileChooser = new FileChooser();
        File chosenEXE = fileChooser.showOpenDialog(stage);

        if(chosenEXE != null) {

            if(Utility.hasExtension(chosenEXE, ".exe")) {

                replayParserEXE = chosenEXE;
                replayParserEXELabel.setText(replayParserEXE.getAbsoluteFile().getAbsolutePath());
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText("The chosen file is not an .exe file");
                alert.setContentText("File chosen: " +
                        chosenEXE.getAbsoluteFile().getAbsolutePath());

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/alertstyle.css").toExternalForm());
                dialogPane.getStyleClass().add("notAnEXEDialog");

                alert.show();
            }
        }

    }

    /** Executes when chooseInputFolderButton is clicked */
    public void chooseInputFolder() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File chosenDirectory = directoryChooser.showDialog(stage);

        if(chosenDirectory != null) {
            inputFolder = chosenDirectory;
            inputFolderLabel.setText(chosenDirectory.getAbsoluteFile().getAbsolutePath());
        }

    }

    /** Executes when chooseOutputFolderButton is clicked */
    public void chooseOutputFolder() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File chosenDirectory = directoryChooser.showDialog(stage);

        if(chosenDirectory != null) {
            outputFolder = chosenDirectory;
            outputFolderLabel.setText(chosenDirectory.getAbsoluteFile().getAbsolutePath());
        }

    }

    /** Called when filterByReplayNameCheckBox is clicked, if it's checked, then set
     * the filter text area to "ReplayName" otherwise if unchecked, delete "ReplayName"
     */
    public void filterByReplayName() {

        boolean isChecked = filterByReplayNameCheckBox.isSelected();
        if(isChecked) {
            filtersTextArea.setText("ReplayName");
        }
        else { // if the filtersTextArea has "ReplayName" then delete it

            filtersTextArea.setText(filtersTextArea.getText().replace("ReplayName", ""));
        }
    }

    /** Forwards execution on Filter Replays button click */
    public void filterReplays() {
        filterReplaysHandler.filterReplays();
    }

    /** Forwards execution on replayToJSONButton click */
    public void replayToJSON() { replayToJSONHandler.replayToJSON(); }

    /** Sets to the primary stage from the main method - allows
     * directory / filechoosers to link to the stage
     * @param stage The primary stage
     */
    public void setStage(Stage stage) { this.stage = stage; }

}
