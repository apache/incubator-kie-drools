package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getFieldTypeMap;

public class KiePMMLScorecardModelASTFactoryTest {

    private static final String SOURCE_SAMPLE = "ScorecardSample.pmml";
    private PMML samplePmml;
    private Scorecard scorecardModel;

    @BeforeEach
    public void setUp() throws Exception {
        samplePmml = TestUtils.loadFromFile(SOURCE_SAMPLE);
        assertThat(samplePmml).isNotNull();
        assertThat(samplePmml.getModels()).hasSize(1);
        assertThat(samplePmml.getModels().get(0)).isInstanceOf(Scorecard.class);
        scorecardModel = ((Scorecard) samplePmml.getModels().get(0));
    }

    @Test
    void getKiePMMLDroolsSampleAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(samplePmml.getDataDictionary(), samplePmml.getTransformationDictionary(),  scorecardModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(samplePmml.getDataDictionary()), scorecardModel, fieldTypeMap, types);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTypes()).isEqualTo(types);
        assertThat(retrieved.getRules()).isNotEmpty();
    }

}