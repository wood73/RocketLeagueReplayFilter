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
 * Handles the renameReplaysButton, filterReplaysButton and filterAndRenameReplaysButton presses.
 * If filterReplaysButton or filterAndRenameReplaysButton were pressed, filter replays based on text matches from the
 * text area, and copy matches from the input folder into the output folder.  And if the
 * filterAndRenameReplaysButton or renameReplaysButton were pressed, also set the outputted replay's
 * File System name to mimic their in-game name (if set)
 */
public class RenameOrFilterReplaysHandler {

    private ReplayFilterController controller;

    public RenameOrFilterReplaysHandler(ReplayFilterController controller) {
        this.controller = controller;
    }

    /**
     * Handles the renameReplaysButton, filterReplaysButton and filterAndRenameReplaysButton presses.
     * If filterReplaysButton or filterAndRenameReplaysButton were pressed, filter replays based on text matches from the
     * text area, and copy matches from the input folder into the output folder.  And if the
     * filterAndRenameReplaysButton or renameReplaysButton were pressed, also set the outputted replay's
     * File System name to mimic their in-game name (if set)
     *
     * @param replayCopyButton Whether the renameReplaysButton, filterReplaysButton or filterAndRenameReplaysButton
     *                         was pressed.
     * @throws RuntimeException if a duplicate file is found in the output folder for some reason,
     *                          should already be checked for (duplicate in-game names are handled during renaming)
     */
    public void renameOrFilterReplays(ReplayCopyButtons replayCopyButton) {

        boolean filtersExist = !controller.filtersTextArea.getText().isEmpty();

        // if the output folder is set and there are .replay files already there, ask user if they want to proceed
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
        else if(controller.inputFolder.getAbsoluteFile().getAbsolutePath().equals(
                controller.outputFolder.getAbsoluteFile().getAbsolutePath())) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("The input folder cannot also be the output folder");
            alert.setContentText(" ");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("inputOutputFoldersEqualDialog");

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
        else if(replayCopyButton == ReplayCopyButtons.RENAME_REPLAYS && filtersExist) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Filters are not empty");
            alert.setContentText("The filters text area should be empty before pressing the 'Rename Replays' button." +
                    "  If you want to filter and rename replays at the same time, press the " +
                    "'Filter and Rename Replays' button.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("renameReplaysFiltersNotEmptyDialog");

            alert.show();

        }
        else if(replayCopyButton == ReplayCopyButtons.FILTER_RENAME_REPLAYS && !filtersExist) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Filters are empty");
            alert.setContentText("The filters text area should not be empty before pressing the " +
                    "'Filter and Rename Replays' button.  If you just want to rename replays without filters," +
                    "press the 'Rename Replays' button.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("filterAndRenameReplaysFiltersEmptyDialog");

            alert.show();

        }
        else if(replayCopyButton == ReplayCopyButtons.FILTER_REPLAYS && !filtersExist) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("The filters text area is empty");
            alert.setContentText("On a 'Filter Replays' button press, the filters text area should not be empty.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/alertstyle.css").toExternalForm());
            dialogPane.getStyleClass().add("missingFiltersDialog");

            alert.show();

        }
        else { // settings are set and can start filtering and/or renaming replays

            // disable the filter/rename replays buttons, replayname checkbox and text area until task is complete
            controller.filterReplaysButton.setDisable(true);
            controller.filterAndRenameReplaysButton.setDisable(true);
            controller.renameReplaysButton.setDisable(true);
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

            // start renaming/filtering replays in a separate thread to update progress label
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {

                    for(int i = 0; i < replays.size(); i++) {

                        // update label depending on which ReplayCopyAction button was pressed
                        if(replayCopyButton == ReplayCopyButtons.RENAME_REPLAYS) {
                            updateMessage("Analyzing and copying replay " + (i + 1) + " of " +
                                    replays.size() + ".  Replays renamed " +
                                    "(in output folder): " + numReplayFilesNameChanged.get());
                        }
                        else if(replayCopyButton == ReplayCopyButtons.FILTER_RENAME_REPLAYS) {
                            updateMessage("Analyzing replay " + (i + 1) + " of " + replays.size() +
                                    ".  Matches found and copied: " + numReplayFilesFiltered.get() +
                                    ".  Replays renamed (in output folder): " + numReplayFilesNameChanged.get());
                        }
                        else if(replayCopyButton == ReplayCopyButtons.FILTER_REPLAYS) {
                            updateMessage("Analyzing replay " + (i + 1) + " of " + replays.size() +
                                    ".  Matches found and copied: " + numReplayFilesFiltered.get());
                        }

                        // will hold the output of the replay parser exe turning the .replay into a JSON string
                        String output = Utility.replayToJSON(replays.get(i), controller.replayParserEXE);

                        output = output.toLowerCase();
                        boolean matchesAllFilters = true;
                        String filterText = controller.filtersTextArea.getText().toLowerCase();

                        // if there are filters, and it's one of the 2 filter replays buttons, check if they match
                        if(!filterText.isEmpty() &&
                                (replayCopyButton == ReplayCopyButtons.FILTER_REPLAYS ||
                                        (replayCopyButton == ReplayCopyButtons.FILTER_RENAME_REPLAYS))) {

                            // if the output ever misses a filter match, turn matchesAllFilters to false
                            // and don't copy the .replay file.  both strings have been set to lower case
                            for (String filter : filterText.split(",")) {
                                filter = filter.trim();

                                if (!output.contains(filter)) {
                                    matchesAllFilters = false;
                                }
                            }
                        }

                        // if the replay file passes the filter (or if 'Rename Replays' was pressed),
                        // copy it to the output directory unless the file already exists in the output directory
                        if (matchesAllFilters || replayCopyButton == ReplayCopyButtons.RENAME_REPLAYS) {

                            numReplayFilesFiltered.incrementAndGet();

                            try {

                                // set the replay filename to its old name, then if one of the 2 rename replays buttons
                                // were pressed, and there exists an in-game replay name, update it
                                String replayFileName = replays.get(i).getName();
                                if(replayCopyButton == ReplayCopyButtons.RENAME_REPLAYS ||
                                    replayCopyButton == ReplayCopyButtons.FILTER_RENAME_REPLAYS) {

                                    // get the in-game replay name if it exists
                                    Optional<String> newReplayFileName = Utility.getInGameReplayName(output);
                                    // if it exists, update the replay name to its in-game name
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

                // update label depending on which ReplayCopyAction button was pressed
                if(replayCopyButton == ReplayCopyButtons.RENAME_REPLAYS) {

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files that have been copied to the output folder.  " +
                            numReplayFilesNameChanged.get() + " of them have been renamed.");
                }
                else if(replayCopyButton == ReplayCopyButtons.FILTER_RENAME_REPLAYS) {

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + numReplayFilesFiltered.get() +
                            " matching replay files have been copied to the output folder- with " +
                            numReplayFilesNameChanged.get() + " of them renamed.");
                }
                else if(replayCopyButton == ReplayCopyButtons.FILTER_REPLAYS){

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + numReplayFilesFiltered.get() +
                            " matching replay files have been copied to the output folder.");
                }


                controller.filterReplaysButton.setDisable(false);
                controller.filterAndRenameReplaysButton.setDisable(false);
                controller.renameReplaysButton.setDisable(false);
                controller.filterByReplayNameCheckBox.setDisable(false);
                controller.filtersTextArea.setDisable(false);
            });

            task.setOnFailed(evt -> {
                // on the JavaFX application thread
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setHeaderText("Error occurred during renaming/filtering");
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
