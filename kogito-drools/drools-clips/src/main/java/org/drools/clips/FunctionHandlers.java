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

package org.drools.clips;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.descr.FunctionDescr;

public class FunctionHandlers {
    public static final FunctionHandlers INSTANCE = new FunctionHandlers();
    
    public static FunctionHandlers getInstance() {
        return INSTANCE;
    }
    
    private Map<String, Function> map = new HashMap<String, Function>();
    
    private FunctionHandlers() {
        
    }
    
    public Function getFunction(String name) {
        return this.map.get( name );
    }
    
    public void registerFunction(Function function) {
        this.map.put( function.getName(), function );
    }
    public static void dump(SExpression sExpression, Appendable appendable) {
        dump(sExpression,
             appendable,
             false);
    }
    public static void dump(SExpression sExpression, Appendable appendable, boolean root) {              
        if ( sExpression instanceof LispAtom ) {
            appendable.append( ( ( LispAtom ) sExpression).getValue() );
        } else {
            LispForm form = (LispForm) sExpression;
            String functionName =  ( (LispAtom) form.getSExpressions()[0]).getValue();
            Function function = FunctionHandlers.getInstance().getFunction( functionName );
            if ( function != null ) {
                function.dump(form, appendable );                
            } else if ( form.getSExpressions()[0] instanceof VariableLispAtom ){
                // try and execute this as a java call
                function = FunctionHandlers.getInstance().getFunction( "call" );
                function.dump(form, appendable );                
            } else {
                // execute as user function
                appendable.append( functionName + "(" );
                for ( int i = 1, length = form.getSExpressions().length; i < length; i++ ) {
                    dump( form.getSExpressions()[i], appendable, false );
                    if ( i < length -1 ) {
                        appendable.append( ", " );
                    }
                }
                appendable.append( ")" );                
            }
        }    
        if ( root ) {
            appendable.append( ";\n" );
        }
    }
    
    public static FunctionDescr createFunctionDescr(SExpression name, LispForm params, List<SExpression> content) {
        FunctionDescr descr = new FunctionDescr(((LispAtom)name).getValue(), "Object" );
        for ( SExpression sExpr  : params.getSExpressions() ) {
            String param = ((LispAtom)sExpr).getValue().trim();
            if ( param.charAt( 0 ) == '"' ) {
                param = param.substring( 1 );
            }
            
            if ( param.charAt( param.length()-1 ) == '"' ) {
                param = param.substring( 0, param.length()-1 );
            }
            descr.addParameter(  "Object",  param );
        }
        
        descr.setBody( content.toString() );
        
        return descr;
    }
    
}
