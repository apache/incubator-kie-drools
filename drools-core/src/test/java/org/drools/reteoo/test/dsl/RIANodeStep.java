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

import java.util.List;
import java.util.Map;

import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.builder.BuildContext;
import org.mockito.Mockito;

public class RIANodeStep
    implements
    Step {

    public RIANodeStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
        String name;
        String source;

        if ( args.size() == 1 ) {
            String[] c = args.get( 0 );
            name = c[0].trim();
            source = c[1].trim();
        } else {
            throw new IllegalArgumentException( "Cannot execute arguments " + args );
        }
        LeftTupleSource pnode = null;
        if( source.equals( "mock" ) ) {
            pnode = Mockito.mock( LeftTupleSource.class );
        } else {
            pnode = (LeftTupleSource) context.get( source );
        }

        RightInputAdapterNode riaNode = new RightInputAdapterNode( buildContext.getNextId(),
                                                                 pnode,
                                                                 buildContext );
        riaNode.attach();
        context.put( name,
                     riaNode );
    }
}