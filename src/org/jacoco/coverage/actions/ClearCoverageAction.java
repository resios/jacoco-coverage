package org.jacoco.coverage.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import org.jacoco.coverage.config.CoverageConfig;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ClearCoverageAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        ProgressManager instance = ProgressManager.getInstance();
        instance.run(new Task.Backgroundable(project, "Removing coverage report") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                File reportRoot = CoverageUtils.getCoverageReportRoot(project);
                boolean isRemoved = FileUtil.delete(reportRoot);
                isRemoved = FileUtil.delete(new File(CoverageUtils.getDefaultCoverageFile(project))) && isRemoved;

                if(!isRemoved){
                    Messages.showErrorDialog("<html>Could not delete report</html>", CoverageUtils.PLUGIN_TITLE);
                }
            }
        });
    }
}
