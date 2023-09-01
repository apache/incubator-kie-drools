package org.kie.pmml.models.drools.ast.factories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.Field;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;

/**
 * Class used to generate <code>KiePMMLDroolsType</code>s out of a <code>DataDictionary</code>
 */
public class KiePMMLDataDictionaryASTFactory {

    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    private KiePMMLDataDictionaryASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }

    /**
     * @param fieldTypeMap the <code>Map&lt;String, KiePMMLOriginalTypeGeneratedType&gt;</code> to be populated with
     * mapping between original field' name and <b>original type/generated type</b> tupla
     * @return
     */
    public static KiePMMLDataDictionaryASTFactory factory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new KiePMMLDataDictionaryASTFactory(fieldTypeMap);
    }

    /**
     * Create a <code>List&lt;KiePMMLDroolsType&gt;</code> out of original <code>Field</code>s,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original
     * type/generated type</b> tupla
     * @param fields
     */
    public List<KiePMMLDroolsType> declareTypes(final List<Field<?>> fields) {
        return fields.stream().map(this::declareType).collect(Collectors.toList());
    }

    /**
     * Create a <code>KiePMMLDroolsType</code> out of original <code>DataField</code>,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original
     * type/generated type</b> tupla
     * @param field
     */
    public KiePMMLDroolsType declareType(Field field) {
        String generatedType = getGeneratedClassName(field.getName().getValue());
        String fieldName = field.getName().getValue();
        String fieldType = field.getDataType().value();
        fieldTypeMap.put(fieldName, new KiePMMLOriginalTypeGeneratedType(fieldType, generatedType));
        return new KiePMMLDroolsType(generatedType, DATA_TYPE.byName(fieldType).getMappedClass().getSimpleName());
    }
}
