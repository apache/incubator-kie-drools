package org.kie.pmml.compiler.testingutils;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;

public class TestModelImplementationProvider implements ModelImplementationProvider<TestingModel, KiePMMLTestingModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TEST_MODEL;
    }

    @Override
    public Class<KiePMMLTestingModel> getKiePMMLModelClass() {
        return KiePMMLTestingModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<TestingModel> compilationDTO) {
        return Collections.emptyMap();
    }


}
