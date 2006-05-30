package org.drools.base;

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

import org.drools.base.evaluators.ArrayFactory;
import org.drools.base.evaluators.BooleanFactory;
import org.drools.base.evaluators.ByteFactory;
import org.drools.base.evaluators.CharacterFactory;
import org.drools.base.evaluators.DateFactory;
import org.drools.base.evaluators.DoubleFactory;
import org.drools.base.evaluators.FloatFactory;
import org.drools.base.evaluators.IntegerFactory;
import org.drools.base.evaluators.LongFactory;
import org.drools.base.evaluators.ObjectFactory;
import org.drools.base.evaluators.ShortFactory;
import org.drools.base.evaluators.StringFactory;
import org.drools.spi.Evaluator;

/**
 * This is a factory to generate evaluators for all types and operations that can be used 
 * in constraints. Uses the helper classes in the evaluators sub package.
 * 
 * eg Person(object<type> opr object)
 * Where valid type's are are defined as contants in Evaluator.
 * 
 * Adding support for other types or operators is quite easy, just remmeber to add it to the getEvaluator method(s)
 * below, and also to the unit tests.
 * TODO: Should this be made dynamic, such that users can add their own types, a-la hibernate?
 */
public class EvaluatorFactory {
    private static final EvaluatorFactory INSTANCE = new EvaluatorFactory();

    public static EvaluatorFactory getInstance() {
        return EvaluatorFactory.INSTANCE;
    }

    private EvaluatorFactory() {

    }

    public static Evaluator getEvaluator(final int type,
                                         final String operator) {
        Evaluator evaluator = null;
        if ( operator.equals( "==" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.EQUAL );
        } else if ( operator.equals( "!=" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.NOT_EQUAL );
        } else if ( operator.equals( "<" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.LESS );
        } else if ( operator.equals( "<=" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.LESS_OR_EQUAL );
        } else if ( operator.equals( ">" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.GREATER );
        } else if ( operator.equals( ">=" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.GREATER_OR_EQUAL );
        } else if ( operator.equals( "contains" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.CONTAINS );
        } else if ( operator.equals( "matches" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.MATCHES );
        } else if ( operator.equals( "excludes" ) ) {
            evaluator = getEvaluator( type,
                                      Evaluator.EXCLUDES );
        } else {
            throw new IllegalArgumentException( "Unknown operator: '" + operator + "'" );
        }

        return evaluator;
    }

    public static Evaluator getEvaluator(final int type,
                                         final int operator) {
        switch ( type ) {
            case Evaluator.STRING_TYPE :
                return StringFactory.getStringEvaluator( operator );
            case Evaluator.OBJECT_TYPE :
                return ObjectFactory.getObjectEvaluator( operator );
            case Evaluator.SHORT_TYPE :
                return ShortFactory.getShortEvaluator( operator );
            case Evaluator.INTEGER_TYPE :
                return IntegerFactory.getIntegerEvaluator( operator );
            case Evaluator.BOOLEAN_TYPE :
                return BooleanFactory.getBooleanEvaluator( operator );
            case Evaluator.DOUBLE_TYPE :
                return DoubleFactory.getDoubleEvaluator( operator );
            case Evaluator.CHAR_TYPE :
                return CharacterFactory.getCharacterEvaluator( operator );
            case Evaluator.BYTE_TYPE :
                return ByteFactory.getByteEvaluator( operator );
            case Evaluator.FLOAT_TYPE :
                return FloatFactory.getFloatEvaluator( operator );
            case Evaluator.LONG_TYPE :
                return LongFactory.getLongEvaluator( operator );
            case Evaluator.DATE_TYPE :
                return DateFactory.getDateEvaluator( operator );
            case Evaluator.ARRAY_TYPE :
                return ArrayFactory.getArrayEvaluator( operator );
            default :
                throw new RuntimeException( "Type '" + type + "' does not exist for BaseEvaluatorFactory" );
        }
    }

}