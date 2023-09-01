package org.kie.pmml.models.drools.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class KiePMMLDroolsModelTest {

    private final static String MODEL_NAME = "MODELNAME";
    private final static String KMODULE_PACKAGE_NAME = getSanitizedPackageName(MODEL_NAME);
    private final static List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private KiePMMLDroolsModel kiePMMLDroolsModel;

    @BeforeEach
    public void setup() {
        kiePMMLDroolsModel = new KiePMMLDroolsModelFake(MODEL_NAME, KMODULE_PACKAGE_NAME, EXTENSIONS);
    }

    @Test
    void constructor() {
        assertThat(kiePMMLDroolsModel.getName()).isEqualTo(MODEL_NAME);
        assertThat(kiePMMLDroolsModel.getExtensions()).isEqualTo(EXTENSIONS);
        assertThat(kiePMMLDroolsModel.getKModulePackageName()).isEqualTo(getSanitizedPackageName(MODEL_NAME));
    }


    private final class KiePMMLDroolsModelFake extends KiePMMLDroolsModel {

        protected KiePMMLDroolsModelFake(String modelName,
                                         String kModulePackageName,
                                         List<KiePMMLExtension> extensions) {
            super("FILENAME", modelName, extensions);
            this.kModulePackageName = kModulePackageName;
        }

        @Override
        public String getKModulePackageName() {
            return super.getKModulePackageName();
        }
    }
}