package org.jacoco.coverage.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jacoco.coverage.data.CoverageSourceData;
import org.jacoco.coverage.util.CoverageUtils;
import org.jacoco.coverage.util.ReportGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.jacoco.coverage.util.CoverageUtils.getSourceData;

public class GenerateCoverageReportAction extends AnAction {

    private static final Logger LOG = Logger.getInstance("#org.jacoco.coverage.actions.GenerateCoverageReportAction");

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        ProgressManager instance = ProgressManager.getInstance();
        instance.run(new Task.Backgroundable(project, "Generating coverage report", true) {
            private Map<String, String> reportPath = new HashMap<String, String>();
            private boolean isCanceled = false;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                File executionDataFile = getExecutionDataFile(project);
                if (executionDataFile == null) {
                    return;
                }
                ModuleManager moduleManager = ModuleManager.getInstance(project);
                Module[] modules = moduleManager.getModules();
                final int moduleSize = modules.length;
                for (int i = 0; i < moduleSize; i++) {
                    Module module = modules[i];
                    if (indicator.isCanceled()) {
                        isCanceled = true;
                        break;
                    }
                    indicator.setFraction((double) i / moduleSize);
                    indicator.setText("Generating report for " + module.getName());
                    reportPath.put(module.getName(), generateModuleReport(module, executionDataFile, project));
                }
            }

            @Override
            public void onSuccess() {
                if (!isCanceled && reportPath != null && !reportPath.isEmpty()) {
                    Messages.showInfoMessage(getResultMessage(reportPath), CoverageUtils.PLUGIN_TITLE);
                    super.onSuccess();
                }
            }

            private String getResultMessage(Map<String, String> reportPath) {
                StringBuilder builder = new StringBuilder();
                builder.append("<html>Generated the following reports:<ul style=\"list-style-type: none;\">");
                for (Map.Entry<String, String> entry : reportPath.entrySet()) {
                    if (entry.getValue() != null) {
                        builder
                                .append("<li><a href=\"")
                                .append(entry.getValue()).append("\">")
                                .append(entry.getKey()).append("</a>");
                    }
                }
                builder.append("</ul></html>");

                return builder.toString();
            }
        });
    }

    private String generateModuleReport(Module module, File executionDataFile, Project project) {
        ReportGenerator reportGenerator = new ReportGenerator();

        reportGenerator.setExecutionDataFile(executionDataFile);
        reportGenerator.setTitle("Coverage of " + module.getName());

        CoverageSourceData sourceData = getSourceData(module);

        reportGenerator.setSourceData(sourceData);
        File report = CoverageUtils.getCoverageReportRoot(module, project);
        reportGenerator.setReportDirectory(report);

        try {
            reportGenerator.create();
            return new File(report, "index.html").getAbsolutePath();
        } catch (IOException error) {
            LOG.error("Error while generating report", error);
            Notifications.Bus.notify(new Notification(CoverageUtils.PLUGIN_TITLE, "Error while generating report",
                    "Error while generating report " + error.getMessage(),
                    NotificationType.ERROR), project);
        }

        return null;
    }

    private File getExecutionDataFile(Project project) {
        String coverageData = CoverageUtils.getDefaultCoverageFile(project);
        File executionDataFile = new File(coverageData);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Execution data file is: " + executionDataFile.getAbsolutePath());
        }

        if (!executionDataFile.exists()) {
            Notifications.Bus.notify(new Notification(CoverageUtils.PLUGIN_TITLE, "No coverage data",
                    "There is no coverage data to ",
                    NotificationType.ERROR), project);
            return null;
        }
        return executionDataFile;
    }


}
