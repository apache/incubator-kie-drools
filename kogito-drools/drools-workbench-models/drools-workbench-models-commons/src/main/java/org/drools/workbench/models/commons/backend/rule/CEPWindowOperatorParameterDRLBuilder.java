/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.rule;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class to build DRL for CEP 'window' operators
 */
public class CEPWindowOperatorParameterDRLBuilder
        implements
        OperatorParameterDRLBuilder {

    public StringBuilder buildDRL( Map<String, String> parameters ) {
        TreeMap<Integer, String> sortedParams = new TreeMap<Integer, String>();
        for ( Map.Entry<String, String> e : parameters.entrySet() ) {
            String key = e.getKey();
            String value = e.getValue();

            try {
                int i = Integer.parseInt( key );
                sortedParams.put( Integer.valueOf( i ),
                                  value );
            } catch ( NumberFormatException nfe ) {
            }
        }

        StringBuilder b = new StringBuilder();
        if ( sortedParams.size() == 0 ) {
            return b;
        }

        b.append( " (" );
        for ( Map.Entry<Integer, String> e : sortedParams.entrySet() ) {
            b.append( e.getValue() );
            b.append( ", " );
        }
        if ( b.lastIndexOf( "," ) != -1 ) {
            b.delete( b.lastIndexOf( "," ),
                      b.length() );
        }
        b.append( ")" );

        return b;
    }

}
