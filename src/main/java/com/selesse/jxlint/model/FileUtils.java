package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

public class FileUtils {
    public static List<File> allXmlFilesIn(File rootDir) {
        if (rootDir == null) {
            throw new NullPointerException("Impossible to get all children files of null directory.");
        }
        return allFilesWithExtensionIn(rootDir, "XML");
    }

    public static List<File> allFilesIn(File rootDir) {
        List<File> files = Lists.newArrayList();

        for (File file : rootDir.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(FileUtils.allFilesIn(file));
            }
            else {
                files.add(file);
            }
        }

        return files;
    }

    public static List<File> allFilesWithExtensionIn(File rootDir, String extension) {
        List<File> filteredFiles = Lists.newArrayList();

        List<File> allFiles = allFilesIn(rootDir);
        for (File file : allFiles) {
            if (Files.getFileExtension(file.getAbsolutePath()).equalsIgnoreCase(extension)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }
}
