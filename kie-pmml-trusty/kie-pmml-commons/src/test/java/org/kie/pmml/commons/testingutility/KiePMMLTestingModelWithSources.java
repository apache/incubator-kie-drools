package org.kie.pmml.commons.testingutility;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLModelWithSources;

public class KiePMMLTestingModelWithSources extends KiePMMLModelWithSources {

    private static final long serialVersionUID = 6937400978611393947L;

    private static final String FILE_NAME = "TestFile";

    public KiePMMLTestingModelWithSources(String modelName, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(FILE_NAME, modelName, kmodulePackageName, Collections.emptyList(), Collections.emptyList(),
              Collections.emptyList(), sourcesMap, false);
    }
}