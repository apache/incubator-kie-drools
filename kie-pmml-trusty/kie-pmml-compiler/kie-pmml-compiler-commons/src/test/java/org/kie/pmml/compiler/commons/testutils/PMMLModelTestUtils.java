/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.testutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.compiler.commons.mocks.TestModel;

/**
 * Helper methods related to <b>PMML</b> original model
 */
public class PMMLModelTestUtils {

    private PMMLModelTestUtils() {
        // Avoid instantiation
    }

    public static PMML getPMMLWithRandomTestModel() {
        PMML toReturn = new PMML();
        DataDictionary dataDictionary = getRandomDataDictionary();
        toReturn.setDataDictionary(dataDictionary);
        toReturn.addModels(getRandomTestModel(dataDictionary));
        return toReturn;
    }

    public static DataDictionary getDataDictionary(List<DataField> dataFields) {
        DataDictionary toReturn = new DataDictionary();
        toReturn.addDataFields(dataFields.toArray(new DataField[0]));
        return toReturn;
    }

    public static TransformationDictionary getTransformationDictionary() {
        TransformationDictionary toReturn = new TransformationDictionary();
        return toReturn;
    }

    public static MiningSchema getMiningSchema(List<MiningField> miningFields) {
        MiningSchema toReturn = new MiningSchema();
        toReturn.addMiningFields(miningFields.toArray(new MiningField[0]));
        return toReturn;
    }

    public static DataDictionary getRandomDataDictionary() {
        DataDictionary toReturn = new DataDictionary();
        IntStream.range(0, new Random().nextInt(3) + 2)
                .forEach(i -> toReturn.addDataFields(getRandomDataField()));
        return toReturn;
    }

    public static MiningSchema getRandomMiningSchema() {
        MiningSchema toReturn = new MiningSchema();
        IntStream.range(0, new Random().nextInt(3)+ 2)
                .forEach(i -> toReturn.addMiningFields(getRandomMiningField()));
        return toReturn;
    }

    public static Output getRandomOutput() {
        Output toReturn = new Output();
        IntStream.range(0, new Random().nextInt(3)+ 2)
                .forEach(i -> toReturn.addOutputFields(getRandomOutputField()));
        return toReturn;
    }

    public static TestModel getRandomTestModel() {
        TestModel toReturn = new TestModel();
        toReturn.setModelName(RandomStringUtils.random(6, true, false));
        toReturn.setMiningSchema(getRandomMiningSchema());
        toReturn.setOutput(getRandomOutput());
        return toReturn;
    }

