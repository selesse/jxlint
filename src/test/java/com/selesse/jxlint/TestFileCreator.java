package com.selesse.jxlint;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;

public class TestFileCreator {

    public static File createValidXml(File baseDirectory) {
        File file = new File(baseDirectory + File.separator + "valid.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createBadEncodingFile(File baseDirectory) {
        File file = new File(baseDirectory + File.separator + "bad-encoding.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createBadAttributeFile(File baseDirectory) {
        File file = new File(baseDirectory + File.separator + "bad-attribute.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<attribute name=\"dupe-name\" name=\"dupe-name\"/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createBadVersionFile(File baseDirectory) {
        File file = new File(baseDirectory + File.separator + "bad-version.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml encoding=\"UTF-8\"?>");
            fileWriter.println("<empty/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File createBadAuthorFile(File baseDirectory) {
        File file = new File(baseDirectory + File.separator + "author.xml");
        try {
            boolean newFile = file.createNewFile();
            assertTrue("File creation failed, could not run test", newFile);
            file.deleteOnExit();

            PrintWriter fileWriter = new PrintWriter(file, Charsets.UTF_8.displayName());
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println("<author name=\"\"/>");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
