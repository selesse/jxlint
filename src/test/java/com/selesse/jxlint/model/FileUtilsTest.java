package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
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
            tempFile1.createNewFile();
            tempFile1.deleteOnExit();
            File tempFile2 = new File(rootTempDir.getAbsolutePath() + File.separator + "x2");
            tempFile2.createNewFile();
            tempFile2.deleteOnExit();

            File tempDir2 = new File(rootTempDir.getAbsolutePath() + File.separator + "dir");
            tempDir2.mkdir();
            tempDir2.deleteOnExit();

            File tempFile3 = new File(tempDir2.getAbsolutePath() + File.separator + "3.xml");
            tempFile3.createNewFile();
            tempFile3.deleteOnExit();

            File tempDir3 = new File(rootTempDir.getAbsolutePath() + File.separator + "w");
            tempDir3.mkdir();
            tempDir3.deleteOnExit();

            File tempDir4 = new File(tempDir3.getAbsolutePath() + File.separator + "y");
            tempDir4.mkdir();
            tempDir4.deleteOnExit();

            File tempDir5 = new File(tempDir4.getAbsolutePath() + File.separator + "z");
            tempDir5.mkdir();
            tempDir5.deleteOnExit();

            File tempFile4 = new File(tempDir3.getAbsolutePath() + File.separator + "test.xml");
            tempFile4.createNewFile();
            tempFile4.deleteOnExit();

            File tempFile5 = new File(tempDir4.getAbsolutePath() + File.separator + "test.xml");
            tempFile5.createNewFile();
            tempFile5.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
