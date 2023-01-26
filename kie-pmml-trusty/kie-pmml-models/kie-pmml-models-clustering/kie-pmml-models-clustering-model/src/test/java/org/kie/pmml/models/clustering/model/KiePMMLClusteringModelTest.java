package org.kie.pmml.models.clustering.model;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KiePMMLClusteringModelTest {

    @Test
    void convertToDouble_validValues() {
        Double expected = 3.0;
        List<Object> inputs = Arrays.asList(3, 3.0, 3.0f);
        inputs.forEach(number -> {
            Double retrieved = KiePMMLClusteringModel.convertToDouble(number);
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    void convertToDouble_invalidValues() {
        List<Object> inputs = Arrays.asList("3", "3.0", true);
        inputs.forEach(number -> {
            assertThatThrownBy(() -> KiePMMLClusteringModel.convertToDouble(number))
                    .isInstanceOf(IllegalArgumentException.class);
        });
    }
}