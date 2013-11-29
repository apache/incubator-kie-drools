package org.jbpm.kie.services.impl.form.provider;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.api.task.model.Task;

@ApplicationScoped
public class ClasspathFormProvider extends FreemakerFormProvider {
    @Override
    public String render(String name, ProcessAssetDesc process, Map<String, Object> renderContext) {
        return render(name, this.getClass().getResourceAsStream("/forms/DefaultProcess.ftl"), renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessAssetDesc process, Map<String, Object> renderContext) {
        return render(name, this.getClass().getResourceAsStream("/forms/DefaultTask.ftl"), renderContext);
    }

    @Override
    public int getPriority() {
        return 1000;
    }

}