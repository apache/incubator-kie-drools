package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

/**
 * This is the model used to store sources for <code>KiePMMLModelFactory</code> classes;
 * <code>KiePMMLModelFactory</code>, in turns, are used to retrieve <code>List&lt;KiePMMLModel&gt;</code>s
 * from kjar inside <code>PMMLAssemblerService</code>
 */
public class KiePMMLFactoryModel extends KiePMMLModel implements HasSourcesMap {

    private static final long serialVersionUID = 1654176510018808424L;
    private final String kmodulePackageName;
    protected Map<String, String> sourcesMap;

    public KiePMMLFactoryModel(String fileName, String name, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(fileName, name, Collections.emptyList());
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext pmmlContext) {
        throw new KiePMMLException("KiePMMLFactoryModel is not meant to be used for actual evaluation");
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
