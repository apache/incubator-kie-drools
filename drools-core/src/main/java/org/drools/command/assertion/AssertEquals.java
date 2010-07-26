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

package org.drools.command.assertion;

import java.util.HashMap;
import java.util.Map;

import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.core.util.StringUtils;
import org.mvel2.MVEL;

public class AssertEquals
    implements
    GenericCommand<Void> {

    private String  message;
    private Object  expectedObject;
    private String  expectedIdentifier;

    private Command command;
    private String  mvelString;

    public AssertEquals(String message,
                        Object expectedObject,
                        Command command,
                        String mvelString) {
        this.message = message;
        this.expectedObject = expectedObject;
        this.command = command;
        this.mvelString = mvelString;
    }

    public AssertEquals(String message,
                        String expectedIdentifier,
                        Command command,
                        String mvelString) {
        this.message = message;
        this.expectedIdentifier = expectedIdentifier;
        this.command = command;
        this.mvelString = mvelString;
    }

    public Void execute(Context context) {
        Object actualObject = ((GenericCommand) command).execute( context );

        if ( this.mvelString != null ) {
            actualObject = MVEL.eval( this.mvelString,
                                      actualObject );
        }

        if ( this.expectedIdentifier != null ) {
            this.expectedObject = context.get( this.expectedIdentifier );
        }

        Map vars = new HashMap();
        vars.put( "expected",
                  expectedObject );
        vars.put( "actual",
                  actualObject );

        if ( ((Boolean) MVEL.eval( "expected != actual",
                                   vars )) ) {
            throw new AssertionError( format( this.message,
                                              expectedObject,
                                              actualObject ) );
        }

        //        Assert.assertTrue( this.message,
        //                           (Boolean) MVEL.eval( "expected == actual",
        //                                                vars ) );

        return null;
    }

    static String format(String message,
                         Object expected,
                         Object actual) {
        StringBuilder builder = new StringBuilder();

        if ( !StringUtils.isEmpty( message ) ) {
            builder.append( message );
            builder.append( " " );
        }
        String expectedString = String.valueOf( expected );
        String actualString = String.valueOf( actual );
        if ( expectedString.equals( actualString ) ) {
            builder.append( "expected: " );
            builder.append( formatClassAndValue( expected,
                                                 expectedString ) );

            builder.append( " but was: " );
            builder.append( formatClassAndValue( actual,
                                                 actualString ) );
        } else {
            builder.append( "expected:<" );
            builder.append( expectedString );
            builder.append( "> but was:<" );
            builder.append( actualString );
        }

        return builder.toString();
    }

    private static String formatClassAndValue(Object value,
                                              String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    public String toString() {
        return "assert";
    }

}
