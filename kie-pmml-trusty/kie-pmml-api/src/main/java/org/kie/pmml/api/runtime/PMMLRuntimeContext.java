package org.kie.pmml.api.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.pmml.api.PMMLContext;

public interface PMMLRuntimeContext extends EfestoRuntimeContext<PMMLListener>,
                                            PMMLContext<PMMLListener> {

    PMMLRequestData getRequestData();

    String getFileName();

    String getFileNameNoSuffix();

    void addMissingValueReplaced(final String fieldName, final Object missingValueReplaced);

    void addCommonTranformation(final String fieldName, final Object commonTranformation);

    void addLocalTranformation(final String fieldName, final Object commonTranformation);

    Map<String, Object> getMissingValueReplacedMap();

    Map<String, Object> getCommonTransformationMap();

    Map<String, Object> getLocalTransformationMap();

    Object getPredictedDisplayValue();

    void setPredictedDisplayValue(Object predictedDisplayValue);

    Object getEntityId();

    void setEntityId(Object entityId);

    Object getAffinity();

    void setAffinity(Object affinity);

    Map<String, Double> getProbabilityMap();

    /**
     * Returns the <b>probability map</b> evaluated by the model
     *
     * @return
     */
    LinkedHashMap<String, Double> getProbabilityResultMap();

    void setProbabilityResultMap(LinkedHashMap<String, Double> probabilityResultMap);

    Map<String, Object> getOutputFieldsMap();

}
