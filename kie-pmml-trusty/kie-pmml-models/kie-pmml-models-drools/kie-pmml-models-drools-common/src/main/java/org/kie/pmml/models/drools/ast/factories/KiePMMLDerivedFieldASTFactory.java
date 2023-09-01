package org.kie.pmml.models.drools.ast.factories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DerivedField;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * Class used to generate <code>KiePMMLDroolsType</code>s out of a <code>DerivedField</code>
 */
public class KiePMMLDerivedFieldASTFactory {

    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    private KiePMMLDerivedFieldASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }

    /**
     * @param fieldTypeMap the <code>Map&lt;String, KiePMMLOriginalTypeGeneratedType&gt;</code> to be populated with mapping between original field' name and <b>original type/generated type</b> tupla
     * @return
     */
    public static KiePMMLDerivedFieldASTFactory factory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new KiePMMLDerivedFieldASTFactory(fieldTypeMap);
    }

    /**
     * Create a <code>List&lt;KiePMMLDroolsType&gt;</code> out of original <code>List&lt;DerivedField&gt;</code>s,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param derivedFields
     */
    public List<KiePMMLDroolsType> declareTypes(final List<DerivedField> derivedFields) {
        return derivedFields.stream().map(this::declareType).collect(Collectors.toList());
    }

    /**
     * Create a <code>KiePMMLDroolsType</code> out of original <code>DerivedField</code>,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param derivedField
     */
    public KiePMMLDroolsType declareType(DerivedField derivedField) {
        String generatedType = getSanitizedClassName(derivedField.getName().getValue().toUpperCase());
        String fieldName = derivedField.getName().getValue();
        String fieldType = derivedField.getDataType().value();
        fieldTypeMap.put(fieldName, new KiePMMLOriginalTypeGeneratedType(fieldType, generatedType));
        return new KiePMMLDroolsType(generatedType, DATA_TYPE.byName(fieldType).getMappedClass().getSimpleName());
    }
}
