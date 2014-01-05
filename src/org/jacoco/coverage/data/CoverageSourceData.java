package org.jacoco.coverage.data;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CoverageSourceData {

    private Set<File> classesDirectory;
    private List<File> sourceDirectories;

    public Set<File> getClassesDirectory() {
        return classesDirectory;
    }

    public void setClassesDirectory(Set<File> classesDirectory) {
        this.classesDirectory = classesDirectory;
    }

    public List<File> getSourceDirectories() {
        return sourceDirectories;
    }

    public void setSourceDirectories(List<File> sourceDirectories) {
        this.sourceDirectories = sourceDirectories;
    }
}
