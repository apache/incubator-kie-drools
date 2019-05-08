/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.api.model;

import java.util.Objects;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * FactMappingValue contains the identifier of a fact mapping + the raw value
 */
public class FactMappingValue {

    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private Object rawValue;
    @XStreamOmitField
    private FactMappingValueStatus status = FactMappingValueStatus.SUCCESS;
    @XStreamOmitField
    private Object errorValue;
    @XStreamOmitField
    private String exceptionMessage;

    public FactMappingValue() {
    }

    public FactMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, Object rawValue) {
        this.factIdentifier = Objects.requireNonNull(factIdentifier, "FactIdentifier has to be not null");
        this.expressionIdentifier = Objects.requireNonNull(expressionIdentifier, "ExpressionIdentifier has to be not null");
        this.rawValue = rawValue;
    }

    public void setRawValue(Object rawValue) {
        this.rawValue = rawValue;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public ExpressionIdentifier getExpressionIdentifier() {
        return expressionIdentifier;
    }

    public Object getRawValue() {
        return rawValue;
    }

    FactMappingValue cloneFactMappingValue() {
        FactMappingValue cloned = new FactMappingValue();
        cloned.expressionIdentifier = expressionIdentifier;
        cloned.factIdentifier = factIdentifier;
        cloned.rawValue = rawValue;
        return cloned;
    }

    public FactMappingValueStatus getStatus() {
        return status;
    }

    public Object getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(Object errorValue) {
        this.errorValue = errorValue;
        this.status = FactMappingValueStatus.FAILED_WITH_ERROR;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        this.status = FactMappingValueStatus.FAILED_WITH_EXCEPTION;
    }

    public void resetStatus() {
        this.status = FactMappingValueStatus.SUCCESS;
        this.exceptionMessage = null;
        this.errorValue = null;
    }
}
