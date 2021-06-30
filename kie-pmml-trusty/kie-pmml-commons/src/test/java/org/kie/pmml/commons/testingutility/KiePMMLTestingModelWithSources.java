package org.kie.pmml.commons.testingutility;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.commons.model.HasSourcesMap;

public class KiePMMLTestingModelWithSources extends KiePMMLTestingModel implements HasSourcesMap {

    private final String kmodulePackageName;
    protected Map<String, String> sourcesMap;

    public KiePMMLTestingModelWithSources(String modelName, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(modelName, Collections.emptyList());
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
    }

    @Override
    public Map<String, String> getSourcesMap() {
        return Collections.unmodifiableMap(sourcesMap);
    }

    @Override
    public void addSourceMap(String key, String value) {
        sourcesMap.put(key, value);
    }

    @Override
    public String getKModulePackageName() {
        return kmodulePackageName;
    }
}