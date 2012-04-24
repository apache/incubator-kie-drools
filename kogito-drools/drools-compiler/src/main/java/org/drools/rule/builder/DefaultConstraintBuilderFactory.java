package org.drools.rule.builder;


public class DefaultConstraintBuilderFactory implements ConstraintBuilderFactory {

    private static ConstraintBuilder cBuilder = new MVELConstraintBuilder();

    public ConstraintBuilder newConstraintBuilder() {
        return cBuilder;
    }
}