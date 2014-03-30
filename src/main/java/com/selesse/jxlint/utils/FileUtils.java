package com.selesse.jxlint.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.io.Files;

import java.io.File;
import java.net.URI;
import java.util.List;

public class FileUtils {
    private static Predicate<File> IS_FILE = new Predicate<File>() {
        @Override public boolean apply(File file) {
            return file != null && file.isFile();
        }
    };

    private static Function<File, String> GET_NAME = new Function<File, String>() {
        @Override public String apply(File file) {
            return file == null ? null : file.getName();
        }
    };

    private static List<File> allFilesSatisfyingPredicate(File rootDir, Predicate<String> predicate) {
        return Files.fileTreeTraverser()
            .preOrderTraversal(rootDir)
            .filter(IS_FILE)
            .filter(Predicates.compose(predicate, GET_NAME))
            .toList();
    }

    /**
     * Recursively get all files in a directory.
     */
    public static List<File> allFiles(File rootDir) {
        return allFilesSatisfyingPredicate(rootDir, Predicates.<String>alwaysTrue());
    }

    /**
     * Return all files in a directory, recursively, that have the given extension.
     * The extension does not include the period, i.e. "txt" would match "file.txt".
     */
    public static List<File> allFilesWithExtension(File rootDir, final String extension) {
        return allFilesSatisfyingPredicate(rootDir, new Predicate<String>() {
            @Override public boolean apply(String name) {
                return name != null && Files.getFileExtension(name).equalsIgnoreCase(extension);
            }
        });
    }

    /**
     * Return all files in a directory, recursively, that have the given filename.
     */
    public static List<File> allFilesWithFilename(File rootDir, String filename) {
        return allFilesSatisfyingPredicate(rootDir, Predicates.equalTo(filename));
    }

    /**
     * Return all files in a directory, recursively, that match a given regex.
     */
    public static List<File> allFilesMatching(File rootDir, final String regex) {
        return allFilesSatisfyingPredicate(rootDir, new Predicate<String>() {
            @Override public boolean apply(String name) {
                return name != null && name.matches(regex);
            }
        });
    }

    /**
     * Return all files in a directory, recursively, that contain a certain substring in their filename.
     */
    public static List<File> allFilesContaining(File rootDir, final String substring) {
        return allFilesSatisfyingPredicate(rootDir, new Predicate<String>() {
            @Override public boolean apply(String name) {
                return name != null && name.contains(substring);
            }
        });
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
