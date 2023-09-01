package org.kie.dmn.api.core.event;

public interface BeforeEvaluateAllEvent extends DMNEvent {

    String getModelNamespace();

    String getModelName();

}
