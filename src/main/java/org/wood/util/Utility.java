package org.wood.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            File parentFile = getParentFile(fileAtInputPath);

            // return a file with the updated extension
            return  new File(parentFile.getAbsoluteFile().getAbsolutePath()
                    + "/" + newFileName);
        }
        else {
            return null;
        }
    }


    /**
     * Gets the in-game replay name from a .replay file converted to .json -
     * replays from 2017 and 2026 will have the same format of
     * *"ReplayName":"in game replay name"*
     *
     * @param jsonReplayContents The contents of a .replay file converted to .json
     * @return An Optional which will hold the in-game replay name if it exists (with a .replay file extension),
     *          or Optional.empty() if it doesn't (or there was some parsing error)
     *
     * @throws RuntimeException if replayname is at the end of the file, or no quote detected after
     *                          in-game name, or in-game name has a length of greater than 254 characters
     */
    public static Optional<String> getInGameReplayName(String jsonReplayContents) {

        jsonReplayContents = jsonReplayContents.toLowerCase();
        if(jsonReplayContents.contains("replayname")) {

            int replayNameIndex = jsonReplayContents.indexOf("replayname");

            // number of characters after the 'r' in "replayname" that the in-game name starts
            final int CHARS_AFTER_REPLAYNAME_INDEX = 13;

            // index of the first character of the in-game replay name
            int firstCharInGameNameIndex = replayNameIndex + CHARS_AFTER_REPLAYNAME_INDEX;

            // if at end of file for some reason, throw error
            if(jsonReplayContents.length() <= firstCharInGameNameIndex) {

                throw new RuntimeException("In json file \"ReplayName\" is at the end of the file " +
                        "for some reason, maybe report this as a bug, sorry.");
            }

            // if quote after the in-game name doesn't exist for some reason, throw error
            if(jsonReplayContents.substring(firstCharInGameNameIndex).indexOf('\"') == -1) {

                throw new RuntimeException("In json file, there is no quote after the in-game name " +
                        "for some reason, maybe report this as a bug, sorry.");
            }

            // index of the ending quote after the in-game replay name
            int quoteAfterInGameNameIndex = firstCharInGameNameIndex +
                    jsonReplayContents.substring(firstCharInGameNameIndex).indexOf('\"');


            String inGameReplayName = jsonReplayContents.substring(
                    firstCharInGameNameIndex, quoteAfterInGameNameIndex) + ".replay";

            // if in-game name is for some reason beyond max file system name length, throw error
            if(inGameReplayName.length() > 254) {

                throw new RuntimeException("In-game replay name length is detected as greater than 254 " +
                        "characters for some reason, maybe report this as a bug, sorry.  First " +
                        "60 characters of the name: " + inGameReplayName.substring(0, 60));
            }

            return Optional.of(inGameReplayName);

        }
        else { // there is no in-game replay name

            return Optional.empty();
        }
    }

    /**
     * While the given .replay File has a duplicate/exists already, keep adding a number to its name
     * (in parentheses) until it is unique.
     *
     * @param file The .replay file to get a unique name for
     * @return A file with a unique name in the same directory
     */
    public static File getUniqueReplayFile(File file) {

        // while the file exists, keep incrementing a number in parenthesis at the end of the filename
        // need to use file.getAbsoluteFile.exists() as per SO
        while(file.getAbsoluteFile().exists()) {
            String fileName = file.getName();
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

            // if the file already has a number, increment it
            if(endsWithAnyNumberInParentheses(fileNameWithoutExtension)) {
                int lastIndexOpeningParentheses = fileNameWithoutExtension.lastIndexOf('(');
                int lastIndexEndingParentheses = fileNameWithoutExtension.length() - 1;

                int numberInParenthesis = Integer.parseInt(fileNameWithoutExtension.substring(
                        lastIndexOpeningParentheses + 1, lastIndexEndingParentheses));

                int newNumberInParentheses = numberInParenthesis + 1;

                String newFileName = fileNameWithoutExtension.substring(0, lastIndexOpeningParentheses + 1) +
                        newNumberInParentheses + ").replay";

                File parentFile = getParentFile(file);

                // update the file with the incremented name
                file = new File(parentFile.getAbsoluteFile().getAbsolutePath() + "/" + newFileName);
            }
            else { // add a " (1)" to the end of the filename

                File parentFile = getParentFile(file);
                file = new File(parentFile.getAbsoluteFile().getAbsolutePath() + "/" +
                        fileNameWithoutExtension + " (1).replay");
            }
        }

        return file;
    }

    /**
     * Tests if a string ends with any number in parentheses
     *
     * @param str The String to check if it ends with any number in parentheses
     * @return True if the input string ends with a number in parentheses
     */
    public static boolean endsWithAnyNumberInParentheses(String str) {

        Pattern pattern = Pattern.compile("\\(\\d+\\)$");
        Matcher matcher = pattern.matcher(str);

        return matcher.find();
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

            if (destination.getAbsoluteFile().exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether any replay files exist in the specified directory
     * @param directory The directory to check for any replay files in
     * @return True if any replay files are found in the given directory
     * @throws IllegalArgumentException if the given folder is not a directory
     */
    public static boolean anyReplayFilesPresent(File directory) {

        if(!directory.isDirectory()) {
            throw new IllegalArgumentException("The given file was not a directory: " +
                    directory.getAbsoluteFile().getAbsolutePath());
        }

        // if any file is a .replay in the directory, return true
        for(File potentialReplay : directory.listFiles()) {

            if(hasExtension(potentialReplay, ".replay")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the parent file of a file
     *
     * @param file File to get the parent of
     * @return The parent file
     */
    public static File getParentFile(File file) {

        // create a new file object with absolute path to make sure parent directory exists
        File fileWithParent = new File(file.getAbsoluteFile().getAbsolutePath());
        return fileWithParent.getParentFile();
    }

}
