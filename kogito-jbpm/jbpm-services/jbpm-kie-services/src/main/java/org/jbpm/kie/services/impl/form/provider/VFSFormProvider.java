package org.jbpm.kie.services.impl.form.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.form.FormProviderServiceImpl;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.api.task.model.Task;
import org.kie.commons.java.nio.file.Path;

@ApplicationScoped
public class VFSFormProvider extends FreemakerFormProvider {

    @Inject
    private FileService fileService;

    @Override
    public String render(String name, ProcessDesc process, Map<String, Object> renderContext) {
        if (process == null || process.getOriginalPath() == null) {
            return null;
        }

        InputStream template = null;
        Iterable<Path> availableForms = null;
        Path processPath = fileService.getPath(process.getOriginalPath());
        Path formsPath = fileService.getPath(processPath.getParent().toUri().toString() + "/forms/");
        try {

            if(fileService.exists(formsPath)){
                availableForms = fileService.loadFilesByType(formsPath, "ftl");
            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path selectedForm = null;
        if(availableForms != null){
            for (Path p : availableForms) {
                if (p.getFileName().toString().contains(process.getId())) {
                    selectedForm = p;
                }
            }
        }

        try {
            if (selectedForm == null) {
                String rootPath = processPath.getRoot().toUri().toString();
                if (!rootPath.endsWith(processPath.getFileSystem().getSeparator())) {
                    rootPath +=processPath.getFileSystem().getSeparator();
                }

                Path defaultFormPath = fileService.getPath(rootPath +"globals/forms/DefaultProcess.ftl");
                if (fileService.exists(defaultFormPath)) {
                    template = new ByteArrayInputStream(fileService.loadFile(defaultFormPath));
                }

            } else {

                template = new ByteArrayInputStream(fileService.loadFile(selectedForm));

            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        Path processPath = null;

        Iterable<Path> availableForms = null;
        try {
            if(process != null && process.getOriginalPath() != null){
                processPath = fileService.getPath(process.getOriginalPath());
                Path formsPath = fileService.getPath(processPath.getParent().toUri().toString() + "/forms/");
                if(fileService.exists(formsPath)){
                    availableForms = fileService.loadFilesByType(formsPath, "ftl");
                }
            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path selectedForm = null;
        if(availableForms != null){
            for (Path p : availableForms) {
                if (p.getFileName().toString().contains(task.getNames().get(0).getText())) {
                    selectedForm = p;
                }
            }
        }

        try {
            if (selectedForm == null) {
                String rootPath = "";
                if(processPath != null){
                    rootPath = processPath.getRoot().toUri().toString();
                    if (!rootPath.endsWith(processPath.getFileSystem().getSeparator())) {
                        rootPath +=processPath.getFileSystem().getSeparator();
                    }
                }
                if(!rootPath.equals("")){
                    Path defaultFormPath = fileService.getPath(rootPath +"globals/forms/DefaultTask.ftl");
                    if (fileService.exists(defaultFormPath)) {
                        template = new ByteArrayInputStream(fileService.loadFile(defaultFormPath));
                    }
                }
            } else {
                template = new ByteArrayInputStream(fileService.loadFile(selectedForm));
            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public int getPriority() {
        return 1;
    }

}