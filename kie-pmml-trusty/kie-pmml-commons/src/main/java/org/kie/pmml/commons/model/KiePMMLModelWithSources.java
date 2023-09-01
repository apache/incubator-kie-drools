package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

/**
 * Interface used to identify a given <code>HasSourcesMap</code> as <b>container</b> for KiePMMLModel' sources
 */
public class KiePMMLModelWithSources extends KiePMMLModel implements HasSourcesMap {

    private static final long serialVersionUID = -7528313589316228283L;
    private final String kmodulePackageName;
    private final List<MiningField> miningFields;
    private final List<OutputField> outputFields;
    private final List<TargetField> targetFields;
    private final Map<String, String> sourcesMap;
    private final boolean isInterpreted;

    public KiePMMLModelWithSources(final String fileName,
                                   final String modelName,
                                   final String kmodulePackageName,
                                   final List<MiningField> miningFields,
                                   final List<OutputField> outputFields,
                                   final List<TargetField> targetFields,
                                   final Map<String, String> sourcesMap,
                                   final boolean isInterpreted) {
        super(fileName, modelName, Collections.emptyList());
        this.kmodulePackageName = kmodulePackageName;
        this.miningFields = miningFields;
        this.outputFields = outputFields;
        this.targetFields = targetFields;
        this.sourcesMap = sourcesMap;
        this.isInterpreted = isInterpreted;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        throw new KiePMMLException("KiePMMLModelWithSources is not meant to be used for actual evaluation");
    }

    @Override
    public List<MiningField> getMiningFields() {
        return miningFields;
    }

    @Override
    public List<OutputField> getOutputFields() {
        return outputFields;
    }

    public List<TargetField> getTargetFields() {
        return targetFields;
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

    @Override
    public boolean isInterpreted() {
        return isInterpreted;
    }

}
