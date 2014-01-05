package org.jacoco.coverage.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

    public static final String PLUGIN_TITLE = "JaCoCo Coverage";

    public static String getDefaultCoverageFile(Project project) {
        File coverageRootPath = getCoverageRootPath(project);
        return coverageRootPath + File.separator + project.getName()  + ".coverage";
    }

    public static File getCoverageReportRoot(Project project){
        return new File(getCoverageRootPath(project), "report");
    }

    public static File getCoverageRootPath(Project project) {
        String path = "coverage";
        CoverageConfig instance = CoverageConfig.getInstance(project);
        if(instance != null){
            CoverageConfig.State state = instance.getState();
            if(state != null){
                path = state.coveragePath;
            }
        }

        return new File(VfsUtil.virtualToIoFile(project.getBaseDir()), path);
    }

    public static CoverageSourceData getSourceData(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        CoverageSourceData sourceData = new CoverageSourceData();
        for (Module module : moduleManager.getModules()) {
            CompilerModuleExtension moduleExtension = CompilerModuleExtension.getInstance(module);
            Set<File> classesDirectory = Sets.newHashSet();
            if (moduleExtension != null) {
                VirtualFile path = moduleExtension.getCompilerOutputPath();
                if (path != null) {
                    classesDirectory.add(VfsUtil.virtualToIoFile(path));
                }
            }

            sourceData.setClassesDirectory(classesDirectory);

            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            List<File> sourceDirectories = Lists.newLinkedList();
            for (VirtualFile root : rootManager.getSourceRoots(false)) {
                File file = VfsUtil.virtualToIoFile(root);
                if (file.exists() && file.canRead()) {
                    sourceDirectories.add(file);
                }
            }
            sourceData.setSourceDirectories(sourceDirectories);
        }
        return sourceData;
    }
}
