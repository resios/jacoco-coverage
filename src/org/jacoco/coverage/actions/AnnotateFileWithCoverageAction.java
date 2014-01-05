package org.jacoco.coverage.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.FrameWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jacoco.coverage.util.CoverageUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.net.URI;
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

            File coverageRootPath = CoverageUtils.getCoverageReportRoot(project);
            if (coverageRootPath.exists()) {
                String packageName = javaFile.getPackageName();
                File url = new File(coverageRootPath, packageName);
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null) {
                    url = new File(url, virtualFile.getName() + ".html");
                    BrowserUtil.launchBrowser(url.toURI().getPath());
                }
            }
        }

    }
}
