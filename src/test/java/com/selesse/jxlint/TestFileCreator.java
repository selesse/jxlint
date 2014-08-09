package com.selesse.jxlint;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestFileCreator {
    private static File createFile(String fileName, List<String> fileContents) {
        File file = new File(fileName);
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            for (String line : fileContents) {
                fileWriter.println(line);
            }
            fileWriter.flush();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createValidXml(File baseDirectory) {
        List<String> fileContents = Lists.newArrayList(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<empty/>"
        );

        return createFile(baseDirectory + File.separator + "valid.xml", fileContents);
    }

    public static File createBadEncodingFile(File baseDirectory) {
        List<String> fileContents = Lists.newArrayList(
                "<?xml version=\"1.0\"?>",
                "<empty/>"
        );
        return createFile(baseDirectory + File.separator + "bad-encoding.xml", fileContents);
    }

    public static File createBadAttributeFile(File baseDirectory) {
        List<String> fileContents = Lists.newArrayList(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<attribute name=\"dupe-name\" name=\"dupe-name\"/>"
        );
        return createFile(baseDirectory + File.separator + "bad-attribute.xml", fileContents);
    }

    public static File createBadVersionFile(File baseDirectory) {
        List<String> fileContents = Lists.newArrayList(
                "<?xml encoding=\"UTF-8\"?>",
                "<empty/>"
        );
        return createFile(baseDirectory + File.separator + "bad-version.xml", fileContents);
    }

    public static File createBadAuthorFile(File baseDirectory) {
        List<String> fileContents = Lists.newArrayList(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<author name=\"\"/>"
        );
        return createFile(baseDirectory + File.separator + "author.xml", fileContents);
    }
}
