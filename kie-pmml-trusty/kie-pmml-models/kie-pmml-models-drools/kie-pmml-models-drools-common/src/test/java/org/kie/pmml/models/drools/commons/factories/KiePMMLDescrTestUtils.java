package org.kie.pmml.models.drools.commons.factories;

import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;

public class KiePMMLDescrTestUtils {

    public static KiePMMLDroolsType getDroolsType() {
        return new KiePMMLDroolsType("FIELD", "date");
    }

    public static KiePMMLDroolsType getDottedDroolsType() {
        return new KiePMMLDroolsType("DOTTED_FIELD", "date");
    }
}
