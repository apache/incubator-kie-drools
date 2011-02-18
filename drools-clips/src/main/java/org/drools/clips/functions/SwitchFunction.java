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

package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class SwitchFunction implements Function {
    private static String name = "switch";
    
    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "switchvar = " );
        LispForm expr = ( LispForm ) sExpressions[1];
        if ( expr.getSExpressions().length > 1 ) {
            FunctionHandlers.dump( expr, appendable );
        } else {
            FunctionHandlers.dump( expr.getSExpressions()[0], appendable );
        }
        appendable.append( ";\n" );
        
        LispForm caseForm = ( LispForm ) sExpressions[2];
        
        appendable.append( "if ( switchvar == " );
        
        FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable );
        appendable.append( ") {" );
        for ( int j = 3, jlength = caseForm.getSExpressions().length; j < jlength; j++ ) {
            FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, true );
        }
        appendable.append( "}" );
        
        for ( int i = 3, length = sExpressions.length-1; i < length; i++ ) {
            caseForm = ( LispForm ) sExpressions[i];
            
            appendable.append( " else if ( switchvar == " );
            FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable );
            appendable.append( ") {" );
            
            for ( int j = 3, jlength = caseForm.getSExpressions().length; j < jlength; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, true );
            }
            appendable.append( "}" );
        }
        
        caseForm = ( LispForm ) sExpressions[ sExpressions.length-1 ];
        if ( "case".equals( ((LispAtom)caseForm.getSExpressions()[0]).getValue() ) ) {
            appendable.append( " else if ( switchvar == " );
            FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable );
            appendable.append( ") {" );
            for ( int j = 3, length = caseForm.getSExpressions().length; j < length; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, true );
            }
            appendable.append( "}" );
        } else {
            appendable.append( " else { " );
            for ( int j = 1, length = caseForm.getSExpressions().length; j < length; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, true );
            }
            appendable.append( "}" );
        }
    }
    
}
