/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.dialect.java;

import java.util.ArrayList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.base.ClassObjectType;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.semantics.java.builder.AccumulateBuilder;
import org.drools.semantics.java.builder.BuildContext;
import org.drools.semantics.java.builder.BuildUtils;
import org.drools.semantics.java.builder.ColumnBuilder;
import org.drools.semantics.java.builder.ConditionalElementBuilder;

/**
 * @author etirelli
 *
 */
public class JavaAccumulateBuilder
    implements
    ConditionalElementBuilder, AccumulateBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.AccumulateBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(BuildContext context,
                                    BuildUtils utils,
                                    ColumnBuilder columnBuilder,
                                    BaseDescr descr) {

        AccumulateDescr accumDescr = (AccumulateDescr) descr;

        Column sourceColumn = columnBuilder.build( context,
                                                   utils,
                                                   accumDescr.getSourceColumn() );

        if ( sourceColumn == null ) {
            return null;
        }

        Column resultColumn = columnBuilder.build( context,
                                                   utils,
                                                   accumDescr.getResultColumn() );

        final String className = "accumulate" + context.getNextId();
        accumDescr.setClassMethodName( className );

        final List[] usedIdentifiers1 = utils.getUsedIdentifiers( context,
                                                                  accumDescr,
                                                                  accumDescr.getInitCode() );
        final List[] usedIdentifiers2 = utils.getUsedIdentifiers( context,
                                                                  accumDescr,
                                                                  accumDescr.getActionCode() );
        final List[] usedIdentifiers3 = utils.getUsedIdentifiers( context,
                                                                  accumDescr,
                                                                  accumDescr.getResultCode() );

        final List requiredDeclarations = new ArrayList( usedIdentifiers1[0] );
        requiredDeclarations.addAll( usedIdentifiers2[0] );
        requiredDeclarations.addAll( usedIdentifiers3[0] );

        final List requiredGlobals = new ArrayList( usedIdentifiers1[1] );
        requiredGlobals.addAll( usedIdentifiers2[1] );
        requiredGlobals.addAll( usedIdentifiers3[1] );

        final Declaration[] declarations = new Declaration[requiredDeclarations.size()];
        for ( int i = 0, size = requiredDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) context.getDeclarationResolver().getDeclaration( (String) requiredDeclarations.get( i ) );
        }
        final Declaration[] sourceDeclArr = (Declaration[]) sourceColumn.getOuterDeclarations().values().toArray( new Declaration[0] );

        final String[] globals = (String[]) requiredGlobals.toArray( new String[requiredGlobals.size()] );

        StringTemplate st = utils.getRuleGroup().getInstanceOf( "accumulateMethod" );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           globals );

        st.setAttribute( "innerDeclarations",
                         sourceDeclArr );
        st.setAttribute( "methodName",
                         className );

        final String initCode = accumDescr.getInitCode();
        final String actionCode = accumDescr.getActionCode();
        final String resultCode = accumDescr.getResultCode();
        st.setAttribute( "initCode",
                         initCode );
        st.setAttribute( "actionCode",
                         actionCode );
        st.setAttribute( "resultCode",
                         resultCode );

        String resultType = null;
        // TODO: Need to change this... 
        if ( resultColumn.getObjectType() instanceof ClassObjectType ) {
            resultType = ((ClassObjectType) resultColumn.getObjectType()).getClassType().getName();
        } else {
            resultType = resultColumn.getObjectType().getValueType().getClassType().getName();
        }

        st.setAttribute( "resultType",
                         resultType );

        context.getMethods().add( st.toString() );

        st = utils.getInvokerGroup().getInstanceOf( "accumulateInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         utils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           (String[]) requiredGlobals.toArray( new String[requiredGlobals.size()] ) );

        st.setAttribute( "hashCode",
                         actionCode.hashCode() );

        Accumulate accumulate = new Accumulate( sourceColumn,
                                                resultColumn,
                                                declarations,
                                                sourceDeclArr );
        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   st.toString() );
        context.getInvokerLookups().put( invokerClassName,
                                         accumulate );
        context.getDescrLookups().put( invokerClassName,
                                       accumDescr );
        return accumulate;
    }

}
