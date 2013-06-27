package org.jbpm.kie.services.impl.form;

import java.util.Map;

import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.task.model.Task;

public interface FormProvider {

    int getPriority();

    String render(String name, ProcessDesc process, Map<String, Object> renderContext);

    String render(String name, Task task, ProcessDesc process, Map<String, Object> renderContext);
}