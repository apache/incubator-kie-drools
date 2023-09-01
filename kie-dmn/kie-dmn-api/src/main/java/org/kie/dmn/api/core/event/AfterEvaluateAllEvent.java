package org.kie.dmn.api.core.event;

public interface AfterEvaluateAllEvent extends DMNEvent {

    String getModelNamespace();

    String getModelName();

}
