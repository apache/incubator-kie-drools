/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test.dsl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.spi.PropagationContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.verification.VerificationMode;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;

public class ObjectSinkStep
    implements
    Step {

    public ObjectSinkStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        if ( args.size() != 0 ) {
            String[] a = args.get( 0 );
            String name = a[0].trim();
            String leftInput = a[1].trim();

            ObjectSource objectSource = (ObjectSource) context.get( leftInput );

            ObjectSink mockedSink = Mockito.mock( ObjectSink.class,
                                                  Mockito.withSettings().extraInterfaces( DSLMock.class ) );

            objectSource.addObjectSink( mockedSink );

            context.put( name,
                         mockedSink );
        } else {
            throw new IllegalArgumentException( "Cannot parse arguments " + args );

        }
    }

    public void process(DslStep step,
                        ObjectSink node,
                        Map<String, Object> context,
                        InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();

            for ( String[] cmd : cmds ) {
                if ( cmd[0].equals( "verify" ) ) {
                    if ( "count".equals( cmd[2] ) ) {
                        verifyCount( step,
                                     node,
                                     wm,
                                     cmd,
                                     context );
                    } else if ( "exec".equals( cmd[2] ) ) {
                        verifyExec( step,
                                    node,
                                    wm,
                                    cmd,
                                    context );
                    } else if ( cmd.length == 3 || cmd.length == 4 ) {
                        verifyExpression( step,
                                          node,
                                          wm,
                                          cmd,
                                          context );
                    } else {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": command '" + cmd[2] + "' does not exist in " + Arrays.toString( cmd ) );
                    }
                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + Arrays.toString( cmd ) );
                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    private void verifyCount(DslStep step,
                             ObjectSink node,
                             InternalWorkingMemory wm,
                             String[] cmd,
                             Map<String, Object> context) throws AssertionError {
        int times = Integer.valueOf( cmd[3] );
        VerificationMode counter;
        if ( times >= 0 ) {
            counter = times( times );
        } else {
            counter = atLeastOnce();
        }
        try {
            ArgumentCaptor<InternalFactHandle> captor = ArgumentCaptor.forClass( InternalFactHandle.class );
            if ( "assert".equals( cmd[1] ) ) {
                verify( node,
                        counter ).assertObject( captor.capture(),
                                                any( PropagationContext.class ),
                                                same( wm ) );
            } else if ( "modify".equals( cmd[1] ) ) {
                verify( node,
                        counter ).modifyObject( captor.capture(),
                                                any( ModifyPreviousTuples.class ),
                                                any( PropagationContext.class ),
                                                same( wm ) );
            } else {
                throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + Arrays.toString( cmd ) );
            }
            String key = getCaptorKey( node,
                                       cmd );
            context.put( key,
                         captor );
        } catch ( MockitoAssertionError e ) {
            AssertionError ae = new AssertionError( "line " + step.getLine() + ": verify failed: " + e.getMessage() );
            ae.setStackTrace( e.getStackTrace() );
            throw ae;
        }
    }

    private String getCaptorKey(ObjectSink node,
                                String[] cmd) {
        return System.identityHashCode( node ) + "." + cmd[1] + ".captor";
    }

    @SuppressWarnings("unchecked")
    private void verifyExpression(DslStep step,
                                  ObjectSink node,
                                  InternalWorkingMemory wm,
                                  String[] cmd,
                                  Map<String, Object> context) throws AssertionError {
        // check that the captor already exists:
        String key = getCaptorKey( node,
                                   cmd );
        ArgumentCaptor<InternalFactHandle> captor = (ArgumentCaptor<InternalFactHandle>) context.get( key );
        if ( captor == null ) {
            // create the captor
            verifyCount( step,
                         node,
                         wm,
                         new String[]{"verify", cmd[1], "count", "-1"},
                         context );
            captor = (ArgumentCaptor<InternalFactHandle>) context.get( key );
        }

        // create a map with all captured tuples as variables
        Map<String, Object> vars = new HashMap<String, Object>();
        int i = 0;
        for ( InternalFactHandle handle : captor.getAllValues() ) {
            vars.put( "handle" + (i++),
                      handle );
        }
        // add all context variables, just in case
        vars.putAll( context );

        // add the static imports for hamcrest matchers
        ParserConfiguration pconf = new ParserConfiguration();
        addStaticImports( pconf,
                          CoreMatchers.class );
        addStaticImports( pconf,
                          JUnitMatchers.class );
        // add import for JUnit assert class
        pconf.addImport( "Assert",
                         Assert.class );

        // compile MVEL expression
        ParserContext mvelctx = new ParserContext( pconf );
        String expression;
        if ( cmd.length == 3 ) {
            expression = "Assert.assertTrue( " + cmd[2].replaceAll( "h(\\d+)",
                                                                    "Handles[$1]" ) + " );";
        } else {
            String val = cmd[2].replaceAll( "h(\\d+)",
                                            "Handles[$1]" );
            String matcher = cmd[3].replaceAll( "h(\\d+)",
                                                "Handles[$1]" );
            expression = "Assert.assertThat( " + val + ", " + matcher + " );";
        }
        try {
            Serializable compiled = MVEL.compileExpression( expression,
                                                            mvelctx );

            // execute the expression
            MVEL.executeExpression( compiled,
                                    vars );
        } catch ( PropertyAccessException e ) {
            String message;
            if ( e.getCause() instanceof InvocationTargetException ) {
                message = ((InvocationTargetException) e.getCause()).getTargetException().toString();
            } else {
                message = e.getMessage();
            }

            Assert.fail( "[ERROR] line " + step.getLine() + " - Executing expression: '" + expression + "'\n" + message );
        }
    }

    @SuppressWarnings("unchecked")
    private void verifyExec(DslStep step,
                            ObjectSink node,
                            InternalWorkingMemory wm,
                            String[] cmd,
                            Map<String, Object> context) throws AssertionError {
        // check that the captor already exists:
        String key = getCaptorKey( node,
                                   cmd );
        ArgumentCaptor<InternalFactHandle> captor = (ArgumentCaptor<InternalFactHandle>) context.get( key );
        if ( captor == null ) {
            // create the captor
            verifyCount( step,
                         node,
                         wm,
                         new String[]{"verify", cmd[1], "count", "-1"},
                         context );
            captor = (ArgumentCaptor<InternalFactHandle>) context.get( key );
        }

        // create a map with all captured tuples as variables
        Map<String, Object> vars = new HashMap<String, Object>();
        int i = 0;
        for ( InternalFactHandle handle : captor.getAllValues() ) {
            vars.put( "handle" + (i++),
                      handle );
        }
        // add all context variables, just in case
        vars.putAll( context );

        // add the static imports for hamcrest matchers
        ParserConfiguration pconf = new ParserConfiguration();
        addStaticImports( pconf,
                          CoreMatchers.class );
        addStaticImports( pconf,
                          JUnitMatchers.class );
        // add import for JUnit assert class
        pconf.addImport( "Assert",
                         Assert.class );

        // compile MVEL expression
        ParserContext mvelctx = new ParserContext( pconf );
        String expression = cmd[3].replaceAll( "h(\\d+)",
                                               "Handles[$1]" );
        try {
            Serializable compiled = MVEL.compileExpression( expression,
                                                            mvelctx );
            // execute the expression
            MVEL.executeExpression( compiled,
                                    vars );
        } catch ( PropertyAccessException e ) {
            String message;
            if ( e.getCause() instanceof InvocationTargetException ) {
                message = ((InvocationTargetException) e.getCause()).getTargetException().toString();
            } else {
                message = e.getMessage();
            }

            Assert.fail( "[ERROR] line " + step.getLine() + " - Executing expression: '" + expression + "'\n" + message );
        }
    }

    private void addStaticImports(ParserConfiguration pconf,
                                  Class< ? > clazz) {
        for ( Method m : clazz.getMethods() ) {
            if ( Modifier.isStatic( m.getModifiers() ) ) {
                pconf.addImport( m.getName(),
                                 m );
            }
        }
    }

}
