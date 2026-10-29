package org.wood.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    /**
     * Tests if a file has a specified file extension
     * @param file The file to test
     * @param extension The case-insensitive extension to check
     * @return True if the file has the given extension
     */
    public static boolean hasExtension(File file, String extension) {

        String fileName = file.getName();
        boolean fileHasDot = fileName.indexOf('.') != -1;
        // if the file has a dot, check the extensions
        if(fileHasDot) {
            String fileNameExtension = fileName.substring(fileName.lastIndexOf('.'));
            // make sure the extension parameter includes a dot before checking equality
            extension = extension.charAt(0) != '.' ? "." + extension : extension;
            return fileNameExtension.equalsIgnoreCase(extension);
        }
        return false;
    }

    /**
     * Runs RocketLeagueReplayParser.Console.exe on a replay file to get the JSON output
     * @param replay The replay file to convert
     * @param replayParserEXE The location of RocketLeagueReplayParser.Console.exe
     * @return The raw JSON output of executing "cmd /c
     *      yourpath/RocketLeagueReplayParser.Console.exe replay_file.replay"
     */
    public static String replayToJSON(File replay, File replayParserEXE) {
        String output = "";

        try {
            // turn the .replay file into a JSON String
            List<String> command = new ArrayList<String>();
            command.add("cmd");
            command.add("/c");
            command.add(replayParserEXE.getAbsoluteFile().getAbsolutePath());
            command.add(replay.getAbsoluteFile().getAbsolutePath());

            Process p = new ProcessBuilder(command).start();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader input = new BufferedReader(isr);
            output = input.readAllAsString();

            p.close();
            isr.close();
            input.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    /**
     * Replaces the file extension of a path
     * @param path The file path to make a new extension out of
     * @param newExtension The new extension to replace the path's extension with
     *                     (can optionally start with a .)
     * @return The file with a new extension, or null if the path has no extension
     */
    public static File replaceExtension(String path, String newExtension) {

        File fileAtInputPath = new File(path);
        // make sure the extension starts with a dot
        newExtension = newExtension.charAt(0) == '.' ? newExtension : "." + newExtension;
        String fileName = fileAtInputPath.getName();
        boolean pathHasDot = fileName.indexOf('.') != -1;

        if(pathHasDot) {
            // update file's name to have the new extension
            String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) +
                    newExtension;

            // get the parent file
            // create a new file object with absolute path to make sure parent directory exists
            File fileWithParent = new File(fileAtInputPath.getAbsoluteFile().getAbsolutePath());
            File parentFile = fileWithParent.getParentFile();

            // return a file with the updated extension
            return  new File(parentFile.getAbsoluteFile().getAbsolutePath()
                    + "/" + newFileName);
        }
        else {
            return null;
        }
    }

    /**
     * Checks whether replay files from the input folder already exist in the output folder
     * @param inputFolder The folder to take replay files from to check their existence in the output folder
     * @param outputFolder The folder to check for duplicates in
     * @return True if any duplicate files from the input folder are found in the output folder
     */
    public static boolean checkDuplicateReplayFiles(File inputFolder, File outputFolder) {

        // gather all .replay files
        ArrayList<File> replays = new ArrayList<>();
        for(File potentialReplay : inputFolder.listFiles()) {
            // ignore any file that isn't a .replay
            if(hasExtension(potentialReplay, ".replay")) {
                replays.add(potentialReplay);
            }
        }

        // check whether any replays would be a duplicate in the output folder
        for(File replay : replays) {

            File destination = new File(outputFolder
                    .getAbsoluteFile().getAbsolutePath() + "/" + replay.getName());

            if (destination.exists()) {
                return true;
            }
        }

        return false;
    }

}
