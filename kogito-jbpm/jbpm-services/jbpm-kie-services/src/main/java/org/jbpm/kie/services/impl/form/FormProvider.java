package org.jbpm.kie.services.impl.form;

import java.util.Map;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.api.task.model.Task;

public interface FormProvider {

    int getPriority();

    String render(String name, ProcessAssetDesc process, Map<String, Object> renderContext);

    String render(String name, Task task, ProcessAssetDesc process, Map<String, Object> renderContext);
}