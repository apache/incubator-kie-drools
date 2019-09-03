package org.kie.pmml.pmml_4_2.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.Model;
import org.drools.model.Rule;
import org.kie.pmml.pmml_4_2.model.ScorecardModel;
import org.kie.pmml.pmml_4_2.model.scorecard.ComplexScore;

public class ScorecardRulesProvider implements RuleProvider {

    private static final String COMPLEX_PARTIAL_SCORE_BASE = "Complex_Partial_Score_";
    private Map<String, Rule> mappedRules;
    private String modelId;
    private ScorecardModel scModel;

    public ScorecardRulesProvider(ScorecardModel scModel) {
        this.modelId = scModel.getModelId();
    }

    @Override
    public Collection<String> getProvidedRuleNames() {
        return mappedRules.keySet();
    }

    @Override
    public Rule getRule(String ruleName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Model addPartialScoreRules(Model model, Collection<ComplexScore> complexScores) {
        complexScores.forEach(cs -> {
            String ruleName = COMPLEX_PARTIAL_SCORE_BASE + modelId + "_" + cs.getCharacteristicName() + "_" + cs.getAttributeIndex();
            //            Rule rule = rule(scModel.getModelPackageName(),ruleName).unit(ScorecardUnit.class).build(
            //                                                                                                     forall(exists(var))
            //                                                                                                     );
            //            mappedRules.put(ruleName, rule);
            //            model.getRules().add(rule);
        });
        return model;
    }

    private void initializeMappedRules() {
        mappedRules = new HashMap<>();
        mappedRules.put(COMPLEX_PARTIAL_SCORE_BASE + modelId, null);
    }

    public static class ScorecardUnit {

    }
}
