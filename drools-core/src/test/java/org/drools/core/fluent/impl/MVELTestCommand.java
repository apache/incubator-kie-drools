/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.fluent.impl;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class MVELTestCommand implements GenericCommand<Void>  {

    public static final String MVEL_HEADER = "MVEL_HEADER";

    private String              headerText = "";
    private String              text;
    private String              reason;
    
    public MVELTestCommand() {
        
    }
    
    public String getHeaderText() {
        return headerText;
    }
    
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Void execute(Context context) {
        //ParserContext ctx = new Parser
        
        ParserContext parserCtx = new ParserContext( );
        String t = headerText + text;
        MVEL.compileExpression( t, parserCtx );
        
        Map<String, Class> inputs = parserCtx.getInputs();
                
        Map<String, Object> vars = new HashMap<String, Object>();
        
        for ( String name : inputs.keySet() ) {
            vars.put( name, context.get( name ) );
        }
        
        if ( ! (( Boolean ) MVEL.eval( headerText + text, vars )).booleanValue() ) {
            fail( text + "\n" + reason );
        }
        return null;
    }

}
