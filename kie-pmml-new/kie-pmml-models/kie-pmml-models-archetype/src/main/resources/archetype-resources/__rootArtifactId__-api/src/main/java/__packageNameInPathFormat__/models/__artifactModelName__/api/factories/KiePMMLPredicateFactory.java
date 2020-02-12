import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.True;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
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
        package ${package}.models.tree.api.factories;
        {package}.api.exceptions.KieDataFieldException;
        {package}.api.exceptions.KiePMMLException;
        {package}.api.model.enums.DATA_TYPE;
        {package}.models.tree.api.model.enums.BOOLEAN_OPERATOR;
        {package}.models.tree.api.model.enums.OPERATOR;
        {package}.models.tree.api.model.predicates.KiePMMLCompoundPredicate;
        {package}.models.tree.api.model.predicates.KiePMMLFalsePredicate;
        {package}.models.tree.api.model.predicates.KiePMMLPredicate;
        {package}.models.tree.api.model.predicates.KiePMMLSimplePredicate;
        {package}.models.tree.api.model.predicates.KiePMMLTruePredicate;{package}.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;

public class KiePMMLPredicateFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateFactory.class.getName());

    private KiePMMLPredicateFactory() {
    }

    public static List<KiePMMLPredicate> getPredicates(List<Predicate> predicates, DataDictionary dataDictionary) throws KiePMMLException {
        logger.info("getPredicates {}", predicates);
        return predicates.stream().map(throwingFunctionWrapper(predicate -> getPredicate(predicate, dataDictionary))).collect(Collectors.toList());
    }

    public static KiePMMLPredicate getPredicate(Predicate predicate, DataDictionary dataDictionary) throws KiePMMLException {
        logger.info("getPredicate {}", predicate);
        if (predicate instanceof SimplePredicate) {
            final DataType dataType = dataDictionary.getDataFields().stream()
                    .filter(dataField -> dataField.getName().getValue().equals(((SimplePredicate) predicate).getField().getValue()))
                    .map(DataField::getDataType)
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLException("Failed to find DataField for predicate " + ((SimplePredicate) predicate).getField().getValue()));
            return getKiePMMLSimplePredicate((SimplePredicate) predicate, dataType);
        } else if (predicate instanceof CompoundPredicate) {
            return getKiePMMLCompoundPredicate((CompoundPredicate) predicate, dataDictionary);
        } else if (predicate instanceof True) {
            return getKiePMMLTruePredicate((True) predicate);
        } else if (predicate instanceof False) {
            return getKiePMMLFalsePredicate((False) predicate);
        } else {
            throw new KiePMMLException("Predicate of type " + predicate.getClass().getName() + " not managed, yet");
        }
    }

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(SimplePredicate predicate, DataType dataType) throws KiePMMLException {
        return KiePMMLSimplePredicate.builder(predicate.getField().getValue(), Collections.emptyList(), OPERATOR.byName(predicate.getOperator().value()))
                .withValue(getActualValue(predicate.getValue(), dataType))
                .build();
    }

    public static KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(CompoundPredicate predicate, DataDictionary dataDictionary) throws KiePMMLException {
        return KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.byName(predicate.getBooleanOperator().value()))
                .withKiePMMLPredicates(getPredicates(predicate.getPredicates(), dataDictionary))
                .build();
    }

    public static KiePMMLTruePredicate getKiePMMLTruePredicate(True predicate) throws KiePMMLException {
        return KiePMMLTruePredicate.builder(Collections.emptyList())
                .build();
    }

    public static KiePMMLFalsePredicate getKiePMMLFalsePredicate(False predicate) throws KiePMMLException {
        return KiePMMLFalsePredicate.builder(Collections.emptyList())
                .build();
    }

    private static Object getActualValue(Object rawValue, DataType dataType) throws KiePMMLException {
        DATA_TYPE data_type = DATA_TYPE.byName(dataType.value());
        final Class<?> mappedClass = data_type.getMappedClass();
        if (mappedClass.isAssignableFrom(rawValue.getClass())) {
            // No cast/transformation needed
            return rawValue;
        }
        if (rawValue instanceof String) {
            String stringValue = (String) rawValue;
            try {
                switch (data_type) {
                    case STRING:
                        return stringValue;
                    case INTEGER:
                        return Integer.parseInt(stringValue);
                    case FLOAT:
                        return Float.parseFloat(stringValue);
                    case DOUBLE:
                        return Double.parseDouble(stringValue);
                    case BOOLEAN:
                        return Boolean.parseBoolean(stringValue);
                    case DATE:
                        return LocalDate.parse(stringValue);
                    case TIME:
                        return LocalTime.parse(stringValue);
                    case DATE_TIME:
                        return LocalDateTime.parse(stringValue);
                    case DATE_DAYS_SINCE_0:
                    case DATE_DAYS_SINCE_1960:
                    case DATE_DAYS_SINCE_1970:
                    case DATE_DAYS_SINCE_1980:
                    case TIME_SECONDS:
                    case DATE_TIME_SECONDS_SINCE_0:
                    case DATE_TIME_SECONDS_SINCE_1960:
                    case DATE_TIME_SECONDS_SINCE_1970:
                    case DATE_TIME_SECONDS_SINCE_1980:
                        return Long.parseLong(stringValue);
                }
            } catch (Exception e) {
                throw new KieDataFieldException("Fail to convert " + rawValue + "[" + rawValue.getClass().getName() + "] to expected class " + mappedClass.getName(), e);
            }
        }
        throw new KieDataFieldException("Unexpected " + rawValue + "[" + rawValue.getClass().getName() + "] to convert");
    }
}
