package org.jbpm.kie.services.impl.form.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalTask;

@ApplicationScoped
public class InMemoryFormProvider extends FreemakerFormProvider {

    private static final String DEFAULT_PROCESS = "DefaultProcess";
    private static final String DEFAULT_TASK = "DefaultTask";

    @Override
    public String render(String name, ProcessAssetDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if (process.getForms().containsKey(process.getId())) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId()).getBytes());
        } else if (process.getForms().containsKey(process.getId() + "-taskform")) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId() + "-taskform").getBytes());
        } else if (process.getForms().containsKey(DEFAULT_PROCESS)) {
            template = new ByteArrayInputStream(process.getForms().get(DEFAULT_PROCESS).getBytes());
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessAssetDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if(task != null && process != null){
            String lookupName = "";
            String formName = ((InternalTask)task).getFormName();
            if(formName != null && !formName.equals("")){
                lookupName = formName;
            }else{
                lookupName = task.getNames().get(0).getText();
                
            }
            if (process.getForms().containsKey(lookupName)) {
                template = new ByteArrayInputStream(process.getForms().get(lookupName).getBytes());
            } else if (process.getForms().containsKey(lookupName.replace(" ", "")+ "-taskform")) {
                template = new ByteArrayInputStream(process.getForms().get(lookupName.replace(" ", "") + "-taskform").getBytes());
            } else if (process.getForms().containsKey(DEFAULT_TASK)) {
                template = new ByteArrayInputStream(process.getForms().get(DEFAULT_TASK).getBytes());
            }
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public int getPriority() {
        return 3;
    }

}