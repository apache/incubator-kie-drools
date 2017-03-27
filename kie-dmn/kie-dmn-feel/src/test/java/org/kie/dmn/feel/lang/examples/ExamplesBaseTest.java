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

package org.kie.dmn.feel.lang.examples;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class ExamplesBaseTest {
    public static final  String DEFAULT_IDENT = "    ";

    private static final Logger logger = LoggerFactory.getLogger( ExamplesBaseTest.class );

    protected static String loadExpression(String fileName) {
        try {
            return new String( Files.readAllBytes( Paths.get( ExamplesTest.class.getResource( fileName ).toURI() ) ) );
        } catch ( Exception e ) {
            logger.error( "Error reading file " + fileName, e );
            Assert.fail( "Error reading file " + fileName);
        }
        return null;
    }

    protected String printContext(Map context) {
        return printContext( context, "" );
    }

    private String printContext(Map<String, Object> context, String ident ) {
        StringBuilder builder = new StringBuilder(  );
        builder.append( "{\n" );
        for( Map.Entry e : context.entrySet() ) {
            builder.append( ident )
                    .append( DEFAULT_IDENT )
                    .append( e.getKey() )
                    .append( ": " );
            if( e.getValue() instanceof Map ) {
                builder.append( printContext( (Map<String, Object>) e.getValue(), ident + DEFAULT_IDENT ) );
            } else {
                builder.append( e.getValue() )
                        .append( "\n" );
            }
        }
        builder.append( ident+"}\n" );
        return builder.toString();
    }
}
