package com.selesse.jxlint.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.selesse.jxlint.utils.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {
    private static File rootTempDir;

    private static void createFile(String... parts) throws IOException {
        File file = new File(rootTempDir, Joiner.on(File.separator).join(parts));
        Files.createParentDirs(file);
        assertThat(file.createNewFile()).isTrue();
    }

    @BeforeClass
    public static void setup() throws IOException {
        rootTempDir = Files.createTempDir();
        rootTempDir.deleteOnExit();
        createFile("x");
        createFile("x2");
        createFile("dir", "3.xml");
        createFile("w", "test.xml");
        createFile("w", "y", "test.xml");
        createFile("txt-this.txt.no");
        createFile("Test.java");
        createFile("Test.txt");
    }

    @AfterClass
    public static void deleteWorkingFiles() throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(rootTempDir);
    }

    private static void assertItemsEqualInAnyOrder(List<File> actual, String... expected) {
        assertThat(actual.size()).isEqualTo(expected.length);
        List<String> filenames = Lists.newArrayList();
        for (File f : actual) {
            filenames.add(f.getName());
        }
        Collections.sort(filenames);
        Arrays.sort(expected);
        assertThat(Arrays.asList(expected)).isEqualTo(filenames);
    }

    @Test
    public void testListAllFiles() {
        assertThat(FileUtils.allFiles(rootTempDir)).hasSize(8);
    }

    @Test
    public void testListAllXmlFiles() {
        List<File> xmlFiles = FileUtils.allFilesWithExtension(rootTempDir, "xml");
        assertItemsEqualInAnyOrder(xmlFiles, "3.xml", "test.xml", "test.xml");
    }

    @Test
    public void testListAllTxtFiles() throws IOException {
        List<File> textFiles = FileUtils.allFilesWithExtension(rootTempDir, "txt");
        assertItemsEqualInAnyOrder(textFiles, "Test.txt");
    }

    @Test
    public void testGetAllFilenames() {
        List<File> files = FileUtils.allFilesWithFilename(rootTempDir, "test.xml");
        assertItemsEqualInAnyOrder(files, "test.xml", "test.xml");
    }

    @Test
    public void testGetAllMatchingFiles() {
        List<File> files = FileUtils.allFilesMatching(rootTempDir, ".*[0-9]+.*");
        assertItemsEqualInAnyOrder(files, "3.xml", "x2");
    }

    @Test
    public void testGetFilesContaining() {
        List<File> files = FileUtils.allFilesContaining(rootTempDir, "test");
        assertItemsEqualInAnyOrder(files, "test.xml", "test.xml");
    }

    @Test
    public void testGetRelativePath() {
        assertThat(FileUtils.getRelativePath(rootTempDir, rootTempDir)).isEqualTo("");
        assertThat(FileUtils.getRelativePath(rootTempDir, new File(rootTempDir, "file.txt"))).isEqualTo("file.txt");
        assertThat(FileUtils.getRelativePath(rootTempDir, new File(new File(rootTempDir, "dir"), "file.txt")))
            .isEqualTo("dir/file.txt");
        assertThat(FileUtils.getRelativePath(new File(rootTempDir, "out"), new File(rootTempDir, "file.txt")))
            .isEqualTo("../file.txt");
        assertThat(FileUtils.getRelativePath(new File(rootTempDir, "out"),
                new File(new File(rootTempDir, "dir"), "file.txt")))
            .isEqualTo("../dir/file.txt");
    }
}
