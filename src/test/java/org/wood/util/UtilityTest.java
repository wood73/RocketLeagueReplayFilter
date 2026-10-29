package org.wood.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    public void hasExtensionTest() {

        // test that a directory has no file extension
        File directory = new File("this is a directory");
        String testDirectoryExtension = ".exe";
        Assertions.assertFalse(Utility.hasExtension(directory, testDirectoryExtension));

        // test that a file has a certain extension
        File file = new File("test.txt");
        String testFileExtension = ".txt";
        Assertions.assertTrue(Utility.hasExtension(file, testFileExtension));

        // test that a file has an extension without the dot
        String testFileExtensionWithoutDot = "txt";
        Assertions.assertTrue(Utility.hasExtension(file, testFileExtensionWithoutDot));

        // test that a file doesn't have a certain extension
        String testWrongFileExtension = ".json";
        Assertions.assertFalse(Utility.hasExtension(file, testWrongFileExtension));

        // test that a file doesn't have a certain extension without the dot
        String testWrongFileExtensionWithoutDot = "json";
        Assertions.assertFalse(Utility.hasExtension(file, testWrongFileExtensionWithoutDot));

    }

    @Test
    public void replaceExtensionTest() {

        // test replacing an extension
        String path1 = "loon/ski_bum.exe";
        String path1NewExtension = ".txt";
        File output1 = Utility.replaceExtension(path1, path1NewExtension);
        String output1Name1 = output1.getName();
        String output1Extension = output1Name1.substring(output1Name1.lastIndexOf("."));
        Assertions.assertEquals(".txt", output1Extension);

        // test replacing an extension without the dot
        String path2 = "loon/ski_bum.exe";
        String path2NewExtension = "txt";
        File output2 = Utility.replaceExtension(path2, path2NewExtension);
        String output2Name = output2.getName();
        String output2Extension = output2Name.substring(output2Name.lastIndexOf("."));
        Assertions.assertEquals(".txt", output2Extension);

        // test that a path without an extension returns null
        String path3 = "loon/camp_three";
        String path3NewExtension = ".txt";
        File output3 = Utility.replaceExtension(path3, path3NewExtension);
        Assertions.assertNull(output3);

    }

}