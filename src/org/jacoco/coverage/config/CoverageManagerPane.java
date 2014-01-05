package org.jacoco.coverage.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.PathUIUtils;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;

import javax.swing.*;
import java.io.File;
import java.io.FileDescriptor;

public class CoverageManagerPane implements Disposable {
    private JPanel root;
    private TextFieldWithBrowseButton.NoPathCompletion coveragePathTextField;
    private final Project project;

    public CoverageManagerPane(final Project project) {
        this.project = project;
        String basePath = project.getBasePath();
        final File projectRoot = basePath == null ? null : new File(basePath);
        coveragePathTextField.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor(), new TextComponentAccessor<JTextField>() {
            @Override
            public String getText(JTextField component) {
                String relativePath = component.getText();
                return projectRoot == null ? relativePath : projectRoot + File.separator + relativePath;
            }

            @Override
            public void setText(JTextField component, String text) {
                String normalizedText = text;
                if(normalizedText != null && projectRoot != null){
                    String relativePath = FileUtil.getRelativePath(projectRoot, new File(normalizedText));
                    if(relativePath != null){
                        normalizedText = relativePath;
                    }
                }
                component.setText(normalizedText);
            }
        });
    }

    @Override
    public void dispose() {
    }

    public JComponent getPane() {
        return root;
    }

    public boolean isModified() {
        CoverageConfig state = getState();
        return state == null || !coveragePathTextField.getText().equals(state.getCoveragePath());
    }

    public void apply() {
        CoverageConfig state = getState();
        if (state != null && state.getState() != null){
            state.getState().coveragePath = coveragePathTextField.getText();
        }
    }

    public void reset() {
        setFormData(getState());
    }

    private void setFormData(CoverageConfig config) {
        CoverageConfig.State state = config.getState();
        if (state != null) {
            coveragePathTextField.setText(state.coveragePath);
        }
    }

    private CoverageConfig getState() {
        return CoverageConfig.getInstance(project);
    }
}
