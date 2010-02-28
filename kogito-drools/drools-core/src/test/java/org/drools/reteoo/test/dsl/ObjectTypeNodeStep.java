/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.reteoo.test.dsl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.builder.BuildContext;

public class ObjectTypeNodeStep
    implements
    Step {

    public ObjectTypeNodeStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
        String name;
        String type;

        if ( args.size() == 1 ) {
            String[] c = args.get( 0 );
            name = c[0].trim();
            type = c[1].trim();
        } else {
            throw new IllegalArgumentException( "Cannot execute arguments " + args );
        }
        ObjectTypeNode otn;
        try {
            EntryPointNode epn = new EntryPointNode( buildContext.getNextId(),
                                                     buildContext.getRuleBase().getRete(),
                                                     buildContext );
            epn.attach();

            otn = new ObjectTypeNode( buildContext.getNextId(),
                                      epn,
                                      new ClassObjectType( Class.forName( type ) ),
                                      buildContext );
            // we don't attach, as we want to manually propagate and not
            // have the working memory propagate
            //otn.attach();
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Cannot create OTN " + Arrays.asList( args,
                                                                                      e ) );
        }
        context.put( name,
                     otn );
    }

}