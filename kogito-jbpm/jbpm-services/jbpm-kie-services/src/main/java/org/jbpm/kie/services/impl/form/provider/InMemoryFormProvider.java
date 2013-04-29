package org.jbpm.kie.services.impl.form.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.impl.form.FormProvider;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.task.model.Task;

@ApplicationScoped
public class InMemoryFormProvider implements FormProvider {

    private static final String DEFAULT_PROCESS = "DefaultProcess";
    private static final String DEFAULT_TASK = "DefaultTask";
    
    @Override
    public InputStream provideProcessForm(ProcessDesc process) {
        InputStream template = null;
        if (process.getForms().containsKey(process.getId())) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId()).getBytes());
        } else if (process.getForms().containsKey(DEFAULT_PROCESS)) {
            template = new ByteArrayInputStream(process.getForms().get(DEFAULT_PROCESS).getBytes());
        }
        return template;
    }

    @Override
    public InputStream provideTaskForm(Task task, ProcessDesc process) {
        InputStream template = null;
        String taskName = task.getNames().get(0).getText();
        if (process.getForms().containsKey(taskName)) {
            template = new ByteArrayInputStream(process.getForms().get(taskName).getBytes());
        } else if (process.getForms().containsKey(DEFAULT_TASK)) {
            template = new ByteArrayInputStream(process.getForms().get(DEFAULT_TASK).getBytes());
        }
        return template;
    }

    @Override
    public int getPriority() {
        return 2;
    }

}
