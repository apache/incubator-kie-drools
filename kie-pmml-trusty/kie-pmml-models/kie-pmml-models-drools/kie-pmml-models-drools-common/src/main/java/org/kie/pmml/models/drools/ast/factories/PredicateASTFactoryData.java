package org.kie.pmml.models.drools.ast.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.OutputField;
import org.dmg.pmml.Predicate;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

/**
 * Data class to contain objects required by <b>Predicate</b>s concrete ASTFactories
 */
public class PredicateASTFactoryData {

    private final Predicate predicate;
    private final List<OutputField> outputFields;
    private final List<KiePMMLDroolsRule> rules;
    private final String parentPath;
    private final String currentRule;
    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    public PredicateASTFactoryData(Predicate predicate,
                                   List<OutputField> outputFields,
                                   List<KiePMMLDroolsRule> rules,
                                   String parentPath,
                                   String currentRule,
                                   Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        this.predicate = predicate;
        this.outputFields = outputFields != null ? Collections.unmodifiableList(outputFields) : Collections.emptyList();
        this.rules = rules;
        this.parentPath = parentPath;
        this.currentRule = currentRule;
        this.fieldTypeMap = fieldTypeMap != null ? Collections.unmodifiableMap(fieldTypeMap) : Collections.emptyMap();
    }

    public PredicateASTFactoryData cloneWithPredicate(Predicate predicate) {
        return new PredicateASTFactoryData(predicate, outputFields, rules, parentPath, currentRule, fieldTypeMap);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<OutputField> getOutputFields() {
        return outputFields;
    }

    public List<KiePMMLDroolsRule> getRules() {
        return rules;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getCurrentRule() {
        return currentRule;
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }
}
