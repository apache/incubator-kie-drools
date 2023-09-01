package org.kie.dmn.api.core;

import java.util.List;

public interface AfterGeneratingSourcesListener {

    void accept(List<GeneratedSource> generatedSource);
}

