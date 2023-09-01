package org.drools.scenariosimulation.api.model;

import java.util.List;
import java.util.Objects;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * FactMappingValue contains the identifier of a fact mapping + the raw value
 * - collectionPathToValue: In case of an error occurred in a collection (List or Map), it contains the path which
 *                          describes how to reach the wrong field (eg. Item #1 | field | ... ).
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
    private List<String> collectionPathToValue;
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

    public List<String> getCollectionPathToValue() {
        return collectionPathToValue;
    }

    public void setCollectionPathToValue(List<String> collectionPathToValue) {
        this.collectionPathToValue = collectionPathToValue;
        this.status = FactMappingValueStatus.FAILED_WITH_ERROR;
    }

    public void resetStatus() {
        this.status = FactMappingValueStatus.SUCCESS;
        this.exceptionMessage = null;
        this.errorValue = null;
        this.collectionPathToValue = null;
    }
}
