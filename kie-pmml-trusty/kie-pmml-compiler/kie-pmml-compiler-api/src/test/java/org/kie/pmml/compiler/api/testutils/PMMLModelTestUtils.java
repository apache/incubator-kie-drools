/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.api.testutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompareFunction;
import org.dmg.pmml.ComparisonMeasure;
import org.dmg.pmml.ComplexScoreDistribution;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.DiscretizeBin;
import org.dmg.pmml.Euclidean;
import org.dmg.pmml.Field;
import org.dmg.pmml.FieldColumnPair;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.InlineTable;
import org.dmg.pmml.Interval;
import org.dmg.pmml.InvalidValueTreatmentMethod;
import org.dmg.pmml.LinearNorm;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.Measure;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.MissingValueTreatmentMethod;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutlierTreatmentMethod;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.Row;
import org.dmg.pmml.ScoreDistribution;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.TableLocator;
import org.dmg.pmml.Target;
import org.dmg.pmml.TargetValue;
import org.dmg.pmml.TextIndex;
import org.dmg.pmml.TextIndexNormalization;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.Value;
import org.dmg.pmml.clustering.Cluster;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
import org.dmg.pmml.clustering.Comparisons;
import org.dmg.pmml.clustering.MissingValueWeights;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.dmg.pmml.mining.Segmentation;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.jpmml.model.cells.InputCell;
import org.jpmml.model.cells.OutputCell;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.Named;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.compiler.api.mocks.TestModel;

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

    public static PMML getPMMLWithMiningRandomTestModel() {
        PMML toReturn = new PMML();
        DataDictionary dataDictionary = getRandomDataDictionary();
        toReturn.setDataDictionary(dataDictionary);
        toReturn.addModels(getRandomMiningModel(dataDictionary));
        return toReturn;
    }

    public static DataDictionary getDataDictionary(List<DataField> dataFields) {
        DataDictionary toReturn = new DataDictionary();
        toReturn.addDataFields(dataFields.toArray(new DataField[0]));
        return toReturn;
    }

    public static TransformationDictionary getTransformationDictionary() {
        return new TransformationDictionary();
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

    public static TransformationDictionary getRandomTransformationDictionary() {
        final TransformationDictionary toReturn = new TransformationDictionary();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.addDerivedFields(getDerivedField("DerivedField-" + i));
            toReturn.addDefineFunctions(getDefineFunction("DefineFunction-" + i));
        });
        return toReturn;
    }

    public static LocalTransformations getRandomLocalTransformations() {
        final LocalTransformations toReturn = new LocalTransformations();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.addDerivedFields(getDerivedField("DerivedField-" + i));
        });
        return toReturn;
    }

    public static MiningSchema getRandomMiningSchema() {
        MiningSchema toReturn = new MiningSchema();
        IntStream.range(0, new Random().nextInt(3) + 2)
                .forEach(i -> toReturn.addMiningFields(getRandomMiningField()));
        return toReturn;
    }

    public static Output getRandomOutput() {
        Output toReturn = new Output();
        IntStream.range(0, new Random().nextInt(3)+ 2)
                .forEach(i -> toReturn.addOutputFields(getRandomOutputField()));
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
        outputField.setName("OUTPUT_" +lastDataField.getName());
        outputField.setDataType(lastDataField.getDataType());
        outputField.setOpType(getRandomOpType());
        toReturn.setModelName(RandomStringUtils.random(6, true, false));
        toReturn.setMiningSchema(miningSchema);
        toReturn.setOutput(output);
        return toReturn;
    }

    public static MiningModel getRandomMiningModel(DataDictionary dataDictionary) {
        MiningModel toReturn = new MiningModel();
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
        outputField.setName("OUTPUT_" +lastDataField.getName());
        outputField.setDataType(lastDataField.getDataType());
        outputField.setOpType(getRandomOpType());
        toReturn.setModelName(RandomStringUtils.random(6, true, false));
        toReturn.setMiningSchema(miningSchema);
        toReturn.setOutput(output);
        TestModel testModel = getRandomTestModel(dataDictionary);
        Segment segment = new Segment();
        segment.setModel(testModel);
        Segmentation segmentation = new Segmentation();
        segmentation.addSegments(segment);
        toReturn.setSegmentation(segmentation);
        return toReturn;
    }

    public static DiscretizeBin getRandomDiscretizeBin() {
        Interval interval = getRandomInterval();
        DiscretizeBin toReturn = new DiscretizeBin();
        toReturn.setInterval(interval);
        toReturn.setBinValue(RandomStringUtils.random(6, true, false));
        return toReturn;
    }

    public static Interval getRandomInterval() {
        Random random = new Random();
        Number leftMargin = random.nextInt(100) / 10;
        Number rightMargin = leftMargin.doubleValue() + random.nextInt(50) / 10;
        Interval toReturn = new Interval();
        toReturn.setLeftMargin(leftMargin);
        toReturn.setRightMargin(rightMargin);
        toReturn.setClosure(getRandomClosure());
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
        toReturn.setField(name);
        toReturn.setValue(value);
        toReturn.setCoefficient(coefficient);
        return toReturn;
    }

    public static NumericPredictor getNumericPredictor(String name, int exponent, double coefficient) {
        NumericPredictor toReturn = new NumericPredictor();
        toReturn.setField(name);
        toReturn.setExponent(exponent);
        toReturn.setCoefficient(coefficient);
        return toReturn;
    }

    public static PredictorTerm getPredictorTerm(String name, double coefficient, List<String> fieldRefNames) {
        PredictorTerm toReturn = new PredictorTerm();
        toReturn.setName(name);
        toReturn.setCoefficient(coefficient);
        toReturn.addFieldRefs(fieldRefNames.stream().map(PMMLModelTestUtils::getFieldRef).toArray(FieldRef[]::new));
        return toReturn;
    }

    public static ClusteringModel getClusteringModel(String modelName, MiningFunction miningFunction,
                                                     MiningSchema miningSchema,
                                                     List<ClusteringField> clusteringFields,
                                                     List<Cluster> clusters) {
        ClusteringModel toReturn = new ClusteringModel();
        toReturn.setModelName(modelName);
        toReturn.setMiningFunction(miningFunction);
        toReturn.setMiningSchema(miningSchema);
        toReturn.addClusteringFields(clusteringFields.toArray(new ClusteringField[0]));
        toReturn.addClusters(clusters.toArray(new Cluster[0]));
        toReturn.setModelClass(getRandomModelClass());
        toReturn.setComparisonMeasure(getRandomComparisonMeasure());
        toReturn.setMissingValueWeights(getRandomMissingValueWeights());
        return toReturn;
    }


    public static DataField getDataField(String fieldName, OpType opType) {
        DataField toReturn = new DataField();
        toReturn.setName(fieldName);
        toReturn.setOpType(opType);
        return toReturn;
    }

    public static DataField getDataField(String fieldName, OpType opType, DataType dataType) {
        DataField toReturn = getDataField(fieldName, opType);
        toReturn.setDataType(dataType);
        return toReturn;
    }

    public static MiningField getMiningField(String fieldName, MiningField.UsageType usageType) {
        MiningField toReturn = getRandomMiningField();
        toReturn.setName(fieldName);
        toReturn.setUsageType(usageType);
        return toReturn;
    }

    public static Target getTarget(String fieldTarget, OpType opType) {
        Target toReturn = new Target();
        toReturn.setField(fieldTarget);
        toReturn.setOpType(opType);
        return toReturn;
    }

    public static DefineFunction getDefineFunction(String functionName) {
        DefineFunction toReturn = new DefineFunction();
        toReturn.setName(functionName);
        toReturn.setDataType(getRandomDataType());
        toReturn.setOpType(getRandomOpType());
        Constant expression = new Constant(5);
        expression.setDataType(DataType.INTEGER);
        toReturn.setExpression(expression);
        IntStream.range(0, 3).forEach(i -> toReturn.addParameterFields(getParameterField("ParameterField-" + i)));
        return toReturn;
    }

    public static DerivedField getDerivedField(String fieldName) {
        DerivedField toReturn = new DerivedField();
        toReturn.setName(fieldName);
        toReturn.setDataType(getRandomDataType());
        toReturn.setOpType(getRandomOpType());
        Constant expression = new Constant(5);
        expression.setDataType(DataType.INTEGER);
        toReturn.setExpression(expression);
        toReturn.setDisplayName("Display-" + fieldName);
        return toReturn;
    }

    public static ComparisonMeasure getRandomComparisonMeasure() {
        ComparisonMeasure toReturn = new ComparisonMeasure();
        toReturn.setCompareFunction(getRandomCompareFunction());
        toReturn.setKind(getRandomKind());
        toReturn.setMeasure(getRandomMeasure());
        return toReturn;
    }

    public static Measure getRandomMeasure() {
        return new Euclidean();
    }

    public static DataField getRandomDataField() {
        DataField toReturn = new DataField();
        toReturn.setName(RandomStringUtils.random(6, true, false));
        toReturn.setDataType(getRandomDataType());
        toReturn.setOpType(getRandomOpType());
        IntStream.range(0, 3).forEach(i -> {
            toReturn.addValues(getRandomValue(toReturn.getDataType()));
            toReturn.addIntervals(getRandomInterval());
        });
        return toReturn;
    }

    public static MiningField getRandomMiningField(DataField dataField) {
        Random random = new Random();
        MiningField toReturn = getRandomMiningField();
        DataType dataType = dataField.getDataType();
        toReturn.setName(dataField.getName());
        toReturn.setInvalidValueReplacement(getRandomObject(dataType).toString());
        toReturn.setMissingValueReplacement(getRandomObject(dataType).toString());
        toReturn.setImportance(random.nextInt(10));
        toReturn.setLowValue(random.nextInt(10));
        toReturn.setHighValue(toReturn.getLowValue().intValue() + random.nextInt(30));
        toReturn.setUsageType(getRandomUsageType());
        toReturn.setOpType(getRandomOpType());
        return toReturn;
    }

    public static MiningField getRandomMiningField() {
        Random random = new Random();
        MiningField toReturn = new MiningField(RandomStringUtils.random(6, true, false));
        toReturn.setInvalidValueTreatment(getRandomInvalidValueTreatmentMethod());
        toReturn.setMissingValueTreatment(getRandomMissingValueTreatmentMethod());
        toReturn.setOutlierTreatment(getRandomOutlierTreatmentMethod());
        DataType dataType = getRandomDataType();
        toReturn.setInvalidValueReplacement(getRandomObject(dataType).toString());
        toReturn.setMissingValueReplacement(getRandomObject(dataType).toString());
        toReturn.setImportance(random.nextInt(10));
        toReturn.setLowValue(random.nextInt(10));
        toReturn.setHighValue(toReturn.getLowValue().intValue() + random.nextInt(30));
        toReturn.setUsageType(getRandomUsageType());
        toReturn.setOpType(getRandomOpType());
        return toReturn;
    }

    public static OutputField getRandomOutputField(DataField dataField) {
        OutputField toReturn = getRandomOutputField();
        toReturn.setName(dataField.getName());
        toReturn.setDataType(dataField.getDataType());
        return toReturn;
    }

    public static OutputField getRandomOutputField() {
         String fieldName =RandomStringUtils.random(6, true, false);
        OutputField toReturn = new OutputField();
        toReturn.setName(fieldName);
        toReturn.setOpType(getRandomOpType());
        toReturn.setDataType(getRandomDataType());
        toReturn.setValue(getRandomValue(toReturn.getDataType()));
        fieldName =RandomStringUtils.random(6, true, false);
        toReturn.setTargetField(fieldName);
        toReturn.setResultFeature(getRandomResultFeature());
        toReturn.setExpression(getRandomConstant());
        return toReturn;
    }

    public static Target getRandomTarget() {
        Random random = new Random();
        Target toReturn = new Target();
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setOpType(getRandomOpType());
        toReturn.setMax(random.nextInt(234));
        toReturn.setMin(random.nextInt(23));
        toReturn.setCastInteger(getRandomCastInteger());
        toReturn.setRescaleConstant(random.nextInt(234));
        toReturn.setRescaleFactor(random.nextInt(234));
        IntStream.range(0, 3)
                .forEach(i -> toReturn.addTargetValues(getRandomTargetValue()));
        return toReturn;
    }

    public static TargetValue getRandomTargetValue() {
        Random random = new Random();
        TargetValue toReturn = new TargetValue();
        toReturn.setValue(random.nextDouble());
        toReturn.setDisplayValue(RandomStringUtils.random(6, true, false));
        toReturn.setDefaultValue(random.nextFloat());
        toReturn.setPriorProbability((double) random.nextInt(100) / 13);
        return toReturn;
    }

    public static FieldColumnPair getRandomFieldColumnPair() {
        FieldColumnPair toReturn = new FieldColumnPair();
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setColumn(RandomStringUtils.random(6, true, false));
        return toReturn;
    }

    public static Cluster getRandomCluster() {
        Random random = new Random();
        Cluster toReturn = new Cluster();
        toReturn.setName(RandomStringUtils.random(6, true, false));
        toReturn.setId(String.valueOf(random.nextInt()));
        return toReturn;
    }

    public static ClusteringField getRandomClusteringField() {
        Random random = new Random();
        ClusteringField toReturn = new ClusteringField();
        toReturn.setCenterField(getRandomClusteringFieldCenterField());
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setCompareFunction(getRandomCompareFunction());
        toReturn.setFieldWeight(random.nextDouble());
        toReturn.setComparisons(getRandomComparisons());
        toReturn.setSimilarityScale(random.nextDouble());
        return toReturn;
    }

    public static Comparisons getRandomComparisons() {
        Comparisons toReturn = new Comparisons();
        return toReturn;
    }

    // Expressions

    public static Apply getRandomApply() {
        Apply toReturn = new Apply();
        toReturn.setFunction(RandomStringUtils.random(6, true, false));
        toReturn.setDefaultValue(RandomStringUtils.random(6, true, false));
        toReturn.setMapMissingTo(RandomStringUtils.random(6, true, false));
        toReturn.setInvalidValueTreatment(getRandomInvalidValueTreatmentMethod());
        toReturn.addExpressions(getRandomConstant());
        toReturn.addExpressions(getRandomDiscretize());
        toReturn.addExpressions(getRandomFieldRef());
        return toReturn;
    }

    public static Constant getRandomConstant() {
        Constant toReturn = new Constant();
        toReturn.setDataType(getRandomDataType());
        toReturn.setValue(getRandomObject(toReturn.getDataType()));
        return toReturn;
    }

    public static Discretize getRandomDiscretize() {
        Discretize toReturn = new Discretize();
        toReturn.setDataType(getRandomDataType());
        toReturn.setDefaultValue(RandomStringUtils.random(6, true, false));
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setMapMissingTo(RandomStringUtils.random(6, true, false));
        IntStream.range(0, 3).forEach(i -> toReturn.addDiscretizeBins(getRandomDiscretizeBin()));
        return toReturn;
    }

    public static FieldRef getRandomFieldRef() {
        FieldRef toReturn = new FieldRef();
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setMapMissingTo(RandomStringUtils.random(6, true, false));
        return toReturn;
    }

    public static MapValues getRandomMapValues() {
        MapValues toReturn = new MapValues();
        toReturn.setDataType(getRandomDataType());
        toReturn.setDefaultValue(getRandomObject(toReturn.getDataType()));
        toReturn.setMapMissingTo(RandomStringUtils.random(6, true, false));
        toReturn.setOutputColumn(RandomStringUtils.random(6, true, false));
        toReturn.setInlineTable(getRandomInlineTableWithCells());
        toReturn.setTableLocator(getRandomTableLocator());
        return toReturn;
    }

    public static MissingValueWeights getRandomMissingValueWeights() {
        MissingValueWeights toReturn = new MissingValueWeights();
        return toReturn;
    }

    public static NormContinuous getRandomNormContinuous() {
        Random random = new Random();
        double mapMissingTo = random.nextInt(100) / 10;
        NormContinuous toReturn = new NormContinuous();
        IntStream.range(0, 3).forEach(i -> toReturn.addLinearNorms(getRandomLinearNorm()));
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setOutliers(getRandomOutlierTreatmentMethod());
        toReturn.setMapMissingTo(mapMissingTo);
        return toReturn;
    }

    public static NormDiscrete getRandomNormDiscrete() {
        NormDiscrete toReturn = new NormDiscrete();
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setValue(getRandomObject(DataType.INTEGER));
        toReturn.setMapMissingTo((Number) getRandomObject(DataType.INTEGER));
        toReturn.setMethod(getRandomMethod());
        return toReturn;
    }

    public static TextIndex getRandomTextIndex() {
        Random random = new Random();
        TextIndex toReturn = new TextIndex();
        toReturn.setField(RandomStringUtils.random(6, true, false));
        toReturn.setExpression(getRandomFieldRef());
        toReturn.setLocalTermWeights(getRandomLocalTermWeights());
        toReturn.setWordSeparatorCharacterRE(RandomStringUtils.random(1, true, false));
        toReturn.setTokenize(true);
        toReturn.setCaseSensitive(false);
        toReturn.setMaxLevenshteinDistance(random.nextInt(10));
        toReturn.setTextField(RandomStringUtils.random(6, true, false));
        IntStream.range(0, 3).forEach(i -> toReturn.addTextIndexNormalizations(getRandomTextIndexNormalization()));
        return toReturn;
    }

    public static LinearNorm getRandomLinearNorm() {
        Random random = new Random();
        double orig = random.nextInt(100) / 10;
        double norm = random.nextInt(100) / 10;
        return new LinearNorm(orig, norm);
    }

    public static ParameterField getParameterField(String fieldName) {
        ParameterField toReturn = new ParameterField(fieldName);
        toReturn.setDataType(getRandomDataType());
        toReturn.setOpType(getRandomOpType());
        toReturn.setDisplayName("Display-" + fieldName);
        return toReturn;
    }

    public static ParameterField getParameterField(String fieldName, DataType dataType) {
        ParameterField toReturn = new ParameterField(fieldName);
        toReturn.setDataType(dataType);
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
        return getEnumList(DATA_TYPE.values(), DataType.class);
    }

    public static List<ResultFeature> getResultFeature() {
        return getEnumList(RESULT_FEATURE.values(), ResultFeature.class);
    }

    public static SimplePredicate getSimplePredicate(final String predicateName,
                                                     final Object value,
                                                     final SimplePredicate.Operator operator) {
         String fieldName =predicateName;
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
         String fieldName =predicateName;
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

    public static FieldRef getFieldRef(final String fieldName) {
        return new FieldRef(fieldName);
    }

    public static Object getRandomObject(DataType dataType) {
        switch (dataType) {
            case INTEGER:
            case DATE_DAYS_SINCE_0:
            case DATE_DAYS_SINCE_1960:
            case DATE_DAYS_SINCE_1970:
            case DATE_DAYS_SINCE_1980:
            case DATE_DAYS_SINCE_1990:
            case DATE_DAYS_SINCE_2000:
            case DATE_DAYS_SINCE_2010:
            case DATE_DAYS_SINCE_2020:
                return new Random().nextInt(40);
            case DOUBLE:
                return new Random().nextDouble();
            case BOOLEAN:
                return new Random().nextBoolean();
            case STRING:
                return RandomStringUtils.random(6, true, false);
            case FLOAT:
                return new Random().nextFloat();
            case DATE:
            case TIME:
            case DATE_TIME:
                return new Date();
            case TIME_SECONDS:
            case DATE_TIME_SECONDS_SINCE_0:
            case DATE_TIME_SECONDS_SINCE_1960:
            case DATE_TIME_SECONDS_SINCE_1970:
            case DATE_TIME_SECONDS_SINCE_1980:
            case DATE_TIME_SECONDS_SINCE_1990:
            case DATE_TIME_SECONDS_SINCE_2000:
            case DATE_TIME_SECONDS_SINCE_2010:
            case DATE_TIME_SECONDS_SINCE_2020:
                return new Random().nextLong();
            default:
                return new Random().nextInt();
        }
    }

    public static Value getRandomValue(DataType dataType) {
        Value toReturn = getRandomValue();
        toReturn.setValue(getRandomObject(dataType));
        return toReturn;
    }

    public static Value getRandomValue() {
        Value toReturn = new Value();
        toReturn.setValue(getRandomObject(getRandomDataType()));
        toReturn.setDisplayValue(RandomStringUtils.random(6, true, false));
        toReturn.setProperty(getRandomProperty());
        return toReturn;
    }

    public static TextIndexNormalization getRandomTextIndexNormalization() {
        Random random = new Random();
        TextIndexNormalization toReturn = new TextIndexNormalization();
        toReturn.setCaseSensitive(false);
        toReturn.setInlineTable(getRandomInlineTableWithCells());
        toReturn.setWordSeparatorCharacterRE(RandomStringUtils.random(1, true, false));
        toReturn.setTokenize(true);
        toReturn.setCaseSensitive(false);
        toReturn.setMaxLevenshteinDistance(random.nextInt(10));
        toReturn.setOutField(RandomStringUtils.random(1, true, false));
        toReturn.setTableLocator(getRandomTableLocator());
        toReturn.setInField(RandomStringUtils.random(1, true, false));
        toReturn.setRecursive(false);
        toReturn.setRegexField(RandomStringUtils.random(1, true, false));
        return toReturn;
    }

    public static TableLocator getRandomTableLocator() {
        return new TableLocator();
    }

    public static InlineTable getRandomInlineTableWithCells() {
        InlineTable toReturn = new InlineTable();
        IntStream.range(0, 3).forEach(i -> toReturn.addRows(getRandomRowWithCells()));
        return toReturn;
    }

    public static Row getRandomRow() {
        Row toReturn = new Row();
        toReturn.addContent(RandomStringUtils.random(6, true, false));
        return toReturn;
    }

    public static Row getRandomRowWithCells() {
        Row toReturn = new Row();
        toReturn.addContent(new InputCell(RandomStringUtils.random(6, true, false)));
        toReturn.addContent(new OutputCell(RandomStringUtils.random(6, true, false)));
        return toReturn;
    }

    // Enums

    public static Array.Type getArrayType(DataType dataType) {
        switch (dataType) {
            case INTEGER:
                return Array.Type.INT;
            case STRING:
                return Array.Type.STRING;
            default:
                return Array.Type.REAL;
        }
    }

    public static Array.Type getRandomArrayType() {
        return getRandomEnum(Array.Type.values());
    }

    public static CompoundPredicate.BooleanOperator getRandomCompoundPredicateBooleanOperator() {
        return getRandomEnum(CompoundPredicate.BooleanOperator.values());
    }

    public static Target.CastInteger getRandomCastInteger() {
        return getRandomEnum(Target.CastInteger.values());
    }

    public static ClusteringField.CenterField getRandomClusteringFieldCenterField() {
        return getRandomEnum(ClusteringField.CenterField.values());
    }

    public static CompareFunction getRandomCompareFunction() {
        return getRandomEnum(CompareFunction.values());
    }

    public static Interval.Closure getRandomClosure() {
        return getRandomEnum(Interval.Closure.values());
    }

    public static DataType getRandomDataType() {
        List<DataType> dataTypes = getDataTypes();
        return getRandomEnum(dataTypes.toArray(new DataType[0]));
    }

    public static InvalidValueTreatmentMethod getRandomInvalidValueTreatmentMethod() {
        return getRandomEnum(InvalidValueTreatmentMethod.values());
    }

    public static  ComparisonMeasure.Kind getRandomKind(){
        return getRandomEnum(ComparisonMeasure.Kind.values());
    }

    public static TextIndex.LocalTermWeights getRandomLocalTermWeights() {
        return getRandomEnum(TextIndex.LocalTermWeights.values());
    }

    public static NormDiscrete.Method getRandomMethod() {
        return getRandomEnum(NormDiscrete.Method.values());
    }

    public static MissingValueTreatmentMethod getRandomMissingValueTreatmentMethod() {
        return getRandomEnum(MissingValueTreatmentMethod.values());
    }

    public static ClusteringModel.ModelClass getRandomModelClass() {
        return getRandomEnum(ClusteringModel.ModelClass.values());
    }

    public static OpType getRandomOpType() {
        return getRandomEnum(OpType.values());
    }

    public static OutlierTreatmentMethod getRandomOutlierTreatmentMethod() {
        return getRandomEnum(OutlierTreatmentMethod.values());
    }

    public static Value.Property getRandomProperty() {
        return getRandomEnum(Value.Property.values());
    }

    public static ResultFeature getRandomResultFeature() {
        List<ResultFeature> resultFeatures = getResultFeature();
        return getRandomEnum(resultFeatures.toArray(new ResultFeature[0]));
    }

    public static SimplePredicate.Operator getRandomSimplePredicateOperator() {
        return getRandomEnum(SimplePredicate.Operator.values());
    }

    public static SimpleSetPredicate.BooleanOperator getRandomSimpleSetPredicateOperator() {
        return getRandomEnum(SimpleSetPredicate.BooleanOperator.values());
    }

    public static MiningField.UsageType getRandomUsageType() {
        return getRandomEnum(MiningField.UsageType.values());
    }

    //

    public static List<ScoreDistribution> getRandomPMMLScoreDistributions(boolean withProbability) {
        List<Double> probabilities = withProbability ? Arrays.asList(0.1, 0.3, 0.6) : Arrays.asList(null, null, null);
        return IntStream.range(0, 3)
                .mapToObj(i -> getRandomPMMLScoreDistribution(probabilities.get(i)))
                .collect(Collectors.toList());
    }

    public static ScoreDistribution getRandomPMMLScoreDistribution(Double probability) {
        Random random = new Random();
        ScoreDistribution toReturn = new ComplexScoreDistribution();
        toReturn.setValue(RandomStringUtils.random(6, true, false));
        toReturn.setRecordCount(random.nextInt(100));
        toReturn.setConfidence((double) random.nextInt(1) / 100);
        toReturn.setProbability(probability);
        return toReturn;
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

    public static CompoundPredicate getRandomCompoundPredicate(List<Field<?>> fields) {
        CompoundPredicate toReturn = new CompoundPredicate();
        toReturn.setBooleanOperator(getRandomCompoundPredicateAndOrOperator(new Random().nextInt(10)));
        IntStream.range(0, fields.size() - 1).forEach(i -> {
            toReturn.addPredicates(getRandomSimplePredicate((DataField) fields.get(i)));
        });
        toReturn.addPredicates(getRandomSimpleSetPredicate((DataField) fields.get(fields.size() - 1)));
        return toReturn;
    }

    public static CompoundPredicate getRandomCompoundPredicate() {
        CompoundPredicate toReturn = new CompoundPredicate();
        toReturn.setBooleanOperator(getRandomCompoundPredicateAndOrOperator(new Random().nextInt(10)));
        IntStream.range(0, 3).forEach(i -> {
            toReturn.addPredicates(getRandomSimplePredicate());
        });
        toReturn.addPredicates(getRandomSimpleSetPredicate());
        return toReturn;
    }

    public static SimplePredicate getRandomSimplePredicate(DataField dataField) {
        SimplePredicate toReturn = getRandomSimplePredicate();
        toReturn.setField(dataField.getName());
        toReturn.setValue(getRandomObject(dataField.getDataType()));
        return toReturn;
    }

    public static SimplePredicate getRandomSimplePredicate() {
         String fieldName =RandomStringUtils.random(6, true, false);
        SimplePredicate toReturn = new SimplePredicate();
        toReturn.setField(fieldName);
        toReturn.setOperator(getRandomSimplePredicateOperator());
        toReturn.setValue(getRandomObject(getRandomDataType()));
        return toReturn;
    }

    public static SimpleSetPredicate getRandomSimpleSetPredicate(DataField dataField) {
        SimpleSetPredicate toReturn = getRandomSimpleSetPredicate();
        toReturn.setField(dataField.getName());
        toReturn.setBooleanOperator(getRandomSimpleSetPredicateOperator());
        Array.Type arrayType = getArrayType(dataField.getDataType());
        List<String> values = getStringObjects(arrayType, 3);
        Array array = getArray(arrayType, values);
        toReturn.setArray(array);
        return toReturn;
    }

    public static SimpleSetPredicate getRandomSimpleSetPredicate() {
         String fieldName =RandomStringUtils.random(6, true, false);
        SimpleSetPredicate toReturn = new SimpleSetPredicate();
        toReturn.setField(fieldName);
        toReturn.setBooleanOperator(getRandomSimpleSetPredicateOperator());
        Array.Type arrayType = getRandomArrayType();
        List<String> values = getStringObjects(arrayType, 3);
        Array array = getArray(arrayType, values);
        toReturn.setArray(array);
        return toReturn;
    }

    public static <T extends Named, E extends Enum<E>> List<E> getEnumList(T[] source, Class<E> enumClass) {
        return Arrays.stream(source).map(namedEnum -> Enum.valueOf(enumClass, namedEnum.toString())).collect(Collectors.toList());
    }

    public static <T extends Enum<?>> T getRandomEnum(T[] source) {
        Random random = new Random();
        return source[random.nextInt(source.length)];
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
