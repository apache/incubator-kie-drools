package org.jbpm.kie.services.impl.form.provider;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.impl.form.FormProvider;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.task.model.Task;

@ApplicationScoped
public class ClasspathFormProvider implements FormProvider {

    @Override
    public InputStream provideProcessForm(ProcessDesc process) {
        return  this.getClass().getResourceAsStream("/forms/DefaultProcess.ftl");
    }

    @Override
    public InputStream provideTaskForm(Task task, ProcessDesc process) {
        return this.getClass().getResourceAsStream("/forms/DefaultTask.ftl");
    }

    @Override
    public int getPriority() {
        return 1000;
    }

}
