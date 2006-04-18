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



import org.drools.spi.Evaluator;

/**
 * BaseEvaluator is an Object Comparator that is operator aware
 * 
 * @author mproctor
 * 
 */
public abstract class BaseEvaluator
    implements
    Evaluator {

    private final int operator;

    private final int type;

    public BaseEvaluator(int type,
                         int operator) {
        this.type = type;
        this.operator = operator;
    }

    public int getOperator() {
        return this.operator;
    }

    public int getType() {
        return this.type;
    }

    public abstract boolean evaluate(Object object1,
                                     Object object2);

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !this.getClass().equals( other.getClass() ) ) {
            return false;
        }
        return (this.getOperator() == ((Evaluator) other).getOperator()) && (this.getType() == ((Evaluator) other).getType());
    }

    public int hashCode() {
        return (this.getType() * 17) ^ (this.getOperator() * 11) ^ (this.getClass().hashCode());
    }

}