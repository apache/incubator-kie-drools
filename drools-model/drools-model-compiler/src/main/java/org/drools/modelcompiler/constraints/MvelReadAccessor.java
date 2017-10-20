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

package org.drools.modelcompiler.constraints;

import java.io.Serializable;

import org.drools.core.base.ValueType;
import org.drools.core.base.extractors.BaseObjectClassFieldReader;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.InternalReadAccessor;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class MvelReadAccessor extends BaseObjectClassFieldReader implements InternalReadAccessor {

    private final Serializable expression;

    public MvelReadAccessor( Class<?> thisType, Class<?> fieldType, String expr ) {
        this(0, thisType, fieldType, expr);
    }

    public MvelReadAccessor( int index, Class<?> thisType, Class<?> fieldType, String expr ) {
        super(index, fieldType, ValueType.determineValueType( fieldType ));

        ParserContext parserContext = new ParserContext();
        parserContext.setStrictTypeEnforcement(true);
        parserContext.setStrongTyping(true);
        parserContext.addInput("this", thisType);
        this.expression = MVEL.compileExpression(expr, parserContext);
    }

    @Override
    public Object getValue( InternalWorkingMemory workingMemory, Object object ) {
        return MVEL.executeExpression(expression, object);
    }
}
