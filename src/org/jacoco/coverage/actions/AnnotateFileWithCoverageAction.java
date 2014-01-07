package org.jacoco.coverage.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testIntegration.TestFinderHelper;
import org.jacoco.coverage.util.CoverageUtils;

import java.io.File;
import java.util.List;

public class AnnotateFileWithCoverageAction extends AnAction {


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

            File coverageRootPath = CoverageUtils.getCoverageReportRoot(project);
            if (coverageRootPath.exists()) {
                String packageName = javaFile.getPackageName();
                File url = new File(coverageRootPath, packageName);
                VirtualFile virtualFile = javaFile.getVirtualFile();
                if (virtualFile != null) {
                    url = new File(url, virtualFile.getName() + ".html");
                    if (url.exists()){
                        BrowserUtil.launchBrowser(url.toURI().getPath());
                    }
                }
            }
        }
    }
}
