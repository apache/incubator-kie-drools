/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.drools.base.evaluators.EvaluatorRegistry;

/**
 * DRLExprTreeTest
 */
public class DRLExprTreeTest extends TestCase {

    private DRLExprTree    walker;
    private DRLExpressions parser;

    protected void setUp() throws Exception {
        super.setUp();
        this.walker = null;
        // initializes pluggable operators
        new EvaluatorRegistry();
    }

    protected void tearDown() throws Exception {
        this.walker = null;
        super.tearDown();
    }

    public void testSimpleExpression() throws Exception {
        String source = "a > b @";
        Object result = parse( "conditionalExpression",
                               "expression",
                               source );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
        
        System.out.println( result );

    }

    public void testAndConnective() throws Exception {
        String source = "a > b && 10 != 20 @";
        Object result = parse( "conditionalExpression",
                               "expression",
                               source );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
        
        System.out.println( result );

    }

    public void testConnective2() throws Exception {
        String source = "(a > b || 10 != 20) && someMethod(10) == 20 @";
        Object result = parse( "conditionalExpression",
                               "expression",
                               source );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
        
        System.out.println( result );

    }

    private Object parse( String parserRuleName,
                          String treeRuleName,
                          final String text ) throws Exception {
        return newParser( parserRuleName,
                          treeRuleName,
                          newCharStream( text ) );
    }

    private Object parse( String parserRuleName,
                          String treeRuleName,
                          final String source,
                          final String text ) throws Exception {
        return newParser( parserRuleName,
                          treeRuleName,
                          newCharStream( text ) );
        // this.parser.setSource( source );
    }

    private Reader getReader( final String name ) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );
        return new InputStreamReader( in );
    }

    private Object parseResource( String parserRuleName,
                                  String treeRuleName,
                                  final String name ) throws Exception {

        // System.err.println( getClass().getResource( name ) );
        final Reader reader = getReader( name );

        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return parse( parserRuleName,
                      treeRuleName,
                      name,
                      text.toString() );
    }

    private CharStream newCharStream( final String text ) {
        return new ANTLRStringStream( text );
    }

    private Object newParser( String parserRuleName,
                              String treeRuleName,
                              final CharStream charStream ) {
        return execTreeParser( parserRuleName,
                               treeRuleName,
                               charStream );
    }

    public Object execTreeParser( String testRuleName,
                                  String testTreeRuleName,
                                  CharStream charStream ) {
        Object treeRuleReturn = null;
        try {
            DRLLexer lexer = new DRLLexer( charStream );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            RecognizerSharedState state = new RecognizerSharedState();
            ParserHelper helper = new ParserHelper( tokens,
                                                    state );
            parser = new DRLExpressions( tokens,
                                         state,
                                         helper );
            parser.setTreeAdaptor( new DroolsTreeAdaptor() );
            /** Use Reflection to get rule method from parser */
            Method ruleName = Class.forName( "org.drools.lang.DRLExpressions" ).getMethod( testRuleName );

            /** Invoke grammar rule, and get the return value */
            Object ruleReturn = ruleName.invoke( parser );

            if ( !parser.hasErrors() ) {
                Class< ? > _return = Class.forName( "org.drools.lang.DRLExpressions" + "$" + testRuleName + "_return" );
                Method returnName = _return.getMethod( "getTree" );
                DroolsTree tree = (DroolsTree) returnName.invoke( ruleReturn );

                // Walk resulting tree; create tree nodes stream first
                CommonTreeNodeStream nodes = new CommonTreeNodeStream( tree );
                // AST nodes have payload that point into token stream
                nodes.setTokenStream( tokens );
                // Create a tree walker attached to the nodes stream
                this.walker = new DRLExprTree( nodes );
                this.walker.setHelper( helper );
                /** Invoke the tree rule, and store the return value if there is */
                Method treeRuleName = Class.forName( "org.drools.lang.DRLExprTree" ).getMethod( testTreeRuleName );
                treeRuleReturn = treeRuleName.invoke( walker );
            } else {
                System.out.println( parser.getErrorMessages() );
            }

            if ( treeRuleReturn != null ) {
                /** If return object is instanceof AST, get the toStringTree */
                if ( treeRuleReturn.toString().indexOf( testTreeRuleName + "_return" ) > 0 ) {
                    try { // NullPointerException may happen here...
                        Class< ? > _treeReturn = Class.forName( "org.drools.lang.DRLExprTree" + "$" + testTreeRuleName + "_return" );
                        Field[] fields = _treeReturn.getDeclaredFields();
                        for ( Field field : fields ) {
                            if ( field.getType().getName().contains( "org.drools.lang.descr." ) ) {
                                return field.get( treeRuleReturn );
                            }
                        }
                    } catch ( Exception e ) {
                        System.err.println( e );
                    }
                }
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( SecurityException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return treeRuleReturn;
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

}
