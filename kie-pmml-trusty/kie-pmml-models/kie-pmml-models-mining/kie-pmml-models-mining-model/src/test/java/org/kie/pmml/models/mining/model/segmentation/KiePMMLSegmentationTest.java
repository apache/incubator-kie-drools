package org.kie.pmml.models.mining.model.segmentation;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSegments;

public class KiePMMLSegmentationTest {

    private static final String SEGMENTATION_NAME = "SEGMENTATION_NAME";
    private static final MULTIPLE_MODEL_METHOD MULTIPLE_MODELMETHOD = MULTIPLE_MODEL_METHOD.MAJORITY_VOTE;
    private static KiePMMLSegmentation.Builder BUILDER;
    private static KiePMMLSegmentation KIE_PMML_SEGMENTATION;

    @BeforeAll
    public static void setup() {
        BUILDER = KiePMMLSegmentation.builder(SEGMENTATION_NAME, Collections.emptyList(),
                                              MULTIPLE_MODELMETHOD);
        assertThat(BUILDER).isNotNull();
        KIE_PMML_SEGMENTATION = BUILDER.build();
        assertThat(KIE_PMML_SEGMENTATION).isNotNull();
    }

    @Test
    void getMultipleModelMethod() {
        assertThat(KIE_PMML_SEGMENTATION.getMultipleModelMethod()).isEqualTo(MULTIPLE_MODELMETHOD);
    }

    @Test
    void getSegments() {
        assertThat(KIE_PMML_SEGMENTATION.getSegments()).isNull();
        final List<KiePMMLSegment> segments = getKiePMMLSegments();
        KIE_PMML_SEGMENTATION = BUILDER.withSegments(segments).build();
        assertThat(KIE_PMML_SEGMENTATION.getSegments()).isEqualTo(segments);
    }




}