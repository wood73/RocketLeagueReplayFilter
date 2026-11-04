package org.wood.replayfilterscene;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.apache.commons.io.FileUtils;
import org.wood.util.Utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the filterReplaysButton and filterAndRenameReplaysButton presses.
 * Filters replays based on text matches from the text area, and copies
 * matches from the input folder into the output folder.  And if the
 * filterAndRenameReplaysButton was pressed, also set the filtered replay's
 * File System name to mimic their in-game name (if set)
 */
public class FilterReplaysHandler {

    private ReplayFilterController controller;

    public FilterReplaysHandler(ReplayFilterController controller) {
        this.controller = controller;
    }

    /**
     *  Executes on a Filter Replays button click.  Filters replays based
     *  on text matches from the text area, and copies matches from the input folder
     *  into the output folder.
     *
     * @param renameReplays Whether filtered replay's File System name should be set to
     *                      mimic their in-game name (if set)
     * @throws RuntimeException if a duplicate file is found in the output folder for some reason,
     *                          should already be checked for (duplicate in-game names are handled during renaming)
     */
    public void filterReplays(boolean renameReplays) {


        // if the output folder is set and there are .replay files there, ask user if they want to proceed
        if(controller.outputFolder != null && Utility.anyReplayFilesPresent(controller.outputFolder)) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText(".replay files detected in the output folder");
            alert.setContentText("Do you want to continue even though .replay files have been " +
                            "found in the output folder?");

            alert.getDialogPane().getButtonTypes().setAll(
                    ButtonType.YES,
                    ButtonType.NO
            );

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("continueFilterDespiteReplaysInOutputDialog");

            ButtonType selectedExitOption = alert.showAndWait().get();
            // stop method if user cancels
            if(selectedExitOption == ButtonType.NO) {
                return;
            }

        }


        if(controller.replayParserEXE == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("The replay parser exe file location is not set");
            alert.setContentText(" ");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("missingEXEDialog");

            alert.show();
        }
        else if(controller.inputFolder == null || controller.outputFolder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Either the input folder or output folder is not set");
            alert.setContentText(" ");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("missingFolderDialog");

            alert.show();
        }
        // with empty filters, halt execution on a Filter Replays button click
        // but with a Filter and Rename Replays button click, allow no filters
        else if(controller.filtersTextArea.getText().isEmpty() && !renameReplays) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("The filters text area is empty");
            alert.setContentText(" ");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("missingFiltersDialog");

