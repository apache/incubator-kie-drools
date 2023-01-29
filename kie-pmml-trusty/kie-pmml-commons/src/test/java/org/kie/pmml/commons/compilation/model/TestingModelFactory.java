package org.kie.pmml.commons.compilation.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;

public class TestingModelFactory implements KiePMMLModelFactory {

    private static final List<KiePMMLModel> KIE_PMML_MODELS = Collections.singletonList(TestMod.getModel());

    @Override
    public List<KiePMMLModel> getKiePMMLModels() {
        return Collections.unmodifiableList(KIE_PMML_MODELS);
    }
}
