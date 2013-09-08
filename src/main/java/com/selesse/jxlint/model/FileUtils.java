package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

public class FileUtils {
    public static List<File> allXmlFilesIn(File rootDir) {
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

    private static List<File> allFilesWithExtensionIn(File rootDir, String extension) {
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
