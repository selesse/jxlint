package com.selesse.jxlint.utils;

import com.google.common.io.Files;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileUtils {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static List<File> allFilesWithNameSatisfyingPredicate(File rootDir, Predicate<String> predicate) {
        return Files.fileTreeTraverser()
            .preOrderTraversal(rootDir)
            .filter(file -> file != null && file.isFile())
            .filter(file -> file != null && predicate.test(file.getName()))
            .toList();
    }

    private FileUtils() {}

    /**
     * Recursively get all files in a directory.
     */
    public static List<File> allFiles(File rootDir) {
        return allFilesWithNameSatisfyingPredicate(rootDir, fileName -> true);
    }

    /**
     * Return all files in a directory, recursively, that have the given extension.
     * The extension does not include the period, i.e. "txt" would match "file.txt".
     */
    public static List<File> allFilesWithExtension(File rootDir, final String extension) {
        return allFilesWithNameSatisfyingPredicate(rootDir, name ->
                Files.getFileExtension(name).equalsIgnoreCase(extension)
        );
    }

    /**
     * Return all files in a directory, recursively, that have the given filename.
     */
    public static List<File> allFilesWithFilename(File rootDir, String filename) {
        return allFilesWithNameSatisfyingPredicate(rootDir, name -> Objects.equals(name, filename));
    }

    /**
     * Return all files in a directory, recursively, that match a given regex.
     */
    public static List<File> allFilesMatching(File rootDir, final String regex) {
        return allFilesWithNameSatisfyingPredicate(rootDir, name -> name.matches(regex));
    }

    /**
     * Return all files in a directory, recursively, that contain a certain substring in their filename.
     */
    public static List<File> allFilesContaining(File rootDir, final String substring) {
        return allFilesWithNameSatisfyingPredicate(rootDir, name -> name.contains(substring));
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
        String result = parent.toPath().relativize(child.toPath()).toString();
        if (!"/".equals(FILE_SEPARATOR)) {
            result = result.replaceAll(Pattern.quote(FILE_SEPARATOR), "/");
        }
        return result;
    }

}
