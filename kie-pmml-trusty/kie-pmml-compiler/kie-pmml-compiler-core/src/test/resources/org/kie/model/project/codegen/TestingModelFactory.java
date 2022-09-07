package org.kie.model.project.codegen;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestingModelFactory implements KiePMMLModelFactory {

    private static final List<KiePMMLModel> KIE_PMML_MODELS = Arrays.asList(TestMod.getModel());

    @Override
    public List<KiePMMLModel> getKiePMMLModels() {
        return Collections.unmodifiableList(KIE_PMML_MODELS);
    }
}
