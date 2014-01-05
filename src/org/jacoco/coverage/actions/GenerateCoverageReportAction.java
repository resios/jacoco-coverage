package org.jacoco.coverage.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jacoco.coverage.util.ReportGenerator;
import org.jacoco.coverage.data.CoverageSourceData;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static org.jacoco.coverage.util.CoverageUtils.getSourceData;

public class GenerateCoverageReportAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        ProgressManager instance = ProgressManager.getInstance();
        instance.run(new Task.Backgroundable(project, "Generating coverage report", true) {
            private String reportPath;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                reportPath = generateReport(project);
            }

            @Override
            public void onSuccess() {
                String message = String.format("<html>Click <a href=\"%s\">here</a> to view the report</html>", reportPath);
                Messages.showInfoMessage(message, CoverageUtils.PLUGIN_TITLE);
                super.onSuccess();
            }
        });
    }

    private String generateReport(Project project) {
        String coverageData = CoverageUtils.getDefaultCoverageFile(project);
        ReportGenerator reportGenerator = new ReportGenerator();

        reportGenerator.setExecutionDataFile(new File(coverageData));
        reportGenerator.setTitle("Coverage of " + project.getName());

        CoverageSourceData sourceData = getSourceData(project);

        reportGenerator.setSourceData(sourceData);
        File report = CoverageUtils.getCoverageReportRoot(project);
        reportGenerator.setReportDirectory(report);

        try {
            reportGenerator.create();
        } catch (IOException e1) {
            // TODO report failure and log error
            e1.printStackTrace();
        }

        return new File(report, "index.html").getAbsolutePath();
    }


}
