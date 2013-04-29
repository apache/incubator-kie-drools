package org.jbpm.kie.services.impl.form;

import java.io.InputStream;

import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.task.model.Task;

public interface FormProvider {
    
    int getPriority();

    InputStream provideProcessForm(ProcessDesc process);
    
    InputStream provideTaskForm(Task task, ProcessDesc process);
}
