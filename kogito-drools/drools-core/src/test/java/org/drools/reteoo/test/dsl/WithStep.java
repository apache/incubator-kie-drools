/**
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.test.dsl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.mvel2.MVEL;

public class WithStep
    implements
    Step {

    public WithStep(ReteTesterHelper reteTesterHelper) {
    }

    @SuppressWarnings("unchecked")
    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put( "h",
                  handles );

        if ( args.size() >= 1 ) {
            for ( String[] str : args ) {
                String handle = fixHandles( str[0] );
                StringBuilder builder = new StringBuilder();
                builder.append( "with( " + handle + ".object ) { " );
                for ( int i = 1; i < str.length; i++ ) {
                    if ( i > 1 ) {
                        builder.append( ", " );
                    }
                    builder.append( fixHandles( str[i] ) );
                }
                builder.append( "}" );
                try {
                    MVEL.eval( builder.toString(),
                               vars );
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "Cannot execute arguments: " + Arrays.toString( str ) );
                }
            }
        } else {
            throw new IllegalArgumentException( "Cannot execute arguments " + args );
        }
    }

    private String fixHandles(String p) {
        return p.replaceAll( "h(\\d+)",
                             "h[$1]" ).trim();
    }
}