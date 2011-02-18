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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

public class FactsStep
    implements
    Step {

    ReteTesterHelper reteTesterHelper;

    public FactsStep(ReteTesterHelper reteTesterHelper) {
        this.reteTesterHelper = reteTesterHelper;
    }

    @SuppressWarnings("unchecked")
    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        if ( args.size() >= 1 ) {

            WorkingMemory wm = (WorkingMemory) context.get( "WorkingMemory" );
            List<FactHandle> handles = (List<FactHandle>) context.get( "Handles" );
            if ( handles == null ) {
                handles = new ArrayList<FactHandle>();
                context.put( "Handles",
                             handles );
            }

            NodeTestCase testCase = (NodeTestCase) context.get( "TestCase" );
            ParserConfiguration conf = new ParserConfiguration();
            for( String imp : testCase.getImports() ) {
                if( imp.endsWith( ".*" ) ) {
                    conf.addPackageImport( imp.substring( 0, imp.lastIndexOf( '.' ) ) );
                } else {
                    try {
                        conf.addImport( imp.substring( imp.lastIndexOf( "." )+1 ), reteTesterHelper.getTypeResolver().resolveType( imp ) );
                    } catch ( ClassNotFoundException e ) {
                        throw new IllegalArgumentException( "Unable to resolve import: "+imp);
                    }
                }
            }
            for ( String[] str : args ) {
                Serializable expr = MVEL.compileExpression( Arrays.asList( str ).toString(), new ParserContext(conf) );
                List< ? > objects = (List< ? >) MVEL.executeExpression( expr );
                for ( Object object : objects ) {
                    FactHandle handle = wm.insert( object );
                    handles.add( handle );
                }
            }
        } else {
            throw new IllegalArgumentException( "Cannot arguments " + Arrays.asList( args ) );
        }
    }
}