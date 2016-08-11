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

package org.kie.dmn.feel.lang.runtime.functions;

import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.runtime.FEELFunction;
import org.kie.dmn.feel.lang.types.FunctionSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class BaseFEELFunction implements FEELFunction {

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private String name;
    private Symbol symbol;

    public BaseFEELFunction( String name ) {
        this.name = name;
        this.symbol = new FunctionSymbol( name, this );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    // TODO: can this method be improved somehow??
    @Override
    public Object applyReflectively(Object[] params) {
        // use reflection to call the appropriate apply method
        try {
            Class[] classes = Stream.of( params ).map( p -> p != null ? p.getClass() : null ).toArray( Class[]::new );
            Method apply = null;
            for( Method m : getClass().getDeclaredMethods() ) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                if( !m.getName().equals( "apply" ) || parameterTypes.length != params.length ) {
                    continue;
                }
                boolean found = true;
                for( int i = 0; i < parameterTypes.length; i++ ) {
                    if ( classes[i] != null && ! parameterTypes[i].isAssignableFrom( classes[i] ) ) {
                        found = false;
                        break;
                    }
                }
                if( found ) {
                    apply = m;
                    break;
                }
            }
            if( apply != null ) {
                Object result = apply.invoke( this, params );
                return result;
            } else {
                String ps = Arrays.toString( classes );
                logger.error( "Unable to find function '" + getName() + "( " + ps.substring( 1, ps.length()-1 ) +" )'" );
            }
        } catch ( Exception e ) {
            logger.error( "Error trying to call function "+getName()+".", e );
        }
        return null;
    }

}
