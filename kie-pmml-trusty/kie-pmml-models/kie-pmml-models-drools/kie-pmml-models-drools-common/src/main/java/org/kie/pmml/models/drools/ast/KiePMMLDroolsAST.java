package org.kie.pmml.models.drools.ast;

import java.util.List;

/**
 * Data-class used to store information needed to generate a whole <b>Drools descr</b>
 */
public class KiePMMLDroolsAST {

    private final List<KiePMMLDroolsType> types;
    private final List<KiePMMLDroolsRule> rules;

    public KiePMMLDroolsAST(List<KiePMMLDroolsType> types, List<KiePMMLDroolsRule> rules) {
        this.types = types;
        this.rules = rules;
    }

    public List<KiePMMLDroolsType> getTypes() {
        return types;
    }

    public List<KiePMMLDroolsRule> getRules() {
        return rules;
    }
}
