package org.kie.dmn.pmml;

import org.junit.Test;
import org.kie.dmn.core.pmml.PMMLInfo;
import org.kie.dmn.core.pmml.PMMLModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class PMMLInfoTest {

    public static final Logger LOG = LoggerFactory.getLogger(PMMLInfoTest.class);

    @Test
    public void testPMMLInfo() throws Exception {
        InputStream inputStream = PMMLInfoTest.class.getResourceAsStream("test_scorecard.pmml");
        PMMLInfo<PMMLModelInfo> p0 = PMMLInfo.from(inputStream);
        assertThat(p0.getModels()).hasSize(1);
        assertThat(p0.getHeader().getPmmlNSURI()).isEqualTo("http://www.dmg.org/PMML-4_2");
        PMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName()).isEqualTo("Sample Score");
        assertThat(m0.getInputFieldNames()).contains("age", "occupation", "residenceState", "validLicense");
        assertThat(m0.getTargetFieldNames()).contains("overallScore");
        assertThat(m0.getOutputFieldNames()).contains("calculatedScore");
    }
}
