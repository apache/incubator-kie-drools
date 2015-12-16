/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.evaluators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.core.base.ValueType;
import org.drools.core.common.DroolsObjectInput;
import org.drools.core.spi.Evaluator;

/**
 * A registry class for all available evaluators
 */
public class EvaluatorRegistry
    implements
    Externalizable {

    private static final long                serialVersionUID = 510l;

    private Map<String, EvaluatorDefinition> evaluators;
    private ClassLoader                      classloader;

    /**
     * Default constructor. The registry will use the context classloader (if available)
     * to load the evaluator definition classes or this class classloader if it is
     * not available.
     */
    public EvaluatorRegistry() {
        this( null );
    }

    /**
     * Creates a new EvaluatorRegistry using the given classloader to load
     * the evaluator definition classes.
     *
     * @param classloader the classloader to use to load evaluator definition
     *                    classes. If it is null, try to obtain the context
     *                    classloader. If it is also null, uses the same classloader
     *                    that loaded this class.
     */
    public EvaluatorRegistry(ClassLoader classloader) {
        this.evaluators = new HashMap<String, EvaluatorDefinition>();
        if ( classloader != null ) {
            this.classloader = classloader;
        } else {
            this.classloader = getDefaultClassLoader();
        }

        // loading default built in evaluators
        for (EvaluatorDefinition evaluatorDefinition : BuiltInEvaluatorDefinitions.getEvaluatorDefinitions()) {
            this.addEvaluatorDefinition( evaluatorDefinition );
        }
    }

    /**
     * Return the set of registered keys.
     * @return a Set of Strings
     */
    public Set<String> keySet() {
        return evaluators.keySet();
    }

    @SuppressWarnings("unchecked")
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        evaluators = (Map<String, EvaluatorDefinition>) in.readObject();
        if ( in instanceof DroolsObjectInput ) {
            classloader = ((DroolsObjectInput) in).getClassLoader();
        } else {
            classloader = getDefaultClassLoader();
        }
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( evaluators );
    }

    private static ClassLoader getDefaultClassLoader() {
        if ( Thread.currentThread().getContextClassLoader() != null ) return Thread.currentThread().getContextClassLoader();
        return EvaluatorRegistry.class.getClassLoader();
    }

    /**
     * Adds an evaluator definition class to the registry using the
     * evaluator class name. The class will be loaded and the corresponting
     * evaluator ID will be added to the registry. In case there exists
     * an implementation for that ID already, the new implementation will
     * replace the previous one.
     *
     * @param className the name of the class for the implementation definition.
     *                  The class must implement the EvaluatorDefinition interface.
     *
     * @return true if the new class implementation is replacing an old
     *         implementation for the same evaluator ID. False otherwise.
     */
    @SuppressWarnings("unchecked")
    public void addEvaluatorDefinition( String className ) {
        try {
            Class<EvaluatorDefinition> defClass = (Class<EvaluatorDefinition>) this.classloader.loadClass( className );
            EvaluatorDefinition def = defClass.newInstance();
            addEvaluatorDefinition( def );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Class not found for evaluator definition: " + className,
                                        e );
        } catch ( InstantiationException e ) {
            throw new RuntimeException( "Error instantiating class for evaluator definition: " + className,
                                        e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( "Illegal access instantiating class for evaluator definition: " + className,
                                        e );
        }
    }

    /**
     * Adds an evaluator definition class to the registry. In case there exists
     * an implementation for that evaluator ID already, the new implementation will
     * replace the previous one.
     *
     * @param def the evaluator definition to be added.
     */
    public void addEvaluatorDefinition( EvaluatorDefinition def ) {
        for ( String id : def.getEvaluatorIds() ) {
            this.evaluators.put( id,
                                 def );
        }
    }

    /**
     * Returns the evaluator definition for the given evaluator ID
     * or null if no one was found
     *
     * @param evaluatorId
     * @return
     */
    public EvaluatorDefinition getEvaluatorDefinition( String evaluatorId ) {
        return this.evaluators.get( evaluatorId );
    }

    /**
     * Returns the evaluator definition for the given operator
     * or null if no one was found
     *
     * @param operator the operator implemented by the evaluator definition
     * @return
     */
    public EvaluatorDefinition getEvaluatorDefinition( Operator operator ) {
        return this.evaluators.get( operator.getOperatorString() );
    }

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operatorId the string identifier of the evaluator
     *
     * @param isNegated true if the evaluator instance to be returned is
     *                  the negated version of the evaluator.
     *
     * @param parameterText some evaluators support parameters and these
     *                      parameters are defined as a String that is
     *                      parsed by the evaluator itself.
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator( ValueType type,
                                   String operatorId,
                                   boolean isNegated,
                                   String parameterText ) {
        return this.getEvaluatorDefinition( operatorId ).getEvaluator( type,
                                                                       operatorId,
                                                                       isNegated,
                                                                       parameterText );
    }

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operator the operator that evaluator implements
     *
     * @param parameterText some evaluators support parameters and these
     *                      parameters are defined as a String that is
     *                      parsed by the evaluator itself.
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator( ValueType type,
                                   Operator operator,
                                   String parameterText ) {
        return this.getEvaluatorDefinition( operator ).getEvaluator( type,
                                                                     operator,
                                                                     parameterText );
    }

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operator the operator that evaluator implements
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator( ValueType type,
                                   Operator operator ) {
        return this.getEvaluatorDefinition( operator ).getEvaluator( type,
                                                                     operator );
    }
}
