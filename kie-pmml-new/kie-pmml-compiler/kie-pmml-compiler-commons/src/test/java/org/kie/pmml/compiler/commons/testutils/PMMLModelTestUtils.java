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

import java.util.List;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;

/**
 * Helper methods related to <b>PMML</b> original model
 */
public class PMMLModelTestUtils {

    public static DataDictionary getDataDictionary(List<DataField> dataFields) {
        DataDictionary toReturn = new DataDictionary();
        toReturn.addDataFields(dataFields.toArray(new DataField[0]));
        return toReturn;
    }

    public static MiningSchema getMiningSchema(List<MiningField> miningFields) {
        MiningSchema toReturn = new MiningSchema();
        toReturn.addMiningFields(miningFields.toArray(new MiningField[0]));
        return toReturn;
    }

    public static RegressionModel getRegressionModel(String modelName, MiningFunction miningFunction, MiningSchema miningSchema, List<RegressionTable> regressionTables) {
        RegressionModel toReturn = new RegressionModel();
        toReturn.setModelName(modelName);
        toReturn.setMiningFunction(miningFunction);
        toReturn.setMiningSchema(miningSchema);
        toReturn.addRegressionTables(regressionTables.toArray(new RegressionTable[0]));
        return toReturn;
    }

    public static RegressionTable getRegressionTable(List<CategoricalPredictor> categoricalPredictors, List<NumericPredictor> numericPredictors, List<PredictorTerm> predictorTerms, double intercept, Object targetCategory) {
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

    public static MiningField getMiningField(String fieldName, MiningField.UsageType usageType) {
        MiningField toReturn = new MiningField();
        toReturn.setName(getFieldName(fieldName));
        toReturn.setUsageType(usageType);
        return toReturn;
    }

    public static FieldName getFieldName(String fieldName) {
        return FieldName.create(fieldName);
    }

    public static FieldRef getFieldRef(String fieldName) {
        return new FieldRef(getFieldName(fieldName));
    }
}
