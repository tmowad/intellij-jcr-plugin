package velir.intellij.cq5.actions.content;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.*;
import org.jdom.Document;
import velir.intellij.cq5.jcr.model.VComponent;
import velir.intellij.cq5.ui.ComponentDialog;

import java.io.IOException;
import java.io.OutputStream;

public class NewComponent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		DataContext dataContext = anActionEvent.getDataContext();
		IdeView ideView = LangDataKeys.IDE_VIEW.getData(dataContext);
		Module module = LangDataKeys.MODULE.getData(dataContext);
		Application application = ApplicationManager.getApplication();
		DomManager manager = DomManager.getDomManager(project);

		PsiDirectory[] dirs = ideView.getDirectories();

		ComponentDialog componentDialog = new ComponentDialog(project);
		componentDialog.show();
		if (componentDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
			final VComponent vComponent = componentDialog.getVComponent();
			for (final PsiDirectory dir : dirs) {
				application.runWriteAction(new Runnable() {
					public void run() {
						PsiDirectory contentDirectory = dir.createSubdirectory(vComponent.getName());
						PsiFile contentFile = contentDirectory.createFile(".content.xml");
						VirtualFile virtualFile = contentFile.getVirtualFile();
						try {
							OutputStream outputStream = virtualFile.getOutputStream(vComponent);
							Document document = new Document(vComponent.getElement());
							JDOMUtil.writeDocument(document, outputStream, "\n");
							outputStream.close();
						} catch (IOException ioe) {
							Messages.showMessageDialog(project, "Could not write to content file", "Error", Messages.getErrorIcon());
						}

					}
				});
			}
		}

		//Messages.showMessageDialog(project, "Version 2", "Information", Messages.getInformationIcon());

	}
}