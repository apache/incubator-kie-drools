package org.drools.semantics.java;
/*
 * Copyright 2005 JBoss Inc
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



import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.And;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.GroupElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Query;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.TypeResolver;

public class FunctionBuilder {
    private static StringTemplateGroup functionGroup = new StringTemplateGroup( new InputStreamReader( FunctionBuilder.class.getResourceAsStream( "javaFunction.stg" ) ),
                                                                                AngleBracketTemplateLexer.class );

    public FunctionBuilder() {

    }

    public String build(Package pkg,
                        FunctionDescr functionDescr) {
        StringTemplate st = functionGroup.getInstanceOf( "function" );

        st.setAttribute( "package",
                         pkg.getName() );

        st.setAttribute( "imports",
                         pkg.getImports() );

        st.setAttribute( "className",
                         ucFirst( functionDescr.getName() ) );
        st.setAttribute( "methodName",
                         functionDescr.getName() );
        
        st.setAttribute( "returnType",
                         functionDescr.getReturnType() );        

        st.setAttribute( "parameterTypes",
                         functionDescr.getParameterTypes() );

        st.setAttribute( "parameterNames",
                         functionDescr.getParameterNames() );

        st.setAttribute( "text",
                         functionDescr.getText() );
        
        return st.toString();

    }

    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }
}