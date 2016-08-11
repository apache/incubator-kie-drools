/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class QualifiedNameNode
        extends BaseNode {

    private static final Logger logger = LoggerFactory.getLogger( QualifiedNameNode.class );

    private List<NameRefNode> parts;

    public QualifiedNameNode(ParserRuleContext ctx, List<NameRefNode> parts) {
        super( ctx );
        this.parts = parts;
    }

    public List<NameRefNode> getParts() {
        return parts;
    }

    public void setParts(List<NameRefNode> parts) {
        this.parts = parts;
    }

    public String[] getPartsAsStringArray() {
        return parts.stream().map( p -> p.getText() ).toArray( String[]::new );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        Object current = parts.get( 0 ).evaluate( ctx );
        try {
            if( current != null ) {
                for( int i = 1; i < parts.size(); i++ ) {
                    String n = parts.get( i ).getText();
                    if( current == null ) {
                        return null;
                    } else if( current instanceof Context ) {
                        current = ((Context)current).getEntry( n );
                    } else if( current instanceof Map ) {
                        current = ((Map)current).get( n );
                    } else {
                        Method getter = getAccessor( current.getClass(), n );
                        current = getter.invoke( current );
                    }
                }
                return current;
            }
        } catch ( Exception e ) {
            logger.error("Error accessing qualified name: "+ getText(), e );
            return null;
        }
        return null;
    }


    public static Method getAccessor(Class<?> clazz, String field) {
        try {
            return clazz.getMethod("get" + ucFirst(field));
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(field);
            } catch (NoSuchMethodException e1) {
                try {
                    return clazz.getMethod("is" + ucFirst(field));
                } catch (NoSuchMethodException e2) {
                    return null;
                }
            }
        }
    }

    public static String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

}
