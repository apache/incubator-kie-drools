package org.kie.pmml.commons.testingutility;

import java.util.List;
import java.util.Map;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * <b>Fake</b> model used for testing. It is mapped to <code>PMML_MODEL.TEST_MODEL</code>
 */
public class KiePMMLTestingModel extends KiePMMLModel {


    private static final long serialVersionUID = 9009765353822151536L;
    public static PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TEST_MODEL;

    public KiePMMLTestingModel(String fileName, String name, List<KiePMMLExtension> extensions) {
        super(fileName,  name, extensions);
    }

    public static Builder builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, extensions, miningFunction);
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        return context;
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLTestingModel> {

        private Builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
            super("TestingModel-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLTestingModel(fileName, name, extensions));
        }
    }
}