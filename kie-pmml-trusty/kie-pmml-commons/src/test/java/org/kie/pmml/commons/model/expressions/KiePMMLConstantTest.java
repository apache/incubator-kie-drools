package org.kie.pmml.commons.model.expressions;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.ProcessingDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLConstantTest {

    @Test
    void evaluate() {
        Object value = 234.45;
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant("NAME", Collections.emptyList(), value, null);
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList());
        Object retrieved = kiePMMLConstant1.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant("NAME", Collections.emptyList(), value,
                                                                     DATA_TYPE.STRING);
        processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                          Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                          Collections.emptyList());
        retrieved = kiePMMLConstant2.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo("234.45");
    }
}