package org.jacoco.coverage.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import org.jacoco.coverage.config.CoverageConfig;

public class ToggleCoverageAction extends ToggleAction {

    @Override
    public boolean isSelected(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false;
        }

        CoverageConfig instance = CoverageConfig.getInstance(project);
        return instance != null && instance.isEnabled();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        Project project = e.getProject();
        if (project != null) {
            CoverageConfig instance = CoverageConfig.getInstance(project);

            if (instance != null) {
                CoverageConfig.State instanceState = instance.getState();
                if (instanceState != null) {
                    instanceState.isEnabled = state;
                }
            }
        }
    }
}
