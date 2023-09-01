package org.kie.pmml.models.drools.scorecard.model;

import java.util.List;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;

public class KiePMMLScorecardModel extends KiePMMLDroolsModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.SCORECARD_MODEL;
    private static final long serialVersionUID = 3726828657243287195L;

    protected KiePMMLScorecardModel(String fileName, String modelName, List<KiePMMLExtension> extensions) {
        super(fileName, modelName, extensions);
    }

    public static Builder builder(String fileName, String name, List<KiePMMLExtension> extensions,
                                  MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, extensions, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    public static class Builder extends KiePMMLDroolsModel.Builder<KiePMMLScorecardModel> {

        private Builder(String fileName, String name, List<KiePMMLExtension> extensions,
                        MINING_FUNCTION miningFunction) {
            super("Scorecard-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLScorecardModel(fileName, name, extensions));
        }
    }
}
