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
package org.drools.scenariosimulation.api.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * It describes how to reach a single property of a fact
 */
public class FactMapping {

    /**
     * Expression to reach the property. I.e. person.fullName.last
     */
    private List<ExpressionElement> expressionElements = new LinkedList<>();

    /**
     * Identifier of this expression (it contains the type of expression, i.e. given/expected) - it is mapped to the <b>property</b> header
     */
    private ExpressionIdentifier expressionIdentifier;

    /**
     * Identify the fact by name and class name - it is mapped to <b>Instance</b> header
     */
    private FactIdentifier factIdentifier;

    /**
     * String name of the type of the property described by this class
     */
    private String className;

    /**
     * Alias to customize the <b>instance</b> label
     */
    private String factAlias;

    /**
     * Alias to customize the <b>property</b> label
     */
    private String expressionAlias;

    /**
     * Generic type(s) of the given properties, where applicable (ex collections)
     */
    private List<String> genericTypes;

    /**
     * It defines the FactMappingValueType. <b>FrontEnd scoped</b>
     */
    private FactMappingValueType factMappingValueType = FactMappingValueType.NOT_EXPRESSION;

    /**
     * The <b>width</b> of the FactMapping column. <b>FrontEnd scoped</b>
     */
    private Double columnWidth;

    public FactMapping() {
    }

    public FactMapping(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        this(factIdentifier.getName(), factIdentifier, expressionIdentifier);
    }

    public FactMapping(String factAlias, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        this.factAlias = factAlias;
        this.expressionIdentifier = expressionIdentifier;
        this.className = factIdentifier.getClassName();
        this.factIdentifier = factIdentifier;
    }

    /**
     * It <b>clones</b> the given <code>FactMapping</code>
     * @param original The original <code>FactMapping</code>
     */
    private FactMapping(FactMapping original) {
        original.expressionElements.forEach(expressionElement -> this.addExpressionElement(expressionElement.getStep(), original.className));
        this.expressionIdentifier = original.expressionIdentifier;
        this.factIdentifier = original.factIdentifier;
        this.className = original.className;
        this.factAlias = original.factAlias;
        this.expressionAlias = original.expressionAlias;
        this.genericTypes = original.genericTypes;
        this.factMappingValueType = original.factMappingValueType;
        this.columnWidth = original.columnWidth;
    }

    public String getFullExpression() {
        return expressionElements.stream().map(ExpressionElement::getStep).collect(Collectors.joining("."));
    }

    public List<ExpressionElement> getExpressionElementsWithoutClass() {
        if (expressionElements.isEmpty()) {
            throw new IllegalStateException("ExpressionElements malformed");
        }
        return expressionElements.subList(1, expressionElements.size());
    }

    public List<ExpressionElement> getExpressionElements() {
        return expressionElements;
    }

    public void addExpressionElement(String stepName, String className) {
        this.className = className;
        expressionElements.add(new ExpressionElement(stepName));
    }

    public String getClassName() {
        return className;
    }

    public ExpressionIdentifier getExpressionIdentifier() {
        return expressionIdentifier;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public String getFactAlias() {
        return factAlias;
    }

    public void setFactAlias(String factAlias) {
        this.factAlias = factAlias;
    }

    public String getExpressionAlias() {
        return expressionAlias;
    }

    public void setExpressionAlias(String expressionAlias) {
        this.expressionAlias = expressionAlias;
    }

    public List<String> getGenericTypes() {
        return genericTypes;
    }

    public void setGenericTypes(List<String> genericTypes) {
        this.genericTypes = genericTypes;
    }

    /**
     * It creates a new <code>FactMapping</code> cloning the instanced one.
     */
    public FactMapping cloneFactMapping() {
        return new FactMapping(this);
    }

    public static String getPlaceHolder(FactMappingType factMappingType) {
        return factMappingType.name();
    }

    public static String getPlaceHolder(FactMappingType factMappingType, int index) {
        return getPlaceHolder(factMappingType) + " " + index;
    }

    public FactMappingValueType getFactMappingValueType() {
        return factMappingValueType;
    }

    public void setFactMappingValueType(FactMappingValueType factMappingValueType) {
        this.factMappingValueType = factMappingValueType;
    }

    public static String getInstancePlaceHolder(int index) {
        return "INSTANCE " + index;
    }

    public static String getPropertyPlaceHolder(int index) {
        return "PROPERTY " + index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactMapping that = (FactMapping) o;
        return getExpressionElements().equals(that.getExpressionElements()) &&
                Objects.equals(getExpressionIdentifier(), that.getExpressionIdentifier()) &&
                Objects.equals(getFactIdentifier(), that.getFactIdentifier()) &&
                Objects.equals(getClassName(), that.getClassName()) &&
                Objects.equals(getFactAlias(), that.getFactAlias()) &&
                Objects.equals(getExpressionAlias(), that.getExpressionAlias()) &&
                Objects.equals(getGenericTypes(), that.getGenericTypes()) &&
                Objects.equals(getFactMappingValueType(), that.getFactMappingValueType()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getExpressionElements(),
                getExpressionIdentifier(),
                getFactIdentifier(),
                getClassName(),
                getFactAlias(),
                getExpressionAlias(),
                getGenericTypes(),
                getFactMappingValueType());
    }

    public Double getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Double columnWidth) {
        this.columnWidth = columnWidth;
    }
}
