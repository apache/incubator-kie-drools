package org.drools.base.evaluators;

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

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.DoubleFactory.DoubleEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleGreaterEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleGreaterOrEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleLessEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleLessOrEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleNotEqualEvaluator;
import org.drools.spi.Evaluator;

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * @author Michael Neale
 */
public class FactTemplateFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new FactTemplateFactory();
    
    private FactTemplateFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new FactTemplateFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return FactTemplateEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return FactTemplateNotEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for FactTemplateEvaluator" );
        }    
    }

    static class FactTemplateEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FactTemplateEqualEvaluator();

        private FactTemplateEqualEvaluator() {
            super( ValueType.FACTTEMPLATE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
        }

        public String toString() {
            return "FactTemplate ==";
        }
    }

    static class FactTemplateNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FactTemplateNotEqualEvaluator();

        private FactTemplateNotEqualEvaluator() {
            super( ValueType.FACTTEMPLATE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return !(object2 == null);
            }

            return !object1.equals( object2 );
        }

        public String toString() {
            return "FactTemplate !=";
        }
    }
}