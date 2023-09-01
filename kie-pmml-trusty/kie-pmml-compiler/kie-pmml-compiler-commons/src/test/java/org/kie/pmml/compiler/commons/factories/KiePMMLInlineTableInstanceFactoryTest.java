package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.InlineTable;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomInlineTableWithCells;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLInlineTableWithCells;

public class KiePMMLInlineTableInstanceFactoryTest {

    @Test
    void getKiePMMLInlineTable() {
        final InlineTable toConvert = getRandomInlineTableWithCells();
        final KiePMMLInlineTable retrieved = KiePMMLInlineTableInstanceFactory.getKiePMMLInlineTable(toConvert);
        commonVerifyKiePMMLInlineTableWithCells(retrieved, toConvert);
    }
}