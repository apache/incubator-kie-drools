package org.kie.pmml.pmml_4_2.model.mining;

import java.util.Map;

import org.kie.api.pmml.ModelApplier;


public interface MiningModelApplier extends ModelApplier {

    public Map<String, SegmentExecution> getSegmentExecutions();
}
