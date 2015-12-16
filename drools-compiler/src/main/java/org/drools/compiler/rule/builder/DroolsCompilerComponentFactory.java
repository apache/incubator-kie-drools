/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder;


import org.drools.core.base.FieldDataFactory;
import org.drools.core.base.FieldFactory;
import org.drools.compiler.lang.MVELDumper;
import org.drools.compiler.lang.ExpressionRewriter;

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
