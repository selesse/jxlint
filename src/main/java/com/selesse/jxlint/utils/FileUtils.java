package com.selesse.jxlint.utils;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.net.URI;
import java.util.List;

public class FileUtils {
    /**
     * Recursively get all files in a directory.
     */
    public static List<File> allFiles(File rootDir) {
        List<File> files = Lists.newArrayList();

        for (File file : rootDir.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(FileUtils.allFiles(file));
            }
            else {
                files.add(file);
            }
        }

        return files;
    }

    /**
     * Get all Files within a particular directory ending in a particular extension.
     * The extension does not include the period, i.e. "txt" would match "file.txt".
     */
    public static List<File> allFilesWithExtension(File rootDir, String extension) {
        List<File> filteredFiles = Lists.newArrayList();

        List<File> allFiles = allFiles(rootDir);
        for (File file : allFiles) {
            if (Files.getFileExtension(file.getAbsolutePath()).equalsIgnoreCase(extension)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    public static List<File> allFilesWithFilename(File rootDir, String filename) {
        List<File> filteredFiles = Lists.newArrayList();

        List<File> allFiles = allFiles(rootDir);
        for (File file : allFiles) {
            if (file.getName().equals(filename)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    /**
     * Return all files in a directory, recursively, that match a given regex.
     */
    public static List<File> allFilesMatching(File rootDir, String regex) {
        List<File> filteredFiles = Lists.newArrayList();

        List<File> allFiles = allFiles(rootDir);
        for (File file : allFiles) {
            if (file.getName().matches(regex)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    /**
     * Return all files in a directory, recursively, that contain a certain substring in their filename.
     */
    public static List<File> allFilesContaining(File rootDir, String substring) {
        List<File> filteredFiles = Lists.newArrayList();

        List<File> allFiles = allFiles(rootDir);
        for (File file : allFiles) {
            if (file.getName().contains(substring)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    /**
     * Normalize the input file. This helps when printing paths.
     * i.e. "java -jar jxlint.jar ." would print "/home/alex/git/jxlint/./text.txt"
     */
    public static File normalizeFile(File file) {
        URI uri = file.toURI();
        uri = uri.normalize();
        String normalizedPath = uri.getPath();
        return new File(normalizedPath);
    }

    public static String getRelativePath(File parent, File child) {
        return parent.toURI().relativize(child.toURI()).getPath();
    }

}