    public static TestModel getRandomTestModel(DataDictionary dataDictionary) {
        TestModel toReturn = new TestModel();
        List<DataField> dataFields = dataDictionary.getDataFields();
        MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, dataFields.size() -1)
                .forEach(i -> {
                            DataField dataField = dataFields.get(i);
                            MiningField miningField = new MiningField();
                            miningField.setName(dataField.getName());
                            miningField.setUsageType(MiningField.UsageType.ACTIVE);
                            miningSchema.addMiningFields(miningField);
                          });
        DataField lastDataField = dataFields.get(dataFields.size()-1);
        MiningField predictedMiningField = new MiningField();
        predictedMiningField.setName(lastDataField.getName());
        predictedMiningField.setUsageType(MiningField.UsageType.PREDICTED);
        miningSchema.addMiningFields(predictedMiningField);
        Output output = new Output();
        OutputField outputField = new OutputField();
        outputField.setName(FieldName.create("OUTPUT_" + lastDataField.getName().getValue()));
        outputField.setDataType(lastDataField.getDataType());
        outputField.setOpType(OpType.values()[new Random().nextInt(OpType.values().length)]);
        toReturn.setModelName(RandomStringUtils.random(6, true, false));
        toReturn.setMiningSchema(miningSchema);
        toReturn.setOutput(output);
        return toReturn;
    }

    public static RegressionModel getRegressionModel(String modelName, MiningFunction miningFunction,
                                                     MiningSchema miningSchema,
                                                     List<RegressionTable> regressionTables) {
        RegressionModel toReturn = new RegressionModel();
        toReturn.setModelName(modelName);
        toReturn.setMiningFunction(miningFunction);
        toReturn.setMiningSchema(miningSchema);
        toReturn.addRegressionTables(regressionTables.toArray(new RegressionTable[0]));
        return toReturn;
    }

    public static RegressionTable getRegressionTable(List<CategoricalPredictor> categoricalPredictors,
                                                     List<NumericPredictor> numericPredictors,
                                                     List<PredictorTerm> predictorTerms, double intercept,
                                                     Object targetCategory) {
        RegressionTable toReturn = new RegressionTable();
        toReturn.setIntercept(intercept);
        toReturn.setTargetCategory(targetCategory);
        toReturn.addCategoricalPredictors(categoricalPredictors.toArray(new CategoricalPredictor[0]));
        toReturn.addNumericPredictors(numericPredictors.toArray(new NumericPredictor[0]));
        toReturn.addPredictorTerms(predictorTerms.toArray(new PredictorTerm[0]));
        return toReturn;
    }

    public static CategoricalPredictor getCategoricalPredictor(String name, double value, double coefficient) {
        CategoricalPredictor toReturn = new CategoricalPredictor();
        toReturn.setField(getFieldName(name));
        toReturn.setValue(value);
        toReturn.setCoefficient(coefficient);
        return toReturn;
    }

    public static NumericPredictor getNumericPredictor(String name, int exponent, double coefficient) {
        NumericPredictor toReturn = new NumericPredictor();
        toReturn.setField(getFieldName(name));
        toReturn.setExponent(exponent);
        toReturn.setCoefficient(coefficient);
        return toReturn;
    }

    public static PredictorTerm getPredictorTerm(String name, double coefficient, List<String> fieldRefNames) {
        PredictorTerm toReturn = new PredictorTerm();
        toReturn.setName(getFieldName(name));
        toReturn.setCoefficient(coefficient);
        toReturn.addFieldRefs(fieldRefNames.stream().map(PMMLModelTestUtils::getFieldRef).toArray(FieldRef[]::new));
        return toReturn;
    }

    public static DataField getDataField(String fieldName, OpType opType) {
        DataField toReturn = new DataField();
        toReturn.setName(getFieldName(fieldName));
        toReturn.setOpType(opType);
        return toReturn;
    }

    public static DataField getDataField(String fieldName, OpType opType, DataType dataType) {
        DataField toReturn = getDataField(fieldName, opType);
        toReturn.setDataType(dataType);
        return toReturn;
    }

    public static MiningField getMiningField(String fieldName, MiningField.UsageType usageType) {
        MiningField toReturn = new MiningField();
        toReturn.setName(getFieldName(fieldName));
        toReturn.setUsageType(usageType);
        return toReturn;
    }

    public static DataField getRandomDataField() {
        DataField toReturn = new DataField();
        toReturn.setName(getFieldName(RandomStringUtils.random(6, true, false)));
        toReturn.setDataType(getRandomDataType());
        return toReturn;
    }

    public static DataType getRandomDataType() {
        List<DataType> dataTypes = getDataTypes();
        return dataTypes.get(new Random().nextInt(dataTypes.size()));
    }

    public static MiningField getRandomMiningField() {
        Random random = new Random();
        MiningField toReturn = new MiningField();
        toReturn.setName(getFieldName(RandomStringUtils.random(6, true, false)));
        toReturn.setUsageType(MiningField.UsageType.values()[random.nextInt(MiningField.UsageType.values().length)]);
        return toReturn;
    }

    public static OutputField getRandomOutputField() {
        Random random = new Random();
        OutputField toReturn = new OutputField();
        toReturn.setName(getFieldName(RandomStringUtils.random(6, true, false)));
        toReturn.setOpType(OpType.values()[random.nextInt(OpType.values().length)]);
        List<DataType> dataTypes = getDataTypes();
        toReturn.setDataType(dataTypes.get(random.nextInt(dataTypes.size())));
        toReturn.setTargetField(getFieldName(RandomStringUtils.random(6, true, false)));
        List<ResultFeature> resultFeatures = getResultFeature();
        toReturn.setResultFeature(resultFeatures.get(random.nextInt(resultFeatures.size())));
        return toReturn;
    }

    public static ParameterField getParameterField(String fieldName, DataType dataType) {
        ParameterField toReturn = new ParameterField();
        toReturn.setDataType(dataType);
        toReturn.setName(getFieldName(fieldName));
        return toReturn;
    }

    public static List<ParameterField> getParameterFields() {
        DATA_TYPE[] dataTypes = DATA_TYPE.values();
        List<ParameterField> toReturn = new ArrayList<>();
        for (int i = 0; i < dataTypes.length; i++) {
            DataType dataType = DataType.fromValue(dataTypes[i].getName());
            ParameterField toAdd = getParameterField(dataType.value().toUpperCase(), dataType);
            toReturn.add(toAdd);
        }
        return toReturn;
    }

    public static List<DataType> getDataTypes() {
        DATA_TYPE[] dataTypes = DATA_TYPE.values();
        List<DataType> toReturn = new ArrayList<>();
        for (int i = 0; i < dataTypes.length; i++) {
            toReturn.add(DataType.fromValue(dataTypes[i].getName()));
        }
        return toReturn;
    }

    public static List<ResultFeature> getResultFeature() {
        RESULT_FEATURE[] resultFeatures = RESULT_FEATURE.values();
        List<ResultFeature> toReturn = new ArrayList<>();
        for (int i = 0; i < resultFeatures.length; i++) {
            toReturn.add(ResultFeature.fromValue(resultFeatures[i].getName()));
        }
        return toReturn;
    }

    public static SimplePredicate getSimplePredicate(final String predicateName,
                                                     final Object value,
                                                     final SimplePredicate.Operator operator) {
        FieldName fieldName = FieldName.create(predicateName);
        SimplePredicate toReturn = new SimplePredicate();
        toReturn.setField(fieldName);
        toReturn.setOperator(operator);
        toReturn.setValue(value);
        return toReturn;
    }

    public static CompoundPredicate getCompoundPredicate(final List<SimplePredicate> simplePredicates, int counter) {
        CompoundPredicate toReturn = new CompoundPredicate();
        toReturn.setBooleanOperator(getRandomCompoundPredicateAndOrOperator(counter));
        toReturn.getPredicates().addAll(getRandomSimplePredicates(simplePredicates));
        return toReturn;
    }

    public static SimpleSetPredicate getSimpleSetPredicate(final String predicateName,
                                                           final Array.Type arrayType,
                                                           final List<String> values,
                                                           final SimpleSetPredicate.BooleanOperator booleanOperator) {
        FieldName fieldName = FieldName.create(predicateName);
        SimpleSetPredicate toReturn = new SimpleSetPredicate();
        toReturn.setField(fieldName);
        toReturn.setBooleanOperator(booleanOperator);
        Array array = getArray(arrayType, values);
        toReturn.setArray(array);
        return toReturn;
    }

    public static Array getArray(Array.Type arrayType, final List<String> values) {
        String arrayString = String.join(" ", values);
        Array toReturn = new Array(arrayType, arrayString);
        toReturn.setN(values.size());
        return toReturn;
    }

    public static FieldName getFieldName(final String fieldName) {
        return FieldName.create(fieldName);
    }

    public static FieldRef getFieldRef(final String fieldName) {
        return new FieldRef(getFieldName(fieldName));
    }

    public static Object getRandomValue(DataType dataType) {
        switch (dataType) {
            case INTEGER:
                return new Random().nextInt(40);
            case DOUBLE:
                return new Random().nextDouble();
            case BOOLEAN:
                return new Random().nextBoolean();
            case STRING:
                return RandomStringUtils.random(6, true, false);
            default:
                return null;
        }
    }

    public static SimplePredicate.Operator getRandomSimplePredicateOperator() {
        final SimplePredicate.Operator[] values = SimplePredicate.Operator.values();
        int rndIndex = new Random().nextInt(values.length - 3);
        return values[rndIndex];
    }

    public static SimpleSetPredicate.BooleanOperator getRandomSimpleSetPredicateOperator() {
        final SimpleSetPredicate.BooleanOperator[] values = SimpleSetPredicate.BooleanOperator.values();
        int rndIndex = new Random().nextInt(values.length);
        return values[rndIndex];
    }

    public static List<String> getStringObjects(Array.Type arrayType, int size) {
        return IntStream.range(0, size).mapToObj(index -> {
            switch (arrayType) {
                case INT:
                    return String.valueOf(new Random().nextInt(40));
                case REAL:
                    return String.valueOf(new Random().nextDouble());
                case STRING:
                    return RandomStringUtils.random(6, true, false);
                default:
                    return null;
            }
        })
                .collect(Collectors.toList());
    }

    private static List<SimplePredicate> getRandomSimplePredicates(final List<SimplePredicate> simplePredicates) {
        int firstIndex = new Random().nextInt(simplePredicates.size());
        int secondIndex = -1;
        while (secondIndex == -1 || secondIndex == firstIndex) {
            secondIndex = new Random().nextInt(simplePredicates.size());
        }
        return Arrays.asList(simplePredicates.get(firstIndex), simplePredicates.get(secondIndex));
    }

    private static CompoundPredicate.BooleanOperator getRandomCompoundPredicateAndOrOperator(int counter) {
        return counter % 2 == 0 ? CompoundPredicate.BooleanOperator.AND : CompoundPredicate.BooleanOperator.OR;
    }
}
