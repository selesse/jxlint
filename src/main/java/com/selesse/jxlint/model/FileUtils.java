package com.selesse.jxlint.model;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

public class FileUtils {
    public static List<File> allXmlFilesIn(File rootDir) {
        List<File> xmlFiles = Lists.newArrayList();

        List<File> allFiles = allFilesIn(rootDir);
        for (File file : allFiles) {
            if (Files.getFileExtension(file.getAbsolutePath()).equalsIgnoreCase("XML")) {
                xmlFiles.add(file);
            }
        }

        return xmlFiles;
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
}
