package org.jacoco.coverage.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testIntegration.TestFinderHelper;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.util.List;

public class AnnotateFileWithCoverageAction extends AnAction {

    private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            return;
        }

        showInBrowser(e, project);
    }

    private void showInBrowser(AnActionEvent e, Project project) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            if (TestFinderHelper.isTest(javaFile)) {
                List<PsiElement> classesForTest = TestFinderHelper.findClassesForTest(javaFile);
                for (PsiElement element : classesForTest) {
                    if (element != null && element.getContainingFile() instanceof PsiJavaFile) {
                        javaFile = (PsiJavaFile) element.getContainingFile();
                        break;
                    }
                }
            }
            showCoverageForFile(javaFile, e, project);
        }
    }

    private void showCoverageForFile(PsiJavaFile javaFile, AnActionEvent e, Project project) {
        VirtualFile virtualFile = javaFile.getVirtualFile();
        if (virtualFile == null) {
            Notifications.Bus.notify(getMissingFileNotification(virtualFile), project);
            return;
        }
        Module module = ModuleUtil.findModuleForFile(virtualFile, project);

        if(module == null){
            return;
        }

        File coverageRootPath = CoverageUtils.getCoverageReportRoot(module, project);
        if (coverageRootPath.exists()) {
            String packageName = javaFile.getPackageName();
            File url = new File(coverageRootPath, packageName);
            boolean wasFound = false;
            url = new File(url, virtualFile.getName() + ".html");
            if (url.exists()) {
                BrowserUtil.launchBrowser(url.toURI().getPath());
                wasFound = true;
            }
            if (!wasFound) {
                Notifications.Bus.notify(getMissingFileNotification(virtualFile), project);
            }
        } else {
            Notifications.Bus.notify(getMissingReportNotification(e), project);
        }
    }

    private Notification getMissingFileNotification(@Nullable VirtualFile url) {
        String title = "File not found";
        String content = "No coverage was found for " + (url != null ? url.getPresentableUrl() : "<null>");

        return new Notification(CoverageUtils.PLUGIN_TITLE, title, content, NotificationType.ERROR);
    }

    private Notification getMissingReportNotification(final AnActionEvent e) {
        return new Notification(
                CoverageUtils.PLUGIN_TITLE,
                "Coverage report missing",
                "Please compile the report by clicking <a href='#'>here</a> and then retry.",
                NotificationType.WARNING,
                new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                        if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
                            actionManager.tryToExecute(new GenerateCoverageReportAction(), e.getInputEvent(), null, null, true);
                        }
                    }
                });
    }
}
