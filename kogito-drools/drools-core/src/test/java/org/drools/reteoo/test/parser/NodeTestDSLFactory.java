/*
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

package org.drools.reteoo.test.parser;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.drools.reteoo.test.dsl.DslStep;
import org.drools.reteoo.test.dsl.NodeTestDef;
import org.drools.reteoo.test.dsl.NodeTestCase;

/**
 * A factory used by the DSL parser
 * 
 * @author etirelli
 */
public class NodeTestDSLFactory {
    
    public static enum Context { 
        SETUP, TEARDOWN, TEST;
    }

    private NodeTestCase testCase;
    private NodeTestDef test;
    private DslStep step;
    private Context current;
    
    public NodeTestCase createTestCase( String name ) {
        testCase = new NodeTestCase( cleanString( name ) );
        return testCase;
    }
    
    public void createSetup() {
        current = Context.SETUP;
    }
    
    public void createTearDown() {
        current = Context.TEARDOWN;
    }

    public void createTest( CommonTree testToken, CommonTree name ) {
        current = Context.TEST;
        test = new NodeTestDef( cleanString( name.getText() ), testToken.getLine() );
        testCase.addTest( test );
    }
    
    @SuppressWarnings("unchecked")
    public void createStep( CommonTree ctx, List<CommonTree> params ) {
        step = new DslStep( ctx.getLine(), ctx.getText() );
        for( CommonTree param : params ) {
            String[] cmds = new String[param.getChildCount()];
            int i = 0;
            for( CommonTree child : (List<CommonTree>) param.getChildren() ) {
                cmds[ i++ ] = child.getText();
            }
            step.addCommand( cmds );
        }
        switch( current ) { 
            case SETUP : 
                testCase.addSetupStep( step );
                break;
            case TEARDOWN :
                testCase.addTearDownStep( step );
                break;
            case TEST :
                test.addStep( step );
                break;
        }
    }
    
    private String cleanString( String text ) {
        return ( text != null && text.length() > 2 ) ? text.substring( 1, text.length()-1 ) : "";
    }
    
}
