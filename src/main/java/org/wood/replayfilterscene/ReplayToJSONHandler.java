package org.wood.replayfilterscene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.FileChooser;
import org.wood.util.Utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles when the replayToJSONButton is pressed.
 * Uses RocketLeagueReplayParser.Console.exe to convert a .replay file to JSON format.
 * Prompts user to select the input file, then outputs the JSON file to a local
 * 'JSON output' directory (doesn't overwrite without permission)
 */
public class ReplayToJSONHandler {

    private ReplayFilterController replayFilterController;

    /** The output directory for the JSON file */
    private final static File LOCAL_JSON_FOLDER = new File("JSON output");

    public ReplayToJSONHandler(ReplayFilterController replayFilterController) {

        this.replayFilterController = replayFilterController;

        if(!LOCAL_JSON_FOLDER.exists()) {
            LOCAL_JSON_FOLDER.mkdir();
        }
    }

    /** Triggers when replayTOJSONButton is pressed, prompts user to
     * open a replay file to then convert to a JSON file and throw into
     * a local "JSON output" folder.
     */
    public void replayToJSON() {

        // if replayParserEXE isn't set, notify user and stop the method
        if(replayFilterController.replayParserEXE == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("The replay parser exe file location is not set");
            alert.setContentText("Click the 'Pick RocketLeagueReplayParser.Console.exe" +
                    " Location' button.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("missingEXEDialog");

            alert.show();

            return;
        }

        FileChooser fileChooser = new FileChooser();
        File chosenReplay = fileChooser.showOpenDialog(replayFilterController.stage);

        if(chosenReplay != null) {

            if (Utility.hasExtension(chosenReplay, ".replay")) {

                String output = Utility.replayToJSON(chosenReplay,
                        replayFilterController.replayParserEXE);

                // have the output file be in the local JSON folder, with the same name
                // as the .replay file, just with a .json extension
                File outputFile = Utility.replaceExtension(LOCAL_JSON_FOLDER.getAbsoluteFile()
                                .getAbsolutePath() + "/" + chosenReplay.getName(), ".json");

                // whether to write the output to file - can be set to false if file already exists
                // and user doesn't want to overwrite
                boolean writeToFile = true;

                if(outputFile.exists()) {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("");
                    alert.setHeaderText("Duplicate File Found");
                    alert.setContentText("The file '" + outputFile.getAbsoluteFile()
                            .getAbsolutePath() + "' already exists.\n\nOverwrite this file?");

                    alert.getDialogPane().getButtonTypes().setAll(
                            ButtonType.YES,
                            ButtonType.CANCEL
                        );

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/alertstyle.css")
                                    .toExternalForm());
                    dialogPane.getStyleClass().add("duplicateFileDialog");

                    ButtonType selectedExitOption = alert.showAndWait().get();

                    writeToFile = selectedExitOption == ButtonType.YES;
                }


                if(writeToFile) {

                    try(PrintWriter pw = new PrintWriter(outputFile)) {
                        pw.print(output);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Successfully parsed  the .replay file to JSON");
                    alert.setContentText("The JSON file is at '" +
                            outputFile.getAbsoluteFile().getAbsolutePath() + "'");

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/alertstyle.css").toExternalForm());
                    dialogPane.getStyleClass().add("replayToJSONDialog");

                    alert.show();

                }

            } else {  // chosen file was not a .replay

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText("The chosen file is not a .replay file");
                alert.setContentText("File chosen: " +
                        chosenReplay.getAbsoluteFile().getAbsolutePath());

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/alertstyle.css").toExternalForm());
                dialogPane.getStyleClass().add("notAReplayDialog");

                alert.show();
            }

        }

    }

}
