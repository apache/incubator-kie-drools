/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class TestUtil {

    public static void assertContainsLinesInOrder( final String text,
                                                   final String... strings ) {

        final Iterator<String> theseAreTheLinesYouAreLookingFor = Arrays.asList( strings )
                .iterator();

        String thisIsTheLineYouAreLookingFor = theseAreTheLinesYouAreLookingFor.next();

        for ( final String line : text.split( "\n" ) ) {

            if ( line.trim()
                    .equals( thisIsTheLineYouAreLookingFor ) ) {
                if ( theseAreTheLinesYouAreLookingFor.hasNext() ) {
                    thisIsTheLineYouAreLookingFor = theseAreTheLinesYouAreLookingFor.next();
                } else {
                    break;
                }
            }
        }

        assertFalse( "Could not find " + thisIsTheLineYouAreLookingFor,
                     theseAreTheLinesYouAreLookingFor.hasNext() );
    }

    public static String loadResource( final String name ) throws
                                                           Exception {
        final InputStream in = TestUtil.class.getResourceAsStream( name );
        final Reader reader = new InputStreamReader( in );
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[1024];
        int len = 0;
        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

}
