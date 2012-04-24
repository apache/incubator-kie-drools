package org.drools.rule.builder;


import org.drools.lang.MVELDumper;
import org.drools.lang.ExpressionRewriter;
import org.drools.util.ServiceRegistryImpl;

public class DroolsCompilerComponentFactory {

    private static ConstraintBuilderFactory constraintBuilderFactory = new DefaultConstraintBuilderFactory();

    public static ConstraintBuilderFactory getConstraintBuilderFactoryService() {
        return constraintBuilderFactory;
    }

    public static void setConstraintBuilderFactoryProvider( ConstraintBuilderFactory provider ) {
        DroolsCompilerComponentFactory.constraintBuilderFactory = provider;
    }

    public static void setDefaultConstraintBuilderFactoryProvider() {
        DroolsCompilerComponentFactory.constraintBuilderFactory = new DefaultConstraintBuilderFactory();
    }




    private static ExpressionRewriter expressionProcessor = new MVELDumper();

    public static ExpressionRewriter getExpressionProcessor() {
        return expressionProcessor;
    }

    public static void setExpressionProcessor( ExpressionRewriter provider ) {
        DroolsCompilerComponentFactory.expressionProcessor = provider;
    }

    public static void setDefaultExpressionProcessor() {
        DroolsCompilerComponentFactory.expressionProcessor = new MVELDumper();
    }



}
