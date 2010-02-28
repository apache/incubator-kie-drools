/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.reteoo.test.dsl;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.List;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.ClassTypeResolver;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.base.evaluators.Operator;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public class ReteTesterHelper {

    private Package                 pkg;
    private ClassFieldAccessorStore store;
    private EvaluatorRegistry       registry = new EvaluatorRegistry();
    private final ClassTypeResolver typeResolver;

    public ReteTesterHelper() {
        this.pkg = new Package( "org.drools.examples.manners" );
        this.pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        this.store = this.pkg.getClassFieldAccessorStore();
        this.store.setEagerWire( true );
        this.typeResolver = new ClassTypeResolver( new HashSet<String>(),
                                                   getClass().getClassLoader() );
    }

    public Package getPkg() {
        return pkg;
    }

    public ClassFieldAccessorStore getStore() {
        return store;
    }

    public EvaluatorRegistry getRegistry() {
        return registry;
    }
    
    public ClassTypeResolver getTypeResolver() {
        return typeResolver;
    }

    public BetaNodeFieldConstraint getBoundVariableConstraint(final Pattern pattern,
                                                              final String fieldName,
                                                              final Declaration declaration,
                                                              final String evaluatorString) throws IntrospectionException {
        final Class< ? > clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName,
                                                                getClass().getClassLoader() );

        Evaluator evaluator = getEvaluator( clazz,
                                            evaluatorString );

        return new VariableConstraint( extractor,
                                       declaration,
                                       evaluator );
    }

    public AlphaNodeFieldConstraint getLiteralConstraint(final Pattern pattern,
                                                         final String fieldName,
                                                         final String evaluatorString,
                                                         final String value ) throws IntrospectionException {
        final Class< ? > clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                fieldName,
                                                                getClass().getClassLoader() );

        Evaluator evaluator = getEvaluator( clazz,
                                            evaluatorString );
        
        FieldValue fieldValue = FieldFactory.getFieldValue( value, 
                                                            extractor.getValueType(), 
                                                            null );

        return new LiteralConstraint( extractor,
                                      evaluator,
                                      fieldValue );
    }

    public Evaluator getEvaluator(Class< ? > cls,
                                  String operator) {
        return registry.getEvaluator( ValueType.determineValueType( cls ),
                                      Operator.determineOperator( operator,
                                                                  false ) );
    }

    public Pattern getPattern(int index,
                              String type) throws ClassNotFoundException {
        Pattern pattern = new Pattern( index,
                                       new ClassObjectType( typeResolver.resolveType( type ) ) );
        return pattern;
    }

    public void addImports(List<String> imports) {
        typeResolver.clearImports();
        for( String importEntry : imports ) {
            typeResolver.addImport( importEntry );
        }
    }

}