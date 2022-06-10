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

package org.kie.pmml.compiler.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.Array;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.Row;
import org.dmg.pmml.Target;
import org.dmg.pmml.Targets;
import org.dmg.pmml.regression.RegressionModel;
import org.jpmml.model.inlinetable.InputCell;
import org.jpmml.model.inlinetable.OutputCell;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getArray;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataTypes;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getParameterFields;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataType;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomMiningField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomOutputField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomRowWithCells;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTarget;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getTarget;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getPrefixedName;

public class ModelUtilsTest {

    private static Map<String, String> expectedBoxedClassName;

    static {
        expectedBoxedClassName = new HashMap<>();
        expectedBoxedClassName.put("string", String.class.getName());
        expectedBoxedClassName.put("integer", Integer.class.getName());
        expectedBoxedClassName.put("float", Float.class.getName());
        expectedBoxedClassName.put("double", Double.class.getName());
        expectedBoxedClassName.put("boolean", Boolean.class.getName());
        expectedBoxedClassName.put("date", Date.class.getName());
        expectedBoxedClassName.put("time", Date.class.getName());
        expectedBoxedClassName.put("dateTime", Date.class.getName());
        expectedBoxedClassName.put("dateDaysSince[0]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1960]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1970]", Long.class.getName());
        expectedBoxedClassName.put("dateDaysSince[1980]", Long.class.getName());
        expectedBoxedClassName.put("timeSeconds", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[0]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1960]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1970]", Long.class.getName());
        expectedBoxedClassName.put("dateTimeSecondsSince[1980]", Long.class.getName());
    }

    @Test
    public void getTargetFieldName() {
        final String fieldName = "fieldName";
        MiningField.UsageType usageType = MiningField.UsageType.ACTIVE;
        MiningField miningField = getMiningField(fieldName, usageType);
        final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
        final DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        final Model model = new RegressionModel();
        model.setMiningSchema(miningSchema);
        final List<Field<?>> fields = getFieldsFromDataDictionary(dataDictionary);
        Optional<String> retrieved = ModelUtils.getTargetFieldName(fields, model);
        assertThat(retrieved.isPresent()).isFalse();
        usageType = MiningField.UsageType.PREDICTED;
        miningField = getMiningField(fieldName, usageType);
        miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        model.setMiningSchema(miningSchema);
        retrieved = ModelUtils.getTargetFieldName(fields, model);
        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved.get()).isEqualTo(fieldName);
    }

    @Test
    public void getTargetFieldTypeWithTargetField() {
        final String fieldName = "fieldName";
        MiningField.UsageType usageType = MiningField.UsageType.PREDICTED;
        MiningField miningField = getMiningField(fieldName, usageType);
        final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
        final DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        final MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        final Model model = new RegressionModel();
        model.setMiningSchema(miningSchema);
        DATA_TYPE retrieved = ModelUtils.getTargetFieldType(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(DATA_TYPE.STRING);
    }

    @Test(expected = Exception.class)
    public void getTargetFieldTypeWithoutTargetField() {
        final String fieldName = "fieldName";
        MiningField.UsageType usageType = MiningField.UsageType.ACTIVE;
        MiningField miningField = getMiningField(fieldName, usageType);
        final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
        final DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        final MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        final Model model = new RegressionModel();
        model.setMiningSchema(miningSchema);
        ModelUtils.getTargetFieldType(getFieldsFromDataDictionary(dataDictionary), model);
    }

    @Test
    public void getTargetFieldsWithoutTargetFields() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final String fieldName = "fieldName-" + i;
            final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(fieldName, MiningField.UsageType.ACTIVE);
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        List<KiePMMLNameOpType> retrieved = ModelUtils.getTargetFields(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
    }

    @Test
    public void getTargetFieldsWithTargetFieldsWithoutOptType() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final String fieldName = "fieldName-" + i;
            final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(fieldName, MiningField.UsageType.PREDICTED);
            miningField.setOpType(null);
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        List<KiePMMLNameOpType> retrieved = ModelUtils.getTargetFields(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(miningSchema.getMiningFields());
        retrieved.forEach(kiePMMLNameOpType -> {
            assertThat(miningSchema.getMiningFields()
                               .stream()
                               .anyMatch(fld -> kiePMMLNameOpType.getName().equals(fld.getName().getValue()))).isTrue();
            Optional<DataField> optionalDataField = dataDictionary.getDataFields()
                    .stream()
                    .filter(fld -> kiePMMLNameOpType.getName().equals(fld.getName().getValue()))
                    .findFirst();
            assertThat(optionalDataField).isPresent();
            DataField dataField = optionalDataField.get();
            OP_TYPE expected = OP_TYPE.byName(dataField.getOpType().value());
            assertThat(kiePMMLNameOpType.getOpType()).isEqualTo(expected);
        });
    }

    @Test
    public void getTargetFieldsWithTargetFieldsWithOptType() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final String fieldName = "fieldName-" + i;
            final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(fieldName, MiningField.UsageType.PREDICTED);
            miningField.setOpType(OpType.CONTINUOUS);
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        List<KiePMMLNameOpType> retrieved = ModelUtils.getTargetFields(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(miningSchema.getMiningFields());
        retrieved.forEach(kiePMMLNameOpType -> {
            Optional<MiningField> optionalMiningField = miningSchema.getMiningFields()
                    .stream()
                    .filter(fld -> kiePMMLNameOpType.getName().equals(fld.getName().getValue()))
                    .findFirst();
            assertThat(optionalMiningField).isPresent();
            MiningField miningField = optionalMiningField.get();
            OP_TYPE expected = OP_TYPE.byName(miningField.getOpType().value());
            assertThat(kiePMMLNameOpType.getOpType()).isEqualTo(expected);
        });
    }

    @Test
    public void getTargetFieldsWithTargetFieldsWithTargetsWithoutOptType() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        final Targets targets = new Targets();
        IntStream.range(0, 3).forEach(i -> {
            final String fieldName = "fieldName-" + i;
            final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(fieldName, MiningField.UsageType.PREDICTED);
            miningField.setOpType(OpType.CONTINUOUS);
            miningSchema.addMiningFields(miningField);
            final Target targetField = getTarget(fieldName, null);
            targets.addTargets(targetField);
        });
        model.setMiningSchema(miningSchema);
        model.setTargets(targets);
        List<KiePMMLNameOpType> retrieved = ModelUtils.getTargetFields(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(miningSchema.getMiningFields());
        retrieved.forEach(kiePMMLNameOpType -> {
            Optional<MiningField> optionalMiningField = miningSchema.getMiningFields()
                    .stream()
                    .filter(fld -> kiePMMLNameOpType.getName().equals(fld.getName().getValue()))
                    .findFirst();
            assertThat(optionalMiningField).isPresent();
            MiningField miningField = optionalMiningField.get();
            OP_TYPE expected = OP_TYPE.byName(miningField.getOpType().value());
            assertThat(kiePMMLNameOpType.getOpType()).isEqualTo(expected);
        });
    }

    @Test
    public void getTargetFieldsWithTargetFieldsWithTargetsWithOptType() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        final Targets targets = new Targets();
        IntStream.range(0, 3).forEach(i -> {
            final String fieldName = "fieldName-" + i;
            final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(fieldName, MiningField.UsageType.PREDICTED);
            miningField.setOpType(OpType.CONTINUOUS);
            miningSchema.addMiningFields(miningField);
            final Target targetField = getTarget(fieldName, OpType.CATEGORICAL);
            targets.addTargets(targetField);
        });
        model.setMiningSchema(miningSchema);
        model.setTargets(targets);
        List<KiePMMLNameOpType> retrieved = ModelUtils.getTargetFields(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(miningSchema.getMiningFields());
        retrieved.forEach(kiePMMLNameOpType -> {
            Optional<Target> optionalTarget = targets.getTargets()
                    .stream()
                    .filter(fld -> kiePMMLNameOpType.getName().equals(fld.getField().getValue()))
                    .findFirst();
            assertThat(optionalTarget).isPresent();
            Target target = optionalTarget.get();
            OP_TYPE expected = OP_TYPE.byName(target.getOpType().value());
            assertThat(kiePMMLNameOpType.getOpType()).isEqualTo(expected);
        });
    }

    @Test
    public void getTargetFieldsTypeMapWithTargetFieldsWithoutTargets() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(dataField.getName().getValue(),
                                                           MiningField.UsageType.PREDICTED);
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        Map<String, DATA_TYPE> retrieved = ModelUtils.getTargetFieldsTypeMap(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(miningSchema.getMiningFields());
        assertThat(retrieved).isInstanceOf(LinkedHashMap.class);
        final Iterator<Map.Entry<String, DATA_TYPE>> iterator = retrieved.entrySet().iterator();
        for (int i = 0; i < miningSchema.getMiningFields().size(); i++) {
            MiningField miningField = miningSchema.getMiningFields().get(i);
            DataField dataField = dataDictionary.getDataFields().stream()
                    .filter(df -> df.getName().equals(miningField.getName()))
                    .findFirst()
                    .get();
            DATA_TYPE expected = DATA_TYPE.byName(dataField.getDataType().value());
            final Map.Entry<String, DATA_TYPE> next = iterator.next();
            assertThat(next.getValue()).isEqualTo(expected);
        }
    }

    @Test
    public void getTargetFieldsTypeMapWithoutTargetFieldsWithoutTargets() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(dataField.getName().getValue(),
                                                           MiningField.UsageType.ACTIVE);
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        Map<String, DATA_TYPE> retrieved = ModelUtils.getTargetFieldsTypeMap(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
    }

    @Test
    public void getTargetFieldsWithoutTargetFieldsWithTargets() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        final Targets targets = new Targets();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getMiningField(dataField.getName().getValue(),
                                                           MiningField.UsageType.ACTIVE);
            miningSchema.addMiningFields(miningField);
            final Target targetField = getTarget(dataField.getName().getValue(), null);
            targets.addTargets(targetField);
        });
        model.setMiningSchema(miningSchema);
        model.setTargets(targets);
        Map<String, DATA_TYPE> retrieved = ModelUtils.getTargetFieldsTypeMap(getFieldsFromDataDictionary(dataDictionary), model);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
    }

    @Test
    public void getOpTypeByDataFields() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
        });
        model.setMiningSchema(miningSchema);
        dataDictionary.getDataFields().forEach(dataField -> {
                                                   OP_TYPE retrieved = ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                                                                                            dataField.getName().getValue());
                                                   assertThat(retrieved).isNotNull();
                                                   OP_TYPE expected = OP_TYPE.byName(dataField.getOpType().value());
                                                    assertThat(retrieved).isEqualTo(expected);
                                               });
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getOpTypeByDataFieldsNotFound() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        IntStream.range(0, 3).forEach(i -> {
            String fieldName = "field" +i;
            final DataField dataField = getRandomDataField();
            dataField.setName(FieldName.create(fieldName));
            dataDictionary.addDataFields(dataField);
        });
        ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                             "NOT_EXISTING");
    }

    @Test
    public void getOpTypeByMiningFields() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getRandomMiningField();
            miningField.setName(dataField.getName());
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        miningSchema.getMiningFields().forEach(miningField -> {
            OP_TYPE retrieved = ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                                                     miningField.getName().getValue());
            assertThat(retrieved).isNotNull();
            OP_TYPE expected = OP_TYPE.byName(miningField.getOpType().value());
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getOpTypeByMiningFieldsNotFound() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        IntStream.range(0, 3).forEach(i -> {
            String fieldName = "field" +i;
            final DataField dataField = getRandomDataField();
            dataField.setName(FieldName.create(fieldName));
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getRandomMiningField();
            miningField.setName(dataField.getName());
            miningSchema.addMiningFields(miningField);
        });
        model.setMiningSchema(miningSchema);
        ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                             "NOT_EXISTING");
    }

    @Test
    public void getOpTypeByTargets() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        final Targets targets = new Targets();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getRandomMiningField();
            miningField.setName(dataField.getName());
            miningSchema.addMiningFields(miningField);
            final Target targetField = getRandomTarget();
            targetField.setField(dataField.getName());
            targets.addTargets(targetField);
        });
        model.setMiningSchema(miningSchema);
        model.setTargets(targets);
        getFieldsFromDataDictionary(dataDictionary);
        targets.getTargets().forEach(target -> {
            OP_TYPE retrieved = ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                                                     target.getField().getValue());
            assertThat(retrieved).isNotNull();
            OP_TYPE expected = OP_TYPE.byName(target.getOpType().value());
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getOpTypeByTargetsNotFound() {
        final Model model = new RegressionModel();
        final DataDictionary dataDictionary = new DataDictionary();
        final MiningSchema miningSchema = new MiningSchema();
        final Targets targets = new Targets();
        IntStream.range(0, 3).forEach(i -> {
            String fieldName = "field" +i;
            final DataField dataField = getRandomDataField();
            dataField.setName(FieldName.create(fieldName));
            dataDictionary.addDataFields(dataField);
            final MiningField miningField = getRandomMiningField();
            miningField.setName(dataField.getName());
            miningSchema.addMiningFields(miningField);
            final Target targetField = getRandomTarget();
            targetField.setField(dataField.getName());
            targets.addTargets(targetField);
        });
        model.setMiningSchema(miningSchema);
        model.setTargets(targets);
        ModelUtils.getOpType(getFieldsFromDataDictionary(dataDictionary), model,
                             "NOT_EXISTING");
    }

    @Test
    public void getOpTypeFromFields() {
        Optional<OP_TYPE> opType = ModelUtils.getOpTypeFromFields(null, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        final DataDictionary dataDictionary = new DataDictionary();
        final List<Field<?>> fields = getFieldsFromDataDictionary(dataDictionary);
        opType = ModelUtils.getOpTypeFromFields(fields, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
        });
        fields.clear();
        fields.addAll(getFieldsFromDataDictionary(dataDictionary));
        dataDictionary.getDataFields().forEach(dataField -> {
            Optional<OP_TYPE> retrieved = ModelUtils.getOpTypeFromFields(fields, dataField.getName().getValue());
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).isPresent();
            OP_TYPE expected = OP_TYPE.byName(dataField.getOpType().value());
            assertThat(retrieved.get()).isEqualTo(expected);
        });
    }

    @Test
    public void getOpTypeFromMiningFields() {
        Optional<OP_TYPE> opType = ModelUtils.getOpTypeFromMiningFields(null, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        final MiningSchema miningSchema = new MiningSchema();
        opType = ModelUtils.getOpTypeFromMiningFields(miningSchema, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        IntStream.range(0, 3).forEach(i -> {
            final MiningField miningField = getRandomMiningField();
            miningSchema.addMiningFields(miningField);
        });
        miningSchema.getMiningFields().forEach(miningField -> {
            Optional<OP_TYPE> retrieved = ModelUtils.getOpTypeFromMiningFields(miningSchema, miningField.getName().getValue());
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).isPresent();
            OP_TYPE expected = OP_TYPE.byName(miningField.getOpType().value());
            assertThat(retrieved.get()).isEqualTo(expected);
        });
    }

    @Test
    public void getOpTypeFromTargets() {
        Optional<OP_TYPE> opType = ModelUtils.getOpTypeFromTargets(null, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        final Targets targets = new Targets();
        opType = ModelUtils.getOpTypeFromTargets(targets, "vsd");
        assertThat(opType).isNotNull();
        assertThat(opType.isPresent()).isFalse();
        IntStream.range(0, 3).forEach(i -> {
            final Target target = getRandomTarget();
            targets.addTargets(target);
        });
        targets.getTargets().forEach(target -> {
            Optional<OP_TYPE> retrieved = ModelUtils.getOpTypeFromTargets(targets, target.getField().getValue());
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).isPresent();
            OP_TYPE expected = OP_TYPE.byName(target.getOpType().value());
            assertThat(retrieved.get()).isEqualTo(expected);
        });
    }

    @Test
    public void getDataTypeFromDerivedFieldsAndDataDictionary() {
        final DataDictionary dataDictionary = new DataDictionary();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
        });
        final List<DerivedField> derivedFields = dataDictionary.getDataFields()
                .stream()
                .map(dataField -> {
                         DerivedField toReturn = new DerivedField();
                         toReturn.setName(FieldName.create("DER_" + dataField.getName().getValue()));
                         DataType dataType = getRandomDataType();
                         while (dataType.equals(dataField.getDataType())) {
                             dataType = getRandomDataType();
                         }
                         toReturn.setDataType(dataType);
                         return toReturn;
                     })
                .collect(Collectors.toList());
        final List<Field<?>> fields = new ArrayList<>();
        dataDictionary.getDataFields().stream()
                .map(Field.class::cast)
                .forEach(fields::add);
        derivedFields.stream()
                    .map(Field.class::cast)
                    .forEach(fields::add);
        dataDictionary.getDataFields().forEach(dataField -> {
            String fieldName = dataField.getName().getValue();
            DataType retrieved = ModelUtils.getDataType(fields, fieldName);
            assertThat(retrieved).isNotNull();
            DataType expected = dataField.getDataType();
            assertThat(retrieved).isEqualTo(expected);
        });
        derivedFields.forEach(derivedField -> {
            String fieldName = derivedField.getName().getValue();
            DataType retrieved = ModelUtils.getDataType(fields, fieldName);
            assertThat(retrieved).isNotNull();
            DataType expected = derivedField.getDataType();
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    public void getDataTypeFromDataDictionary() {
        final DataDictionary dataDictionary = new DataDictionary();
        IntStream.range(0, 3).forEach(i -> {
            final DataField dataField = getRandomDataField();
            dataDictionary.addDataFields(dataField);
        });
        dataDictionary.getDataFields().forEach(dataField -> {
            DATA_TYPE retrieved = ModelUtils.getDATA_TYPE(getFieldsFromDataDictionary(dataDictionary), dataField.getName().getValue());
            assertThat(retrieved).isNotNull();
            DATA_TYPE expected = DATA_TYPE.byName(dataField.getDataType().value());
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getDataTypeNotFound() {
        final DataDictionary dataDictionary = new DataDictionary();
        IntStream.range(0, 3).forEach(i -> {
            String fieldName = "field" +i;
            final DataField dataField = getRandomDataField();
            dataField.setName(FieldName.create(fieldName));
            dataDictionary.addDataFields(dataField);
        });
        ModelUtils.getDATA_TYPE(getFieldsFromDataDictionary(dataDictionary), "NOT_EXISTING");
    }

    @Test
    public void getObjectsFromArray() {
        List<String> values = Arrays.asList("32", "11", "43");
        Array array = getArray(Array.Type.INT, values);
        List<Object> retrieved = ModelUtils.getObjectsFromArray(array);
        assertThat(retrieved).hasSameSizeAs(values);
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertThat(obj).isInstanceOf(Integer.class);
            Integer expected = Integer.valueOf(values.get(i));
            assertThat(obj).isEqualTo(expected);
        }
        values = Arrays.asList("just", "11", "fun");
        array = getArray(Array.Type.STRING, values);
        retrieved = ModelUtils.getObjectsFromArray(array);
        assertThat(retrieved).hasSameSizeAs(values);
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertThat(obj).isInstanceOf(String.class);
            assertThat(obj).isEqualTo(values.get(i));
        }
        values = Arrays.asList("23.11", "11", "123.123");
        array = getArray(Array.Type.REAL, values);
        retrieved = ModelUtils.getObjectsFromArray(array);
        assertThat(retrieved).hasSameSizeAs(values);
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertThat(obj).isInstanceOf(Double.class);
            Double expected = Double.valueOf(values.get(i));
            assertThat(obj).isEqualTo(expected);
        }
    }

    @Test
    public void convertToKieMiningField() {
        final String fieldName = "fieldName";
        final MiningField.UsageType usageType = MiningField.UsageType.ACTIVE;
        final MiningField toConvert = getMiningField(fieldName, usageType);
        toConvert.setOpType(null);
        final DataField dataField = getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING);
        org.kie.pmml.api.models.MiningField retrieved = ModelUtils.convertToKieMiningField(toConvert, dataField);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(fieldName);
        assertThat(retrieved.getUsageType()).isEqualTo(FIELD_USAGE_TYPE.ACTIVE);
        assertThat(retrieved.getDataType()).isEqualTo(DATA_TYPE.STRING);
        assertThat(retrieved.getOpType()).isNull();
        toConvert.setOpType(OpType.CATEGORICAL);
        retrieved = ModelUtils.convertToKieMiningField(toConvert, dataField);
        assertThat(retrieved.getOpType()).isEqualTo(OP_TYPE.CATEGORICAL);
    }

    @Test
    public void convertToKieOutputField() {
        final OutputField toConvert = getRandomOutputField();
        org.kie.pmml.api.models.OutputField retrieved = ModelUtils.convertToKieOutputField(toConvert, null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(toConvert.getName().getValue());
        OP_TYPE expectedOpType = OP_TYPE.byName(toConvert.getOpType().value());
        assertThat(retrieved.getOpType()).isEqualTo(expectedOpType);
        DATA_TYPE expectedDataType = DATA_TYPE.byName(toConvert.getDataType().value());
        assertThat(retrieved.getDataType()).isEqualTo(expectedDataType);
        assertThat(retrieved.getTargetField()).isEqualTo(toConvert.getTargetField().getValue());
        RESULT_FEATURE expectedResultFeature = RESULT_FEATURE.byName(toConvert.getResultFeature().value());
        assertThat(retrieved.getResultFeature()).isEqualTo(expectedResultFeature);
        toConvert.setOpType(null);
        toConvert.setTargetField(null);
        retrieved = ModelUtils.convertToKieOutputField(toConvert, null);
        assertThat(retrieved.getOpType()).isNull();
        assertThat(retrieved.getTargetField()).isNull();
    }

    @Test
    public void getBoxedClassNameByParameterFields() {
        List<ParameterField> parameterFields = getParameterFields();
        parameterFields.forEach(parameterField -> {
            String retrieved = ModelUtils.getBoxedClassName(parameterField);
            commonVerifyEventuallyBoxedClassName(retrieved, parameterField.getDataType());
        });
    }

    @Test
    public void getBoxedClassNameByDataTypes() {
        List<DataType> dataTypes = getDataTypes();
        dataTypes.forEach(dataType -> {
            String retrieved = ModelUtils.getBoxedClassName(dataType);
            commonVerifyEventuallyBoxedClassName(retrieved, dataType);
        });
    }

    @Test
    public void getRowDataMap() {
        Row source = getRandomRowWithCells();
        Map<String, Object> retrieved = ModelUtils.getRowDataMap(source);
        InputCell inputCell = source.getContent().stream()
                .filter(InputCell.class::isInstance)
                .map(InputCell.class::cast)
                .findFirst()
                .get();
        OutputCell outputCell = source.getContent().stream()
                .filter(OutputCell.class::isInstance)
                .map(OutputCell.class::cast)
                .findFirst()
                .get();
        assertThat(retrieved).hasSize(2);
        String expected = getPrefixedName(inputCell.getName());
        assertThat(retrieved).containsKey(expected);
        assertThat(retrieved.get(expected)).isEqualTo(inputCell.getValue());

        expected = getPrefixedName(outputCell.getName());
        assertThat(retrieved).containsKey(expected);
        assertThat(retrieved.get(expected)).isEqualTo(outputCell.getValue());
    }

    private void commonVerifyEventuallyBoxedClassName(String toVerify, DataType dataType) {
        assertThat(toVerify).isEqualTo(expectedBoxedClassName.get(dataType.value()));
    }
}