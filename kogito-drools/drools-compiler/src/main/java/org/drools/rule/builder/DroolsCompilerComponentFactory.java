package org.drools.rule.builder;


import org.drools.base.FieldDataFactory;
import org.drools.base.FieldFactory;
import org.drools.lang.MVELDumper;
import org.drools.lang.ExpressionRewriter;

public class DroolsCompilerComponentFactory {

    private ConstraintBuilderFactory constraintBuilderFactory = new DefaultConstraintBuilderFactory();

    public ConstraintBuilderFactory getConstraintBuilderFactoryService() {
        return constraintBuilderFactory;
    }

    public void setConstraintBuilderFactoryProvider( ConstraintBuilderFactory provider ) {
        constraintBuilderFactory = provider;
    }

    public void setDefaultConstraintBuilderFactoryProvider() {
        constraintBuilderFactory = new DefaultConstraintBuilderFactory();
    }




    private ExpressionRewriter expressionProcessor = new MVELDumper();

    public ExpressionRewriter getExpressionProcessor() {
        return expressionProcessor;
    }

    public void setExpressionProcessor( ExpressionRewriter provider ) {
        expressionProcessor = provider;
    }

    public void setDefaultExpressionProcessor() {
        expressionProcessor = new MVELDumper();
    }



    private FieldDataFactory fieldFactory = FieldFactory.getInstance();

    public FieldDataFactory getFieldFactory() {
        return fieldFactory;
    }

    public void setFieldDataFactory( FieldDataFactory provider ) {
        fieldFactory = provider;
    }

    public void setDefaultFieldDataFactory() {
        fieldFactory = FieldFactory.getInstance();
    }


}
