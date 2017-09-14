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

package org.drools.modelcompiler.builder.generator;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Pattern;
import org.drools.core.util.ClassUtils;
import org.drools.modelcompiler.builder.generator.ModelGenerator.RuleContext;

public class MvelParseUtil {

    public static TypedExpression toTypedExpression( RuleContext context, Pattern pattern, AtomicExprDescr atomicExprDescr,
                                                     Set<String> usedDeclarations, Set<String> reactOnProperties ) {
        if ( atomicExprDescr.isLiteral() ) {
            return new TypedExpression( atomicExprDescr.getExpression(), Optional.empty());
        } else {
            String expression = atomicExprDescr.getExpression();
            String[] parts = expression.split("\\.");
            StringBuilder telescoping = new StringBuilder();
            boolean implicitThis = true;
            Class<?> typeCursor = ( (ClassObjectType) pattern.getObjectType() ).getClassType();
            for ( int idx = 0; idx < parts.length ; idx++ ) {
                String part = parts[idx];
                boolean isGlobal = false;
                if ( isGlobal ) {
                    implicitThis = false;
                    telescoping.append( part );
                } else if ( idx == 0 && context.declarations.containsKey(part) ) {
                    implicitThis = false;
                    usedDeclarations.add( part );
                    telescoping.append( part );
                } else {
                    if ( ( idx == 0 && implicitThis ) || ( idx == 1 && implicitThis == false ) ) {
                        reactOnProperties.add(part);
                    }
                    Method accessor = ClassUtils.getAccessor( typeCursor, part );
                    typeCursor = accessor.getReturnType();
                    telescoping.append( "." + accessor.getName() + "()" );
                }
            }
            return new TypedExpression( implicitThis ? "_this" + telescoping.toString() : telescoping.toString(), Optional.of( typeCursor ));
        }
    }

}
