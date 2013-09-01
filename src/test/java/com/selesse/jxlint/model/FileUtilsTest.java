package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {
    File rootTempDir;

    @Before
    public void setup() {
        rootTempDir = Files.createTempDir();
        rootTempDir.deleteOnExit();
        try {
            File tempFile1 = new File(rootTempDir.getAbsolutePath() + File.separator + "x");
            tempFile1.createNewFile();
            File tempFile2 = new File(rootTempDir.getAbsolutePath() + File.separator + "x2");
            tempFile2.createNewFile();

            File tempDir2 = new File(rootTempDir.getAbsolutePath() + File.separator + "dir");
            tempDir2.mkdir();

            File tempFile3 = new File(tempDir2.getAbsolutePath() + File.separator + "3.xml");
            tempFile3.createNewFile();

            File tempDir3 = new File(rootTempDir.getAbsolutePath() + File.separator + "w" + File.separator + "y" + File.separator + "z");
            tempDir3.mkdirs();

            File tempFile4 = new File(tempDir3.getAbsolutePath() + File.separator + "test.xml");
            tempFile4.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListAllFiles() {
        assertEquals(4, FileUtils.allFilesIn(rootTempDir).size());
    }

    @Test
    public void testListAllXmlFiles() {
        assertEquals(2, FileUtils.allXmlFilesIn(rootTempDir).size());

        List<String> fileNames = Lists.newArrayList();

        for (File file : FileUtils.allXmlFilesIn(rootTempDir)) {
            fileNames.add(file.getName());
        }

        Collections.sort(fileNames);

        assertEquals("3.xml", fileNames.get(0));
        assertEquals("test.xml", fileNames.get(1));
    }

}
