package org.jacoco.coverage.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jacoco.coverage.config.CoverageConfig;
import org.jacoco.coverage.data.CoverageSourceData;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CoverageUtils {

    private static final Logger LOG = Logger.getInstance("#org.jacoco.coverage.util.CoverageUtils");

    public static final String PLUGIN_TITLE = "JaCoCo Coverage";

    public static String getDefaultCoverageFile(Project project) {
        File coverageRootPath = getCoverageRootPath(project);
        return coverageRootPath + File.separator + project.getName() + ".coverage";
    }

    public static File getCoverageReportRoot(Project project) {
        return new File(getCoverageRootPath(project), "report");
    }

    public static File getCoverageRootPath(Project project) {
        String path = "coverage";
        CoverageConfig instance = CoverageConfig.getInstance(project);
        if (instance != null) {
            CoverageConfig.State state = instance.getState();
            if (state != null && state.coveragePath != null) {
                path = state.coveragePath;
            }
        }

        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return new File(FileUtil.getTempDirectory(), path);
        } else {
            return new File(VfsUtil.virtualToIoFile(baseDir), path);
        }
    }

    public static CoverageSourceData getSourceData(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        CoverageSourceData sourceData = new CoverageSourceData();
        for (Module module : moduleManager.getModules()) {
            CompilerModuleExtension moduleExtension = CompilerModuleExtension.getInstance(module);
            final boolean debugEnabled = LOG.isDebugEnabled();
            if(debugEnabled){
                LOG.debug("Computing classes directories for module " + module);
            }
            Set<File> classesDirectory = Sets.newHashSet();
            if (moduleExtension != null) {
                VirtualFile path = moduleExtension.getCompilerOutputPath();
                if(debugEnabled){
                    LOG.debug("Found compiler path " + path);
                }
                if (path != null) {
                    classesDirectory.add(VfsUtil.virtualToIoFile(path));
                }
            }

            if(debugEnabled){
                LOG.debug("Class directories are " + classesDirectory);
            }
            sourceData.setClassesDirectory(classesDirectory);

            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            List<File> sourceDirectories = Lists.newLinkedList();
            if(debugEnabled){
                LOG.debug("Computing sources directories for module " + module);
            }
            for (VirtualFile root : rootManager.getSourceRoots(false)) {
                if(debugEnabled){
                    LOG.debug("Testing source path " + root);
                }
                File file = VfsUtil.virtualToIoFile(root);
                if (file.exists() && file.canRead()) {
                    sourceDirectories.add(file);
                }
            }

            if(debugEnabled){
                LOG.debug("Source directories are " + sourceDirectories);
            }
            sourceData.setSourceDirectories(sourceDirectories);
        }
        return sourceData;
    }
}
