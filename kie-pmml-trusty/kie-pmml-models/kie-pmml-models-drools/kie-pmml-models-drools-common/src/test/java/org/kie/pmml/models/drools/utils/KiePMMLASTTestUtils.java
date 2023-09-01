package org.kie.pmml.models.drools.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDerivedFieldASTFactory;
import org.kie.pmml.models.drools.ast.factories.PredicateASTFactoryData;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;

/**
 * Utility methods for other <b>Test</b> classes
 */
public class KiePMMLASTTestUtils {

    public static PredicateASTFactoryData getPredicateASTFactoryData(Predicate predicate,
                                                                     List<OutputField> outputFields,
                                                                     List<KiePMMLDroolsRule> rules,
                                                                     String parentPath,
                                                                     String currentRule,
                                                                     Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new PredicateASTFactoryData(predicate, outputFields, rules, parentPath, currentRule, fieldTypeMap);
    }

    public static Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final LocalTransformations localTransformations) {
        final Map<String, KiePMMLOriginalTypeGeneratedType> toReturn = new HashMap<>();
        KiePMMLDerivedFieldASTFactory kiePMMLDerivedFieldASTFactory = KiePMMLDerivedFieldASTFactory.factory(toReturn);
        if (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(transformationDictionary.getDerivedFields());
        }
        if (localTransformations != null && localTransformations.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(localTransformations.getDerivedFields());
        }
        KiePMMLDataDictionaryASTFactory.factory(toReturn).declareTypes(getFieldsFromDataDictionary(dataDictionary));
        return toReturn;
    }

    public static DataField getTypeDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setDataType(DataType.DATE);
        toReturn.setName(FieldName.create("dataField"));
        return toReturn;
    }

    public static DataField getDottedTypeDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setDataType(DataType.BOOLEAN);
        toReturn.setName(FieldName.create("dotted.field"));
        return toReturn;
    }

}
