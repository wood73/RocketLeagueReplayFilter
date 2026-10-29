package org.wood.replayfilterscene;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.apache.commons.io.FileUtils;
import org.wood.util.Utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the filterReplaysButton press.
 * Filters replays based on text matches from the text area, and copies
 * matches from the input folder into the output folder.
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
     */
    public void filterReplays() {

        // will hold replays from the input folder that pass the filter but have a duplicate in the output folder
        // the keys are the input files and values are the destination files
        // will later prompt user if they want to overwrite (can't prompt user within task thread)
        final Map<File, File> filteredReplaysThatHaveDuplicate = new HashMap<>();

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
        else if(controller.filtersTextArea.getText().isEmpty()) {

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
        else { // settings are set and can start filtering replays

            // disable the filter replays button until task is complete
            controller.filterReplaysButton.setDisable(true);

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
            // whether at least 1 file has been copied to the output folder
            final AtomicBoolean hasCopiedFiles = new AtomicBoolean(false);

            // start filtering replays in a separate thread to update progress label
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {

                    for(int i = 0; i < replays.size(); i++) {

                        updateMessage("Analyzing replay " + (i + 1) + " of " + replays.size() +
                                ".  Matches found: " + numReplayFilesFiltered.get());

                        // will hold the output of the replay parser exe turning the .replay into a JSON string
                        String output = Utility.replayToJSON(replays.get(i), controller.replayParserEXE);

                        output = output.toLowerCase();
                        boolean matchesAllFilters = true;
                        String filterText = controller.filtersTextArea.getText().toLowerCase();
                        // if the output ever misses a filter match, turn matchesAllFilters to false
                        // and don't copy the .replay file.  both strings have been set to lower case
                        for (String filter : filterText.split(",")) {
                            filter = filter.trim();

                            if (!output.contains(filter)) {
                                matchesAllFilters = false;
                            }
                        }

                        // if the replay file passes the filter, copy it to the output directory
                        // unless the file already exists in the output directory
                        if (matchesAllFilters) {

                            numReplayFilesFiltered.incrementAndGet();

                            try {
                                File destination = new File(controller.outputFolder
                                        .getAbsoluteFile().getAbsolutePath() + "/" +
                                        replays.get(i).getName());

                                // if the file already exists, add to list and prompt user about overwriting after this
                                // task is finished (can't prompt user from this thread)
                                if (destination.exists()) {
                                    filteredReplaysThatHaveDuplicate.put(replays.get(i), destination);

                                } else {  // if there is no duplicate file
                                    FileUtils.copyFile(replays.get(i), destination);
                                    hasCopiedFiles.set(true);
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

                // if there are no duplicate replays in the output folder
                if(filteredReplaysThatHaveDuplicate.isEmpty()) {

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + numReplayFilesFiltered.get() +
                            " matching replay files have been copied to the output folder.");
                }
                else { // there are duplicate files and need to prompt the user whether to overwrite

                    controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                            " replay files.  " + (hasCopiedFiles.get() ? "Some file(s) have been copied to " +
                        "the output folder,\nbut some duplicate file(s) have been found." :
                            "Some duplicate file(s) have been found."));

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Duplicate File(s) Found");
                    alert.setContentText("There are some replay files in the input folder that " +
                            "have the same name as files in the output folder.\n\nOverwrite " +
                            filteredReplaysThatHaveDuplicate.size() + " files?\n\nApplication might freeze " +
                            "while files are being overwritten.");

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

                    // overwrite the files
                    if (selectedExitOption == ButtonType.YES) {

                        for (Map.Entry<File,File> entry : filteredReplaysThatHaveDuplicate.entrySet()) {

                            File inputFile = entry.getKey();
                            File duplicateDestinationFile = entry.getValue();

                            try {  // overwrite
                                FileUtils.copyFile(inputFile, duplicateDestinationFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                                " replay files.  " + numReplayFilesFiltered.get() + " replay files have " +
                                "been copied/overwritten in the output folder.");
                    }
                    // don't overwrite, update label
                    else if (selectedExitOption == ButtonType.CANCEL) {

                        controller.filteringProgressLabel.setText("Finished analyzing " + replays.size() +
                                " replay files.  " + (hasCopiedFiles.get() ? "Some file(s) have been copied to " +
                                "the output folder.  " : "") + "\nDuplicate files have not been overwritten.");
                    }
                }

                controller.filterReplaysButton.setDisable(false);
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
