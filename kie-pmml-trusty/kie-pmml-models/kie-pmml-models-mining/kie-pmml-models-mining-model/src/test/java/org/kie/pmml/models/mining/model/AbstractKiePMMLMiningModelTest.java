package org.kie.pmml.models.mining.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

public abstract class AbstractKiePMMLMiningModelTest {

    public static KiePMMLSegmentation getKiePMMLSegmentation(String segmentationName) {
        return KiePMMLSegmentation.builder(segmentationName, Collections.emptyList(),
                                           MULTIPLE_MODEL_METHOD.AVERAGE)
                .withSegments(getKiePMMLSegments())
                .build();
    }

    public static List<KiePMMLSegment> getKiePMMLSegments() {
        return IntStream.range(0, 3)
                .mapToObj(i -> getKiePMMLSegment("SEGMENT-"+i))
                .collect(Collectors.toList());
    }

    public static KiePMMLSegment getKiePMMLSegment(String segmentName) {
        return KiePMMLSegment.builder(segmentName,
                                      Collections.emptyList(),
                                      getKiePMMLSimplePredicate(segmentName + "-PREDICATE"),
                                      getKiePMMLModel(segmentName + "-MODEL"))
                .build();
    }

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(String predicateName) {
        return KiePMMLSimplePredicate.builder(predicateName, Collections.emptyList(), OPERATOR.EQUAL).build();
    }

    public static KiePMMLModel getKiePMMLModel(String modelName) {
        return new KiePMMLTestingModel("fileName", modelName, Collections.emptyList());
    }
}