            alert.show();

        }
        else if(Utility.checkDuplicateReplayFiles(controller.inputFolder, controller.outputFolder)) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Duplicate file(s) found");
            alert.setContentText("Some .replay files from the input folder have the same " +
                    "name as .replay files in the output folder.  Please make sure there are no " +
                    "name collisions before trying again.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("duplicateFileDialog");

            alert.show();

        }
        else { // settings are set and can start filtering replays

            // disable the filter replays buttons, replayname checkbox and text area until task is complete
            controller.filterReplaysButton.setDisable(true);
            controller.filterAndRenameReplaysButton.setDisable(true);
            controller.filterByReplayNameCheckBox.setDisable(true);
            controller.filtersTextArea.setDisable(true);

            // gather all .replay files
            ArrayList<File> replays = new ArrayList<>();
            for(File potentialReplay : controller.inputFolder.listFiles()) {
                // ignore any file that isn't a .replay
                if(Utility.hasExtension(potentialReplay, ".replay")) {
                    replays.add(potentialReplay);
                }
            }

            // counter for replays that pass the filter
            final AtomicInteger numReplayFilesFiltered = new AtomicInteger(0);
            // counter for replays that have had their name changed
            final AtomicInteger numReplayFilesNameChanged = new AtomicInteger(0);

            // start filtering replays in a separate thread to update progress label
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {

                    for(int i = 0; i < replays.size(); i++) {

                        // if renaming replays with no filter, update label differently
                        if(renameReplays && controller.filtersTextArea.getText().isEmpty()) {
                            updateMessage("Analyzing and copying replay " + (i + 1) + " of " +
                                    replays.size() + ".  Replay names changed " +
                                    "(in output folder): " + numReplayFilesNameChanged.get());
                        }
                        // if renaming replays with a filter
                        else if(renameReplays) {
                            updateMessage("Analyzing replay " + (i + 1) + " of " + replays.size() +
                                    ".  Matches found and copied: " + numReplayFilesFiltered.get() +
                                    ".  Replay names changed (in output folder): " + numReplayFilesNameChanged.get());
                        }
                        // if filtering without renaming replays
                        else {
                            updateMessage("Analyzing replay " + (i + 1) + " of " + replays.size() +
                                    ".  Matches found and copied: " + numReplayFilesFiltered.get());
                        }

                        // will hold the output of the replay parser exe turning the .replay into a JSON string
                        String output = Utility.replayToJSON(replays.get(i), controller.replayParserEXE);

                        output = output.toLowerCase();
                        boolean matchesAllFilters = true;
                        String filterText = controller.filtersTextArea.getText().toLowerCase();

                        // if there are filters, check if they match
                        if(!filterText.isEmpty()) {
                            // if the output ever misses a filter match, turn matchesAllFilters to false
                            // and don't copy the .replay file.  both strings have been set to lower case
                            for (String filter : filterText.split(",")) {
                                filter = filter.trim();

                                if (!output.contains(filter)) {
                                    matchesAllFilters = false;
                                }
                            }
                        }

                        // if the replay file passes the filter (or has none and renameReplays is true),
                        // copy it to the output directory unless the file already exists in the output directory
                        if (matchesAllFilters) {

                            numReplayFilesFiltered.incrementAndGet();

                            try {

                                // set the replay filename to its old name, but if renameReplays is true
                                // and there exists an in-game replay name, update it
                                String replayFileName = replays.get(i).getName();
                                if(renameReplays) {
                                    Optional<String> newReplayFileName = Utility.getInGameReplayName(output);
                                    if(newReplayFileName.isPresent()) {
                                        // make sure the filename is unique
                                        // (otherwise keep adding a number at the end until it is)
                                        replayFileName = Utility.getUniqueReplayFile(new File(controller.outputFolder
                                                .getAbsoluteFile().getAbsolutePath() + "/" +
                                                newReplayFileName.get())).getName();

                                        numReplayFilesNameChanged.incrementAndGet();
                                    }
                                }

                                File destination = new File(controller.outputFolder
                                        .getAbsoluteFile().getAbsolutePath() + "/" +
                                        replayFileName);

                                // duplicates have already been checked for, if the file already exists,
                                // something has gone very wrong. absoluteFile.exists() is more reliable as per SO
                                if (destination.getAbsoluteFile().exists()) {

                                    // creates an error dialog
                                    throw new RuntimeException("Duplicate files should have already been checked for " +
                                            "before filtering began, this error message should never run. " +
                                            "Maybe report this as a bug, thanks.\n\nDuplicate file: " +
                                            destination.getAbsoluteFile().getAbsolutePath());

                                } else {  // if there is no duplicate file

                                    FileUtils.copyFile(replays.get(i), destination);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    return null;
                }

            };

            task.setOnSucceeded(evt -> {
                // on the JavaFX application thread

                // unbind so the label so it both can be set normally again, and stops listening to the task
                controller.filteringProgressLabel.textProperty().unbind();

                if(renameReplays && controller.filtersTextArea.getText().isEmpty()) {

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files that have been copied to the output folder.  " +
                            numReplayFilesNameChanged.get() + " of them have been given a name change.");
                }
                else if(renameReplays) { // if renameReplays is true, but there are also filters

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + numReplayFilesFiltered.get() +
                            " matching replay files have been copied to the output folder- with " +
                            numReplayFilesNameChanged.get() + " of them given a name change.");
                }
                else { // renameReplays is false
                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + numReplayFilesFiltered.get() +
                            " matching replay files have been copied to the output folder.");
                }


                controller.filterReplaysButton.setDisable(false);
                controller.filterAndRenameReplaysButton.setDisable(false);
                controller.filterByReplayNameCheckBox.setDisable(false);
                controller.filtersTextArea.setDisable(false);
            });

            task.setOnFailed(evt -> {
                // on the JavaFX application thread
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setHeaderText("Error occurred during filtering");
                alert.setContentText("Error: " + task.getException().getMessage());

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/alertstyle.css").toExternalForm());
                dialogPane.getStyleClass().add("errorFilteringDialog");

                alert.show();
            });

            // bind the label so it can be set from within the task thread
            controller.filteringProgressLabel.textProperty().bind(task.messageProperty());

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        }

    }

}
