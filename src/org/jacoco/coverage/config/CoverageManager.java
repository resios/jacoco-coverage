package org.jacoco.coverage.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CoverageManager implements SearchableConfigurable, Configurable {
    private CoverageManagerPane panel;
    private final Project project;

    public CoverageManager(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return CoverageManager.class.getName();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return CoverageUtils.PLUGIN_TITLE;
    }

    @Nullable
//    @Override
    public Icon getIcon() {
        return null;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (panel == null) {
            panel = new CoverageManagerPane(project);
        }
        return panel.getPane();
    }

    @Override
    public boolean isModified() {
        return panel == null || panel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (panel != null) {
            panel.apply();
        }
    }

    @Override
    public void reset() {
        if (panel != null) {
            panel.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        panel = null;
    }
}
