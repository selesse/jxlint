package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileUtilsTest {
    private static File rootTempDir;

    @BeforeClass
    public static void setup() {
        rootTempDir = Files.createTempDir();
        rootTempDir.deleteOnExit();
        try {
            File tempFile1 = new File(rootTempDir.getAbsolutePath() + File.separator + "x");
            boolean tempFile1NewFile = tempFile1.createNewFile();
            File tempFile2 = new File(rootTempDir.getAbsolutePath() + File.separator + "x2");
            boolean tempFile2NewFile = tempFile2.createNewFile();

            File tempDir2 = new File(rootTempDir.getAbsolutePath() + File.separator + "dir");
            boolean tempDir2mkdir = tempDir2.mkdir();

            File tempFile3 = new File(tempDir2.getAbsolutePath() + File.separator + "3.xml");
            boolean tempFile3NewFile = tempFile3.createNewFile();

            File tempDir3 = new File(rootTempDir.getAbsolutePath() + File.separator + "w");
            boolean tempDir3mkdir = tempDir3.mkdir();

            File tempDir4 = new File(tempDir3.getAbsolutePath() + File.separator + "y");
            boolean tempDir4mkdir = tempDir4.mkdir();

            File tempDir5 = new File(tempDir4.getAbsolutePath() + File.separator + "z");
            boolean tempDir5mkdir = tempDir5.mkdir();

            File tempFile4 = new File(tempDir3.getAbsolutePath() + File.separator + "test.xml");
            boolean tempFile4NewFile = tempFile4.createNewFile();

            File tempFile5 = new File(tempDir4.getAbsolutePath() + File.separator + "test.xml");
            boolean tempFile5NewFile = tempFile5.createNewFile();

            // Wow, gross! This is done to appease FindBugs.
            assertTrue("Files were all created property for test", tempFile1NewFile && tempFile2NewFile &&
                    tempFile3NewFile && tempFile4NewFile && tempFile5NewFile && tempDir2mkdir && tempDir3mkdir &&
                    tempDir4mkdir && tempDir5mkdir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void deleteWorkingFiles() throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(rootTempDir);
    }

    @Test
    public void testListAllFiles() {
        assertEquals(5, FileUtils.allFilesIn(rootTempDir).size());
    }

    @Test
    public void testListAllXmlFiles() {
        assertEquals(3, FileUtils.allXmlFilesIn(rootTempDir).size());

        List<String> fileNames = Lists.newArrayList();

        for (File file : FileUtils.allXmlFilesIn(rootTempDir)) {
            fileNames.add(file.getName());
        }

        Collections.sort(fileNames);

        assertEquals("3.xml", fileNames.get(0));
        assertEquals("test.xml", fileNames.get(1));
        assertEquals("test.xml", fileNames.get(2));
    }

    @Test
    public void testGetAllFilenames() {
        List<File> files = FileUtils.allFilesWithFilenameIn(rootTempDir, "test.xml");

        assertTrue(files.size() == 2);
        assertEquals(files.get(0).getName(), "test.xml");
    }

}
