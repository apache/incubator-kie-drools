package org.drools.scenariosimulation.api.model;

/**
    It assigns a Type to the <code>FactMapping</code> value. Used <b>ONLY</b> in FrontEnd side, to determine if a
    column value should be managed as an Expression.
    - EXPRESSION: The value associated with the Fact is of Expression type.
    - NOT_EXPRESSION: The specific value-type is defined by its content.
 */
public enum FactMappingValueType {
    EXPRESSION,
    NOT_EXPRESSION
}
